package de.uniwue.info6.parser.visitors;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  FromListVisitor.java
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
import com.akiban.sql.parser.BinaryOperatorNode;
import com.akiban.sql.parser.ColumnReference;
import com.akiban.sql.parser.ConstantNode;
import com.akiban.sql.parser.FromTable;
import com.akiban.sql.parser.JoinNode;
import com.akiban.sql.parser.NodeTypes;
import com.akiban.sql.parser.QueryTreeNode;
import com.akiban.sql.parser.Visitable;
import com.akiban.sql.parser.Visitor;

import de.uniwue.info6.parser.structures.TableStructure;

/**
 * Visitor for collecting 'from table' entries.
 * 
 * @author Christian
 * 
 */
public class FromListVisitor implements Visitor {

	private LinkedList<TableStructure> tables = new LinkedList<TableStructure>();

	/**
	 * Main visit function, delegates entries to the according sub visit
	 * function.
	 * 
	 * @param node
	 * @throws StandardException
	 * @see com.akiban.sql.parser.Visitor#visit(com.akiban.sql.parser.Visitable)
	 */
	@Override
	public Visitable visit(Visitable node) throws StandardException {

		switch (((QueryTreeNode) node).getNodeType()) {
		case NodeTypes.FROM_LIST:
			break;
		case NodeTypes.FROM_BASE_TABLE:
			tables.add(new TableStructure(((FromTable) node).getOrigTableName().getTableName()));
			break;
		case NodeTypes.FULL_OUTER_JOIN_NODE:
		case NodeTypes.HALF_OUTER_JOIN_NODE:
		case NodeTypes.JOIN_NODE:
			visit((JoinNode) node);
			break;
		default:

			if (node instanceof BinaryOperatorNode) {

			} else if (node instanceof ConstantNode) {

			} else if (node instanceof ColumnReference) {

			} else {
				throw new StandardException("Unbekannter Parameter: \n "
						+ node.getClass() + "\n" + node.toString());
			}

			break;

		}

		return node;

	}

	/**
	 * Sub visit function for join nodes, delegates node to a JoinNodeVisitor.
	 * 
	 * @param node
	 * @throws StandardException
	 */
	public void visit(JoinNode node) throws StandardException {

		JoinNodeVisitor jvisitor = new JoinNodeVisitor();
		node.accept(jvisitor);

		tables.add(jvisitor.getJoinTree());

	}

	/**
	 * @return tables
	 */
	public LinkedList<TableStructure> getTables() {
		return tables;
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

		boolean tmp = false;

		switch (((QueryTreeNode) node).getNodeType()) {
		case NodeTypes.JOIN_NODE:
			tmp = true;
			break;
		}

		return tmp;
	}

}
