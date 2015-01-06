package de.uniwue.info6.parser.visitors;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  ResultColumnListVisitor.java
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
import com.akiban.sql.parser.AggregateNode;
import com.akiban.sql.parser.AllResultColumn;
import com.akiban.sql.parser.BinaryOperatorNode;
import com.akiban.sql.parser.ColumnReference;
import com.akiban.sql.parser.ConstantNode;
import com.akiban.sql.parser.JavaToSQLValueNode;
import com.akiban.sql.parser.NodeTypes;
import com.akiban.sql.parser.QueryTreeNode;
import com.akiban.sql.parser.ResultColumn;
import com.akiban.sql.parser.StaticMethodCallNode;
import com.akiban.sql.parser.Visitable;
import com.akiban.sql.parser.Visitor;

import de.uniwue.info6.parser.structures.AliasColumnStructure;
import de.uniwue.info6.parser.structures.ColumnStructure;
import de.uniwue.info6.parser.structures.Structure;

/**
 * Visitor for analyzing all column entries, and getting their types like alias
 * or update.
 * 
 * @author Christian
 * 
 */
public class ResultColumnListVisitor implements Visitor {

	private boolean isPuffering = false;
	private String puffer = "";
	private String exposedPuffer = ""; // contains alias string
	private String mainKeyWord;
	private String tableName;
	private LinkedList<ColumnStructure> columns = new LinkedList<ColumnStructure>();

	/**
	 * Necessary for getting update columns.
	 * 
	 * @param mainKeyWord
	 */
	public ResultColumnListVisitor(String mainKeyWord) {
		this.mainKeyWord = mainKeyWord;
	}

	/**
	 * Main visit function, delegates columns to an according sub visit
	 * function.
	 * 
	 * @param node
	 * @throws StandardException
	 * @see com.akiban.sql.parser.Visitor#visit(com.akiban.sql.parser.Visitable)
	 */
	@Override
	public Visitable visit(Visitable node) throws StandardException {

		switch (((QueryTreeNode) node).getNodeType()) {
		case NodeTypes.RESULT_COLUMN_LIST:
			break;
		case NodeTypes.RESULT_COLUMN:
			visit((ResultColumn) node);
			break;
		case NodeTypes.ALL_RESULT_COLUMN:
			visit((AllResultColumn) node);
			break;
		case NodeTypes.AGGREGATE_NODE:
			visit((AggregateNode) node);
			break;
		case NodeTypes.COLUMN_REFERENCE:
			visit((ColumnReference) node);
			break;
		case NodeTypes.JAVA_TO_SQL_VALUE_NODE:
			break;
		case NodeTypes.SQL_TO_JAVA_VALUE_NODE:
			break;
		case NodeTypes.STATIC_METHOD_CALL_NODE:
			visit((StaticMethodCallNode) node);
			break;
		default:

			if (node instanceof BinaryOperatorNode) {
				visit((BinaryOperatorNode) node);
			} else if (node instanceof ConstantNode) {

				isPuffering = true;
				extendCol(((ConstantNode) node).getValue().toString());

			} else
				throw new StandardException("Unbekannter Parameter: \n "
						+ node.getClass() + "\n" + node.toString());

		}

		return node;

	}

	/**
	 * A column starts.
	 * 
	 * @param node
	 * @throws StandardException
	 */
	public void visit(ResultColumn node) throws StandardException {

		close();

		isPuffering = true;
		puffer = "";
		tableName = "";
		exposedPuffer = node.getName();

		if (mainKeyWord.equals("UPDATE")) {

			UpdateColumnVisitor uvisitor = new UpdateColumnVisitor();
			node.accept(uvisitor);

			isPuffering = false;
			extendCol(uvisitor.getConditionTree());

		}

	}

	/**
	 * Sometimes the visitor cannot know if he reached the end of a column, so
	 * it has to be closed.
	 */
	public void close() {

		ColumnStructure tmp;

		if (isPuffering) {

			isPuffering = false;

			tmp = new ColumnStructure(puffer, null);
			extendCol(tmp);

			puffer = "";
			exposedPuffer = "";

		}

	}

