package de.uniwue.info6.comparator;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  SqlQuery.java
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

import com.akiban.sql.StandardException;
import com.akiban.sql.parser.SQLParser;
import com.akiban.sql.parser.StatementNode;

import de.uniwue.info6.parser.errors.SqlError;
import de.uniwue.info6.parser.exceptions.ParserException;
import de.uniwue.info6.parser.structures.JoinTableStructure;
import de.uniwue.info6.parser.structures.TableStructure;
import de.uniwue.info6.parser.visitors.RootVisitor;

/**
 * Kapselt SQL-Query und bietet Schnittstelle zum SQL-Parser.
 * 
 * @author Christian
 * 
 */
public class SqlQuery {

	private String plainContent;
	private RootVisitor parsedContent = null;
	private SqlResult result;
	private LinkedList<TableStructure> tables;
	private SqlError error;

	public SqlQuery(String sqlString) {
		plainContent = adjustPlainString(sqlString);
	}

	public static String adjustPlainString(String plainContent) {
		// Im Folgenden werden einige Zeichenfolgen des Queries ersetzt, da
		// diese zum Probleme beim genutzten SQL-Parser führen.
		plainContent = plainContent.replaceAll("year", "yearx");
		plainContent = plainContent.replaceAll("group", "groupx");
		plainContent = plainContent.replaceAll(";", "");

		return plainContent;

	}

	public static String dejustPlainString(String plainContent) {

		plainContent = plainContent.replaceAll("yearx", "year");
		plainContent = plainContent.replaceAll("groupx", "group");

		return plainContent;

	}

	public String toString() {
		return plainContent;
	}

	public SqlError getError() {
		return error;
	}

	public RootVisitor getParsedContent() throws ParserException,
			StandardException {

		if (parsedContent == null) {

			SQLParser parser = new SQLParser();
			StatementNode data;

			data = parser.parseStatement(plainContent);
			parsedContent = new RootVisitor();

			data.accept(parsedContent);

			tables = parsedContent.getTables();

		}

		return parsedContent;

	}

	public RefLink getRefLink() {

		try {
			return SqlDocLinker.getUrlByKeyword(getParsedContent()
					.getMainKeyWord());
		} catch (ParserException e) {
			return null;
		} catch (StandardException e) {
			return null;
		}

	}

	public String getPlainContent() {
		return plainContent;
	}

	public void setPlainContent(String editedQuery) {
		plainContent = editedQuery;
	}

	public void rebuild() throws ParserException, StandardException {

		parsedContent = null;

		getParsedContent();

	}

	public LinkedList<TableStructure> getRelevantTables() {

		LinkedList<TableStructure> tmp = new LinkedList<TableStructure>();

		if (tables == null) {
			return null;
		}

		for (TableStructure item : tables) {
			if (item instanceof JoinTableStructure) {
				tmp.addAll(getTablesFromJoin((JoinTableStructure) item));
			} else {
				tmp.add(item);
			}
		}

		return tmp;

	}

	public static LinkedList<TableStructure> getTablesFromJoin(
			JoinTableStructure join) {

		LinkedList<TableStructure> tmp = new LinkedList<TableStructure>();

		if (join.getLeftTable() instanceof JoinTableStructure) {
			tmp.addAll(getTablesFromJoin((JoinTableStructure) join
					.getLeftTable()));
		} else {
			tmp.add(join.getLeftTable());
		}

		if (join.getRightTable() instanceof JoinTableStructure) {
			tmp.addAll(getTablesFromJoin((JoinTableStructure) join
					.getRightTable()));
		} else {
			tmp.add(join.getRightTable());
		}

		return tmp;

	}

	public LinkedList<String> getContainingTags() {

		LinkedList<String> tmpList = new LinkedList<String>();

		String[] relevantTags = { "SELECT", "UPDATE", "INSERT", "DELETE",
				"VALUES", "ORDER BY", "LIMIT", "HAVING", "WHERE", "JOIN",
				"GROUP BY", "SET" };

		for (String item : relevantTags) {
			if (plainContent.toUpperCase().contains(item))
				tmpList.add(item);
		}

		return tmpList;

	}

	public void setResult(SqlResult result) {
		this.result = result;
	}

	public SqlResult getResult() {
		return result;
	}

	public void setError(SqlError error) {
		this.error = error;
	}

}
