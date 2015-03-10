package de.uniwue.info6.comparator;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  SqlQueryComparator.java
 * ************************************************************************
 * %%
 * Copyright (C) 2014 - 2015 Institute of Computer Science, University of Wuerzburg
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.sql.Connection;
import java.util.LinkedList;

import com.akiban.sql.parser.SQLParserException;

import de.uniwue.info6.parser.errors.Error;
import de.uniwue.info6.parser.errors.JavaError;
import de.uniwue.info6.parser.errors.ResultError;
import de.uniwue.info6.parser.errors.SemanticError;
import de.uniwue.info6.parser.exceptions.ParserException;
import de.uniwue.info6.parser.structures.Structure;
import de.uniwue.info6.parser.visitors.RootVisitor;
import de.uniwue.info6.webapp.session.SessionListener;

import static de.uniwue.info6.misc.properties.PropertiesFile.DEF_LANGUAGE;
import de.uniwue.info6.misc.properties.Cfg;


/**
 *
 * @author Christian
 */
public class SqlQueryComparator {

  private SqlQuery userQuery;
  private SqlQuery solutionQuery;
  LinkedList<SqlQuery> solutionQueries;

  private SqlExecuter executer;

  private RootVisitor userVisitor;
  private RootVisitor solutionVisitor;

  private boolean visitorBased = false;
  private String msgPrefix = "";

  private LinkedList<Error> messages = new LinkedList<Error>();
  private LinkedList<RefLink> refLinks = new LinkedList<RefLink>();

  public SqlQueryComparator(RootVisitor userVisitor, RootVisitor solutionVisitor) {
    this.userVisitor = userVisitor;
    this.solutionVisitor = solutionVisitor;
    this.visitorBased = true;
  }

  public SqlQueryComparator(SqlQuery userQuery, SqlQuery solutionQuery, SqlExecuter executer) {
    this.userQuery = userQuery;
    this.solutionQuery = solutionQuery;
    this.executer = executer;
  }

  public SqlQueryComparator(SqlQuery userQuery, LinkedList<SqlQuery> solutionQueries, SqlExecuter executer) {
    this.userQuery = userQuery;
    this.solutionQueries = solutionQueries;
    this.executer = executer;
  }

  /**
   *
   *
   * @return
   */
  //thisQuery = user, another = solution
  public LinkedList<Error> compare() {

    try {

      if (solutionQueries != null && solutionQuery == null) {
        int minDist = 9999;
        for (SqlQuery tmpQuery : solutionQueries) {
          int curDist = LevenshteinDistance.computeLevenshteinDistance(tmpQuery.getPlainContent().toLowerCase(),
                        userQuery.getPlainContent().toLowerCase());
          if (curDist < minDist) {
            solutionQuery = tmpQuery;
            minDist = curDist;
          }
        }
      }

      if (visitorBased) {
        try {
          compareStatical();
        } catch (Exception e) {
          e.printStackTrace();
          messages.add(new JavaError("", e.getMessage()));
        }
        return messages;
      }

      SqlExecuter ex = null;
      @SuppressWarnings("unused")
      Connection connection = null;

      // Step 1
      // Ist der Query ausführbar?
      ex = executer;
      ex.execute(userQuery);
      ex.execute(solutionQuery);

      if (userQuery.getError() != null) {
        messages.add(userQuery.getError());
      }

      if (messages.size() > 0) // Kompilierungsfehler?
        return messages;

      // Step 2
      // Stimmen die Ergebnisse der Queries überein?
      double startTime = System.nanoTime();

      compareDynamical();

      double endTime = System.nanoTime();

      String statsString = "compare_dyn\t" + ((endTime - startTime) / 1000000);
      SessionListener.setExecuterStat(statsString);

      // Step 3
      // Syntaktische Unterschiede feststellen

      try {

        startTime = System.nanoTime();

        compareStatical();

        endTime = System.nanoTime();

        statsString = "compare_stat\t" + ((endTime - startTime) / 1000000);
        SessionListener.setExecuterStat(statsString);

      } catch (SQLParserException e2) {
        messages.add(new JavaError("", e2.getMessage()));
      } catch (Exception e) {
        messages.add(new JavaError("", e.getMessage()));
      }


    } catch (Exception e) {
      messages.add(new JavaError("", e.getMessage()));
    }
    return messages;
  }

