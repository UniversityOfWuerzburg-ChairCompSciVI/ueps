package de.uniwue.info6.parser.visitors;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  UpdateColumnVisitor.java
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


import com.akiban.sql.StandardException;
import com.akiban.sql.parser.AggregateNode;
import com.akiban.sql.parser.BinaryOperatorNode;
import com.akiban.sql.parser.ColumnReference;
import com.akiban.sql.parser.ConstantNode;
import com.akiban.sql.parser.JavaToSQLValueNode;
import com.akiban.sql.parser.NodeTypes;
import com.akiban.sql.parser.QueryTreeNode;
import com.akiban.sql.parser.SQLToJavaValueNode;
import com.akiban.sql.parser.StaticMethodCallNode;
import com.akiban.sql.parser.Visitable;
import com.akiban.sql.parser.Visitor;

import de.uniwue.info6.parser.structures.ColumnStructure;
import de.uniwue.info6.parser.structures.Structure;
import de.uniwue.info6.parser.structures.UpdateColumnStructure;

/**
 * Visitor for analzying update statements .
 * 
 * @author Christian
 * 
 */
public class UpdateColumnVisitor implements Visitor {

	private UpdateColumnStructure conditionTree;
	private Structure leftOperand;
	private Structure rightOperand;
	private String tableName;

	/* (non-Javadoc)
	 * @see com.akiban.sql.parser.Visitor#visit(com.akiban.sql.parser.Visitable)
	 */
	@Override
	public Visitable visit(Visitable node) throws StandardException {

		switch (((QueryTreeNode) node).getNodeType()) {
		case NodeTypes.JAVA_TO_SQL_VALUE_NODE:
			visit((JavaToSQLValueNode) node);
			break;
		case NodeTypes.RESULT_COLUMN:
			break;
		case NodeTypes.AGGREGATE_NODE:
			visit((AggregateNode) node);
			break;
		case NodeTypes.COLUMN_REFERENCE:
			ColumnStructure tmp = new ColumnStructure(
					((ColumnReference) node).getColumnName(), null);
			tableName = ((ColumnReference) node).getTableName();
			setOperand(tmp);
			break;
		default:

			if (node instanceof BinaryOperatorNode) {
				visit((BinaryOperatorNode) node);
			} else if (node instanceof ConstantNode) {
				visit((ConstantNode) node);
			} else if (node instanceof SQLToJavaValueNode) {

			} else
				throw new StandardException("Unbekannter Parameter: \n "
						+ node.getClass() + "\n" + node.toString());

			break;

		}

		return node;

	}

	/**
	 * @return
	 */
	public UpdateColumnStructure getConditionTree() {
		return conditionTree;
	}

	/**
	 * @param node
	 * @throws StandardException
	 */
	public void visit(AggregateNode node) throws StandardException {

		AggregateNodeVisitor avisitor = new AggregateNodeVisitor();
		node.accept(avisitor);
		avisitor.getConditionTree();

	}

	/**
	 * @param node
	 * @throws StandardException
	 */
	public void visit(JavaToSQLValueNode node) throws StandardException {

		StaticMethodCallNodeVisitor avisitor = new StaticMethodCallNodeVisitor();
		node.accept(avisitor);
		avisitor.getConditionTree();

	}

	/**
	 * @param node
	 */
	public void visit(ConstantNode node) {
		Structure tmp = new Structure(node.getValue().toString());
		setOperand(tmp);
	}

	/**
	 * @param node
	 * @throws StandardException
	 */
	public void visit(BinaryOperatorNode node) throws StandardException {

		WhereClauseVisitor wvisitor = new WhereClauseVisitor();
		node.accept(wvisitor);

		Structure tmp = wvisitor.getConditionTree();

		setOperand(tmp);

	}

	/**
	 * @param tmp
	 */
	public void setOperand(Structure tmp) {

		if (leftOperand == null)
			leftOperand = tmp;
		else if (rightOperand == null) {

			rightOperand = tmp;
			conditionTree = new UpdateColumnStructure(rightOperand, tableName,
					leftOperand.toString());

		}
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

		if (node instanceof BinaryOperatorNode)
			return true;
		else if (node instanceof AggregateNode)
			return true;
		else if (node instanceof StaticMethodCallNode)
			return true;
		else if (rightOperand != null)
			return true;

		return false;

	}

}