	/**
	 * Sub visit function for binary operations.
	 * 
	 * @param node
	 * @throws StandardException
	 */
	public void visit(BinaryOperatorNode node) throws StandardException {

		WhereClauseVisitor wvisitor = new WhereClauseVisitor();
		node.accept(wvisitor);

		Structure tmp = wvisitor.getConditionTree();

		extendCol(tmp);

	}

	/**
	 * Adds tmp to column puffer, or to columns.
	 * 
	 * @param tmp
	 */
	public void extendCol(String tmp) {

		if (isPuffering)
			puffer += tmp;
		else {

			ColumnStructure tmp2 = new ColumnStructure(puffer + tmp, null);
			columns.add(tmp2);

		}
	}

	/**
	 * Adds tmp to column puffer, or to columns.
	 * 
	 * @param tmp
	 */
	public void extendCol(Structure tmp) {

		if (isPuffering)
			puffer += tmp;
		else {

			if (exposedPuffer != null && mainKeyWord.equals("SELECT")
					&& !exposedPuffer.equals(puffer))
				columns.add(new AliasColumnStructure(tmp, tableName,
						exposedPuffer));
			else
				columns.add(new ColumnStructure(tmp, tableName));

		}

	}

	/**
	 * Sub visit function for aggregate functions.
	 * 
	 * @param node
	 * @throws StandardException
	 */
	public void visit(AggregateNode node) throws StandardException {

		AggregateNodeVisitor avisitor = new AggregateNodeVisitor();
		node.accept(avisitor);
		extendCol(avisitor.getConditionTree());

	}

	/**
	 * Sub visit function for method calls.
	 * 
	 * @param node
	 * @throws StandardException
	 */
	public void visit(StaticMethodCallNode node) throws StandardException {

		StaticMethodCallNodeVisitor avisitor = new StaticMethodCallNodeVisitor();
		node.accept(avisitor);
		extendCol(avisitor.getConditionTree());

	}

	/**
	 * Sub visit function for method calls, see above. Note: Sometimes a method
	 * call is hidden in a JavaToSQLValueNode.
	 * 
	 * @param node
	 * @throws StandardException
	 */
	public void visit(JavaToSQLValueNode node) throws StandardException {

		StaticMethodCallNodeVisitor avisitor = new StaticMethodCallNodeVisitor();
		node.accept(avisitor);
		extendCol(avisitor.getConditionTree());

	}

	/**
	 * Sub visit function for '*'.
	 * 
	 * @param node
	 */
	public void visit(AllResultColumn node) {
		extendCol(new Structure("*"));
	}

	/**
	 * Sub visit function for columns.
	 * 
	 * @param node
	 */
	public void visit(ColumnReference node) {
		tableName = node.getTableName();
		extendCol(node.getColumnName());

	}

	/**
	 * @return columns
	 */
	public LinkedList<ColumnStructure> getColumns() {
		return columns;
	}

	/* (non-Javadoc)
	 * @see com.akiban.sql.parser.Visitor#visitChildrenFirst(com.akiban.sql.parser.Visitable)
	 */
	@Override
	public boolean visitChildrenFirst(Visitable node) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.akiban.sql.parser.Visitor#stopTraversal()
	 */
	@Override
	public boolean stopTraversal() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.akiban.sql.parser.Visitor#skipChildren(com.akiban.sql.parser.Visitable)
	 */
	@Override
	public boolean skipChildren(Visitable node) throws StandardException {

		boolean tmp = false;

		switch (((QueryTreeNode) node).getNodeType()) {
		case NodeTypes.AGGREGATE_NODE:
			tmp = true;
			break;
		case NodeTypes.RESULT_COLUMN:
			if (mainKeyWord.equals("UPDATE"))
				return true;
			break;
		case NodeTypes.STATIC_METHOD_CALL_NODE:
			tmp = true;
			break;
		}

		if (node instanceof BinaryOperatorNode) {
			return true;
		}

		return tmp;

	}

}