  /**
   *
   *
   *
   * @throws ParserException
   */
  public void compareStatical() throws Exception {

    RootVisitor userParsed = visitorBased ? userVisitor : userQuery.getParsedContent();
    RootVisitor solutionParsed = visitorBased ? solutionVisitor : solutionQuery.getParsedContent();

    if (userParsed == null || solutionParsed == null)
      return;

    //Main Keyword
    if (!userParsed.getMainKeyWord().equals(solutionParsed.getMainKeyWord())) {
      messages.add(new SemanticError(msgPrefix + Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.SEMANTIC_ERROR"),
                                     getStringForKeyWord(1, userParsed.getMainKeyWord(), solutionParsed.getMainKeyWord())));
      return;

    }

    //table from + joins
    compareLists(userParsed.getTables(), solutionParsed.getTables(), "FROM");

    //columns
    compareLists(userParsed.getColumns(), solutionParsed.getColumns(), "COLUMNS");

    //targetcolumns#
    compareLists(userParsed.getTargetColumns(), solutionParsed.getTargetColumns(), "TARGETCOLUMNS");

    //targetcolumns
    compareLists(userParsed.getValueColumns(), solutionParsed.getValueColumns(), "VALUES");

    //conditions where
    if (solutionParsed.getWhereConditions() != null) {
      if (userParsed.getWhereConditions() != null) {
        if (!userParsed.getWhereConditions().equals(solutionParsed.getWhereConditions(), this)) {
          messages.add(new SemanticError(msgPrefix + "WHERE", getStringForKeyWord(1, "WHERE", "")));
        }
      } else {
        messages.add(new SemanticError(msgPrefix + Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.SEMANTIC_ERROR"),
                                       getStringForKeyWord(0, "WHERE", "")));
      }
    } else {
      if (userParsed.getWhereConditions() != null) {
        messages.add(new SemanticError(msgPrefix + Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.SEMANTIC_ERROR"),
                                       getStringForKeyWord(2, "WHERE", "")));
      }
    }

    //conditions heaving
    if (solutionParsed.getHavingConditions() != null) {
      if (userParsed.getHavingConditions() != null) {
        if (!userParsed.getHavingConditions().equals(solutionParsed.getHavingConditions())) {
          messages.add(new SemanticError(msgPrefix + "HAVINTG", getStringForKeyWord(1, "HAVING", "")));
        }
      } else {
        messages.add(new SemanticError(msgPrefix + Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.SEMANTIC_ERROR"),
                                       getStringForKeyWord(0, "HAVING", "")));
      }
    } else {
      if (userParsed.getHavingConditions() != null) {
        messages.add(new SemanticError(msgPrefix + Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.SEMANTIC_ERROR"),
                                       getStringForKeyWord(2, "HAVING", "")));
      }
    }

    //order
    compareLists(userParsed.getOrderBys(), solutionParsed.getOrderBys(), "ORDERBY");

    //groupby
    compareLists(userParsed.getGroupBys(), solutionParsed.getGroupBys(), "GROUPBY");

    if (solutionParsed.getFirstFetch() != userParsed.getFirstFetch())
      messages.add(new SemanticError(msgPrefix + "LIMIT", getStringForKeyWord(1, "LIMIT", "")));

    if (solutionParsed.getNumFetch() != userParsed.getNumFetch())
      messages.add(new SemanticError(msgPrefix + "LIMIT", getStringForKeyWord(2, "LIMIT", "")));
  }

  public LinkedList<Error> getMessages() {
    return messages;
  }

  public LinkedList<RefLink> getRefLinks() {
    return refLinks;
  }

  public void setMessages(LinkedList<Error> messages) {
    this.messages.addAll(messages);
  }

