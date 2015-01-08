package de.uniwue.info6.parser.visitors;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  InsertVisitor.java
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
import com.akiban.sql.parser.InsertNode;
import com.akiban.sql.parser.NodeTypes;
import com.akiban.sql.parser.QueryTreeNode;
import com.akiban.sql.parser.ResultColumnList;
import com.akiban.sql.parser.Visitable;
import com.akiban.sql.parser.Visitor;

import de.uniwue.info6.parser.structures.ColumnStructure;
import de.uniwue.info6.parser.structures.TableStructure;

/**
 * Visitor for analyzing insert nodes. INSERT INTO table (targetColumns) VALUES
 * (valueColumns)
 * 
 * @author Christian
 * 
 */
public class InsertVisitor implements Visitor {

	private LinkedList<ColumnStructure> targetColumns = new LinkedList<ColumnStructure>();
	private LinkedList<ColumnStructure> valueColumns = new LinkedList<ColumnStructure>();
	private TableStructure table;

	private boolean isValues = false;

	/**
	 * Main visit function, sub nodes to the according sub visit function.
	 * 
	 * @param node
	 * @throws StandardException
	 * @see com.akiban.sql.parser.Visitor#visit(com.akiban.sql.parser.Visitable)
	 */
	@Override
	public Visitable visit(Visitable node) throws StandardException {

		switch (((QueryTreeNode) node).getNodeType()) {
		case NodeTypes.INSERT_NODE:

			if (((InsertNode) node).getTargetTableName() != null) {
				table = new TableStructure(((InsertNode) node)
						.getTargetTableName().toString());
			}

			if (((InsertNode) node).getTargetColumnList() != null) {
				visit(((InsertNode) node).getTargetColumnList());
			}

			if (((InsertNode) node).getResultSetNode() != null) {
				isValues = true;
				visit(((InsertNode) node).getResultSetNode().getResultColumns());
				isValues = false;
			}

			break;

		}

		return node;

	}

	/**
	 * Sub visit function for columns, delegates column list to an
	 * ResultColumnVisitor.
	 * 
	 * @param node
	 * @throws StandardException
	 */
	public void visit(ResultColumnList node) throws StandardException {

		ResultColumnListVisitor rvisitor = new ResultColumnListVisitor("");
		node.accept(rvisitor);
		rvisitor.close();

		if (!isValues)
			targetColumns.addAll(rvisitor.getColumns());
		else
			valueColumns.addAll(rvisitor.getColumns());
	}

	/**
	 * @return valueColumns
	 */
	public LinkedList<ColumnStructure> getValueColumns() {
		return valueColumns;
	}

	/**
	 * @return targetColumns
	 */
	public LinkedList<ColumnStructure> getTargetColumns() {
		return targetColumns;
	}

	/**
	 * @return table
	 */
	public TableStructure getTable() {
		return table;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.akiban.sql.parser.Visitor#visitChildrenFirst(com.akiban.sql.parser.Visitable)
	 */
	@Override
	public boolean visitChildrenFirst(Visitable node) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.akiban.sql.parser.Visitor#stopTraversal()
	 */
	@Override
	public boolean stopTraversal() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.akiban.sql.parser.Visitor#skipChildren(com.akiban.sql.parser.Visitable)
	 */
	@Override
	public boolean skipChildren(Visitable node) throws StandardException {
		return false;
	}

}
