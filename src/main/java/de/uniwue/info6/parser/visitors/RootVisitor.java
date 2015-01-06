package de.uniwue.info6.parser.visitors;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  RootVisitor.java
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
import com.akiban.sql.parser.CursorNode;
import com.akiban.sql.parser.DeleteNode;
import com.akiban.sql.parser.InsertNode;
import com.akiban.sql.parser.NodeTypes;
import com.akiban.sql.parser.OrderByList;
import com.akiban.sql.parser.QueryTreeNode;
import com.akiban.sql.parser.SelectNode;
import com.akiban.sql.parser.UpdateNode;
import com.akiban.sql.parser.Visitable;
import com.akiban.sql.parser.Visitor;

import de.uniwue.info6.parser.structures.ColumnStructure;
import de.uniwue.info6.parser.structures.OrderColumnStructure;
import de.uniwue.info6.parser.structures.Structure;
import de.uniwue.info6.parser.structures.TableStructure;

/**
 * Root visitor, delegates node to an according visitor.
 * 
 * @author Christian
 * 
 */
public class RootVisitor implements Visitor {

	private String mainKeyWord;
	private LinkedList<OrderColumnStructure> orderBys = new LinkedList<OrderColumnStructure>();
	private LinkedList<ColumnStructure> columns = new LinkedList<ColumnStructure>();
	private LinkedList<TableStructure> tables = new LinkedList<TableStructure>();
	private LinkedList<ColumnStructure> groupBys = new LinkedList<ColumnStructure>();
	private LinkedList<ColumnStructure> targetColumns = new LinkedList<ColumnStructure>();
	private LinkedList<ColumnStructure> valueColumns = new LinkedList<ColumnStructure>();
	private Structure whereConditions;
	private Structure havingConditions;
	private int firstFetch;
	private int numFetch;
	private boolean hasSelect = false;

	/**
	 * Main visit function, delegates node to an according sub visit function.
	 * 
	 * @param node
	 * @throws StandardException
	 * @see com.akiban.sql.parser.Visitor#visit(com.akiban.sql.parser.Visitable)
	 */
	@Override
	public Visitable visit(Visitable node) throws StandardException {

		switch (((QueryTreeNode) node).getNodeType()) {
		case NodeTypes.CURSOR_NODE:

			if (((CursorNode) node).getFetchFirstClause() != null) { // limit
																		// (offset,
																		// fetch
																		// first)
				LimitClauseVisitor lvisitor = new LimitClauseVisitor();
				((CursorNode) node).getFetchFirstClause().accept(lvisitor);

				firstFetch = lvisitor.getOffset();
			}

			if (((CursorNode) node).getOffsetClause() != null) { // limit
																	// (offset,
																	// fetch
																	// first)
				LimitClauseVisitor lvisitor = new LimitClauseVisitor();
				((CursorNode) node).getOffsetClause().accept(lvisitor);

				numFetch = lvisitor.getOffset();
			}

			break;
		case NodeTypes.SELECT_NODE:
			visit((SelectNode) node);
			break;
		case NodeTypes.DELETE_NODE: // next child is select node
			visit((DeleteNode) node);
			break;
		case NodeTypes.UPDATE_NODE: // next child is select node
			visit((UpdateNode) node);
			break;
		case NodeTypes.INSERT_NODE:
			visit((InsertNode) node);
			break;
		case NodeTypes.ORDER_BY_LIST:
			visit((OrderByList) node);
			break;
		}

		return node;

	}

	/**
	 * Select node.
	 * 
	 * @param node
	 * @throws StandardException
	 */
	public void visit(SelectNode node) throws StandardException {
		
		if(hasSelect){
			return;
		} else {
			hasSelect = true;
		}
		
		setMainKeyWord("SELECT");

		SelectVisitor svisitor = new SelectVisitor(mainKeyWord);
		node.accept(svisitor);

		tables = svisitor.getTables();
		columns = svisitor.getColumns();
		whereConditions = svisitor.getWhereConditions();
		havingConditions = svisitor.getHavingConditions();
		groupBys = svisitor.getGroupBys();

	}

	/**
	 * Delete node.
	 * 
	 * @param node
	 * @throws StandardException
	 */
	public void visit(DeleteNode node) throws StandardException {
		setMainKeyWord("DELETE");
	}

	/**
	 * Insert node.
	 * 
	 * @param node
	 * @throws StandardException
	 */
	public void visit(InsertNode node) throws StandardException {

		setMainKeyWord("INSERT");

		InsertVisitor ivisitor = new InsertVisitor();
		node.accept(ivisitor);

		tables.add(ivisitor.getTable());
		targetColumns = ivisitor.getTargetColumns();
		valueColumns = ivisitor.getValueColumns();

	}

	/**
	 * Update node.
	 * 
	 * @param node
	 * @throws StandardException
	 */
	public void visit(UpdateNode node) throws StandardException {
		setMainKeyWord("UPDATE");
	}

	/**
	 * Sub visit function for 'order by' list.
	 * 
	 * @param node
	 * @throws StandardException
	 */
	public void visit(OrderByList node) throws StandardException {

		OrderByListVisitor ovisitor = new OrderByListVisitor();
		node.accept(ovisitor);

		orderBys.addAll(ovisitor.getOrderBys());

	}

	/**
	 * @return first fetch
	 */
	public int getFirstFetch() {
		return firstFetch;
	}

	/**
	 * @return num fetch
	 */
	public int getNumFetch() {
		return numFetch;
	}

	/**
	 * @return main keyword
	 */
	public String getMainKeyWord() {
		return mainKeyWord;
	}

	/**
	 * @return columns
	 */
	public LinkedList<ColumnStructure> getColumns() {
		return columns.size() == 0 ? null : columns;
	}
	
	public LinkedList<ColumnStructure> getTargetColumns() {
		return targetColumns.size() == 0 ? null : targetColumns;
	}
	
	public LinkedList<ColumnStructure> getValueColumns() {
		return valueColumns.size() == 0 ? null : valueColumns;
	}

	/**
	 * @return tables
	 */
	public LinkedList<TableStructure> getTables() {
		return tables.size() == 0 ? null : tables;
	}

	/**
	 * @return 'group by' list
	 */
	public LinkedList<ColumnStructure> getGroupBys() {
		return groupBys.size() == 0 ? null : groupBys;
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
	 * @param keyword
	 */
	private void setMainKeyWord(String keyword) {

		if (mainKeyWord == null)
			mainKeyWord = keyword;

	}

	/**
	 * @return 'order by' list
	 */
	public LinkedList<OrderColumnStructure> getOrderBys() {
		return orderBys;
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
		return false;
	}

	/*
	 * (non-Javadoc)	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return mainKeyWord
				+ (columns.size() > 0 ? " " + columns.toString() + " " : "") 
				+ (targetColumns.size() > 0 ? " ("+ targetColumns.toString() + ") " : "")
				+ (valueColumns.size() > 0 ? "VALUES ("+ valueColumns.toString() + ") " : "")
				+ (tables.size() > 0 ? " FROM " + tables.toString() : "")
				+ (whereConditions != null ? " WHERE "+ whereConditions.toString() : "")
				+ (groupBys.size() > 0 ? " GROUP BY " + groupBys.toString() : "")
				+ (havingConditions != null ? " HAVING " + havingConditions.toString() : "")
				+ (orderBys.size() > 0 ? " ORDER BY " + orderBys.toString() : "")
				+ " LIMIT (" + numFetch + ", " + firstFetch + ")";
	}

}