  public boolean compareDynamical() {

    if (visitorBased) {
      messages.add(new ResultError(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.DYN_RESULT"),
                                   Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.DYN_RESULT.FAIL"), false));
      return false;
    }

    try {

      if (userQuery.getError() == null && solutionQuery.getError() == null) {
        SqlResult userResult = userQuery.getResult();
        SqlResult solutionResult = solutionQuery.getResult();
        LinkedList<Error> tmp = userResult.equals(solutionResult);

        if (tmp.size() == 0) {
          messages.add(new ResultError(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.DYN_RESULT"),
                                       Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.DYN_RESULT.SUC"), true));
        } else {
          messages.add(new ResultError(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.DYN_RESULT"),
                                       Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.DYN_RESULT.FAIL"), false));
        }

        setMessages(tmp);

        return tmp.size() > 0;

      } else {
        messages.add(new ResultError(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.DYN_RESULT"),
                                     Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.DYN_RESULT.FAIL"), false));
        return false;
      }
    } catch (Exception e) {
      messages.add(new ResultError(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.DYN_RESULT"),
                                   Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.DYN_RESULT.FAIL"), false));
      return false;
    }
  }

  public SqlQuery getSolutionQuery() {
    return solutionQuery;
  }

  public void setMsgPrefix(String msgPrefix) {
    this.msgPrefix = msgPrefix;
  }

  private void compareLists(LinkedList<? extends Structure> userList, LinkedList<? extends Structure> solList,
                            String keyWord) throws Exception {

    LinkedList<Structure> missings = new LinkedList<Structure>();
    LinkedList<Structure> surplus = new LinkedList<Structure>();
    LinkedList<Structure> mistaken = new LinkedList<Structure>();

    if (solList != null) {
      if (userList != null) {
        @SuppressWarnings("unchecked")
        LinkedList<Structure> tmpList = (LinkedList<Structure>) userList.clone();
        for (Structure item : solList) {
          Structure tmpItem = null;
          for (Structure item2 : tmpList) {
            if (item.equals(item2)) {
              tmpItem = item2;
            }

          }
          if (tmpItem != null) {
            tmpList.remove(tmpItem);
          } else {
            //messages.add(new SemanticError(msgPrefix + keyWord, getStringForKeyWord(1, keyWord, item.toString())));
            missings.add(item);
          }
        }
        for (Structure item2 : tmpList) {
          //messages.add(new SemanticError(msgPrefix + keyWord, getStringForKeyWord(2, keyWord, item2.toString())));
          surplus.add(item2);
        }

        for (Structure item : missings) {

          boolean post = true;

          for (Structure item2 : surplus) {
            //System.out.println("Diff["+item.toString() + "," + item2.toString()+"] = "+LevenshteinDistance.computeLevenshteinDistance(item.toString(),  item2.toString()));
            if (LevenshteinDistance.computeLevenshteinDistance(item.toString(), item2.toString()) < 3) {
              mistaken.add(item2);
              surplus.remove(item2);
              missings.remove(item);
              post = false;
              break;
            }
          }

          if (post)
            messages.add(new SemanticError(msgPrefix + keyWord, getStringForKeyWord(1, keyWord,
                                           item.toString())));

        }

        for (Structure item : surplus) {
          messages.add(new SemanticError(msgPrefix + keyWord,
                                         getStringForKeyWord(2, keyWord, item.toString())));
        }

        for (Structure item : mistaken) {
          messages.add(new SemanticError(msgPrefix + keyWord,
                                         getStringForKeyWord(3, keyWord, item.toString())));
        }

      } else {
        messages.add(new SemanticError(msgPrefix + Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.SEMANTIC_ERROR"),
                                       getStringForKeyWord(0, keyWord, "")));
      }
    }
  }

  private void addRefLink(String keyword) {

    RefLink url = SqlDocLinker.getUrlByKeyword(keyword);

    if (url != null) {

      for (RefLink tmp : refLinks) {
        if (tmp.getUrl() != null && tmp.getUrl().equals(url))
          return;
      }

      refLinks.add(url);

    }

  }

  private String getStringForKeyWord(int num, String keyWord, String customText) {

    addRefLink(keyWord);

    if (num == 0) {
      return fillPropertyString(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.KEYWORD_MISSING"), new String[] { keyWord });
    } else if (num == 3) {
      return fillPropertyString(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.TYPO_ERROR"), new String[] { customText });
    } else if (keyWord.equals("FROM")) {
      if (num == 1)
        return fillPropertyString(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.TABLE_MISSING"), new String[] { customText });
      else if (num == 2)
        return fillPropertyString(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.TABLE_SURPLUS"), new String[] { customText });
    } else if (keyWord.equals("COLUMNS")) {
      if (num == 1)
        return fillPropertyString(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.COLUMN_MISSING"), new String[] { customText });
      else if (num == 2)
        return fillPropertyString(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.COLUMN_SURPLUS"), new String[] { customText });
    } else if (keyWord.equals("TARGETCOLUMNS")) {
      if (num == 1)
        return fillPropertyString(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.TAR_COL_MISSING"), new String[] { customText });
      else if (num == 2)
        return fillPropertyString(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.TAR_COL_SURPLUS"), new String[] { customText });
    } else if (keyWord.equals("VALUES")) {
      if (num == 1)
        return fillPropertyString(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.VAL_COL_MISSING"), new String[] { customText });
      else if (num == 2)
        return fillPropertyString(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.VAL_COL_SURPLUS"), new String[] { customText });
    } else if (keyWord.equals("ORDERBY")) {
      if (num == 1)
        return fillPropertyString(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.ORDERBY_MISSING"), new String[] { customText });
      else if (num == 2)
        return fillPropertyString(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.ORDERBY_SURPLUS"), new String[] { customText });
    } else if (keyWord.equals("GROUPBY")) {
      if (num == 1)
        return fillPropertyString(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.GROUPBY_MISSING"), new String[] { customText });
      else if (num == 2)
        return fillPropertyString(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.GROUPBY_SURPLUS"), new String[] { customText });
    } else if (keyWord.equals("WHERE") || keyWord.equals("HAVING")) {
      if (num == 1)
        return fillPropertyString(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.COND_WRONG"), new String[] { keyWord });
      else if (num == 2)
        return fillPropertyString(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.COND_SURPLUS"), new String[] { keyWord });
    } else if (keyWord.equals("SELECT") || keyWord.equals("INSERT") || keyWord.equals("UPDATE")
               || keyWord.equals("DELETE")) {
      if (num == 1)
        return fillPropertyString(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.KEYWORD_WRONG"), new String[] { keyWord,
                                  customText
                                                                                                             });
    } else if (keyWord.equals("LIMIT")) {
      if (num == 1)
        return Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.LIM_OFFSET_WRONG");
      else if (num == 2)
        return Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.LIM_NUMBER_WRONG");
    }

    return "";

  }

  public String fillProperyString(String varStr) {

    String[] empty = null;
    return fillPropertyString(varStr, empty);

  }

  public String fillPropertyString(String varStr, String[] data) {

    String tmp = /*System.getProperty(*/varStr/*)*/;

    if (data != null) {
      for (String tmpStr : data) {
        tmp = tmp.replaceFirst("%", tmpStr);
      }
    }

    return tmp;

  }

}

