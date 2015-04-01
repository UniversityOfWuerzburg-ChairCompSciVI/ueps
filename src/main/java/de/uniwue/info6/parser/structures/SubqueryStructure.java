package de.uniwue.info6.parser.structures;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  SubqueryStructure.java
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

import java.util.LinkedList;

import de.uniwue.info6.comparator.SqlQueryComparator;
import de.uniwue.info6.parser.visitors.RootVisitor;
import de.uniwue.info6.parser.errors.Error;

import static de.uniwue.info6.misc.properties.PropertiesFile.DEF_LANGUAGE;
import de.uniwue.info6.misc.properties.Cfg;

/**
 * MySQL Subquery
 *
 * @author Christian
 *
 */
public class SubqueryStructure extends Structure{

  private RootVisitor rvisitor;

  public SubqueryStructure(String value, RootVisitor rvisitor) {
    super(value);
    this.setRvisitor(rvisitor);
  }

  @Override
  public String toString(){
    return "(" + rvisitor.toString() + ")";
  }

  @Override
  public boolean equals(Structure anotherStructure) {
    return equals(anotherStructure, null);
  }

  @Override
  public boolean equals(Structure anotherStructure, SqlQueryComparator comparator){

    if(anotherStructure instanceof SubqueryStructure){

      SqlQueryComparator comp = new SqlQueryComparator(rvisitor, ((SubqueryStructure) anotherStructure).getRvisitor());
      comp.setMsgPrefix(Cfg.inst().getProp(DEF_LANGUAGE, "COMPARATOR.SUBQUERY") + ": ");

      try {

        LinkedList<Error> messages = comp.compare();
        if(messages.size() == 0){ // work around
          return true;
        } else {
          if(comparator != null){
            comparator.setMessages(messages);
          }
          return false;
          // die compare mesgs sollten an den main comparator übergeben werden, aber wie ?
        }

      } catch (Exception e) {
        e.printStackTrace();
      }

    }

    return false;

  }

  public RootVisitor getRvisitor() {
    return rvisitor;
  }

  public void setRvisitor(RootVisitor rvisitor) {
    this.rvisitor = rvisitor;
  }

}
