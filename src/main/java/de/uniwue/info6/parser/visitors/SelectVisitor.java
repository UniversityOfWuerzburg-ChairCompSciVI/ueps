package de.uniwue.info6.parser.visitors;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  SelectVisitor.java
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
import com.akiban.sql.parser.FromList;
import com.akiban.sql.parser.NodeTypes;
import com.akiban.sql.parser.QueryTreeNode;
import com.akiban.sql.parser.ResultColumnList;
import com.akiban.sql.parser.SelectNode;
import com.akiban.sql.parser.SubqueryNode;
import com.akiban.sql.parser.Visitable;
import com.akiban.sql.parser.Visitor;

import de.uniwue.info6.parser.structures.ColumnStructure;
import de.uniwue.info6.parser.structures.Structure;
import de.uniwue.info6.parser.structures.TableStructure;

/**
 * Visitor for analyzing select statements.
 * 
 * @author Christian
 * 
 */
public class SelectVisitor implements Visitor {

	private LinkedList<ColumnStructure> columns = new LinkedList<ColumnStructure>();
	private LinkedList<TableStructure> tables = new LinkedList<TableStructure>();
	private LinkedList<ColumnStructure> groupBys = new LinkedList<ColumnStructure>();
	private Structure whereConditions;
	private Structure havingConditions;

	private String mainKeyWord;

	/**
	 * @param mainKeyWord
	 */
	public SelectVisitor(String mainKeyWord) {
		this.mainKeyWord = mainKeyWord;
	}

	/**
	 * Main visit function, delegates node to the according sub visit function.
	 * 
	 * @param node
	 * @throws StandardException
	 * @see com.akiban.sql.parser.Visitor#visit(com.akiban.sql.parser.Visitable)
	 */
	@Override
	public Visitable visit(Visitable node) throws StandardException {

		switch (((QueryTreeNode) node).getNodeType()) {
		case NodeTypes.SELECT_NODE:

			((SelectNode) node).isDistinct();

			if (((SelectNode) node).getWhereClause() != null) {

				WhereClauseVisitor wvisitor = new WhereClauseVisitor();
				((SelectNode) node).getWhereClause().accept(wvisitor);
				whereConditions = wvisitor.getConditionTree();
				
				tables.addAll(wvisitor.getTables());

			}

			if (((SelectNode) node).getGroupByList() != null) {

				GroupByListVisitor wvisitor = new GroupByListVisitor();
				((SelectNode) node).getGroupByList().accept(wvisitor);
				groupBys.addAll(wvisitor.getGroupBys());

			}

			if (((SelectNode) node).getHavingClause() != null) {

				WhereClauseVisitor wvisitor = new WhereClauseVisitor();
				((SelectNode) node).getHavingClause().accept(wvisitor);
				havingConditions = wvisitor.getConditionTree();
				
				tables.addAll(wvisitor.getTables());

			}

			if (((SelectNode) node).getFromList() != null)
				visit(((SelectNode) node).getFromList());

			if (((SelectNode) node).getResultColumns() != null)
				visit(((SelectNode) node).getResultColumns());

			break;

		}

		return node;

	}

	/**
	 * @param node
	 * @throws StandardException
	 */
	public void visit(ResultColumnList node) throws StandardException {

		ResultColumnListVisitor rvisitor = new ResultColumnListVisitor(
				mainKeyWord);
		node.accept(rvisitor);
		rvisitor.close();

		columns.addAll(rvisitor.getColumns());

	}

	/**
	 * @param nodeList
	 * @throws StandardException
	 */
	private void visit(FromList nodeList) throws StandardException {

		FromListVisitor fvisitor = new FromListVisitor();
		nodeList.accept(fvisitor);

		tables.addAll(fvisitor.getTables());

	}

	/**
	 * @return columns
	 */
	public LinkedList<ColumnStructure> getColumns() {
		return columns;
	}

	/**
	 * @return tables
	 */
	public LinkedList<TableStructure> getTables() {
		return tables;
	}

	/**
	 * @return 'group by' list
	 */
	public LinkedList<ColumnStructure> getGroupBys() {
		return groupBys;
	}

	/**
	 * @return where conditions
	 */
	public Structure getWhereConditions() {
		return whereConditions;
	}

	/**
	 * @return having conditions
	 */
	public Structure getHavingConditions() {
		return havingConditions;
	}

	/**
	 * @return main keyword
	 */
	public String getMainKeyWord() {
		return mainKeyWord;
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
		if(node instanceof SubqueryNode)
			return true;
		
		return false;
	}

}
