package de.uniwue.info6.parser.visitors;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  AggregateNodeVisitor.java
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
import com.akiban.sql.parser.BinaryOperatorNode;
import com.akiban.sql.parser.ColumnReference;
import com.akiban.sql.parser.ConstantNode;
import com.akiban.sql.parser.NodeTypes;
import com.akiban.sql.parser.QueryTreeNode;
import com.akiban.sql.parser.SQLToJavaValueNode;
import com.akiban.sql.parser.StaticMethodCallNode;
import com.akiban.sql.parser.Visitable;
import com.akiban.sql.parser.Visitor;

import de.uniwue.info6.parser.structures.ColumnStructure;
import de.uniwue.info6.parser.structures.PrefixOperatorStructure;
import de.uniwue.info6.parser.structures.Structure;

/**
 * Visitor for analysis of aggregate mysql fuctions like sum, max and count.
 * 
 * @author Christian
 * 
 */
public class AggregateNodeVisitor implements Visitor {

	private PrefixOperatorStructure conditionTree; // contains the resulting
													// operator tree
	private String operator;
	private LinkedList<Structure> operands = new LinkedList<Structure>();
	private boolean stopChilds = false; // do not visit childs of this node

	/**
	 * Main visit function, gets operator and delegates operands to the
	 * according sub visit function.
	 * 
	 * @param node
	 * @throws StandardException
	 * @see com.akiban.sql.parser.Visitor#visit(com.akiban.sql.parser.Visitable)
	 */
	@Override
	public Visitable visit(Visitable node) throws StandardException {

		switch (((QueryTreeNode) node).getNodeType()) {
		case NodeTypes.AGGREGATE_NODE:

			if (operator == null) {
				operator = ((AggregateNode) node).getAggregateName();
			} else {
				visit((AggregateNode) node);
				stopChilds = true;
			}

			if (operator.equals("COUNT(*)")) { // count needs special treatment
				operands.add(new Structure("*"));
				conditionTree = (new PrefixOperatorStructure("COUNT", operands));
			}

			break;

		case NodeTypes.COLUMN_REFERENCE:

			ColumnStructure tmp5 = new ColumnStructure(
					((ColumnReference) node).getColumnName(),
					((ColumnReference) node).getTableName());

			setOperand(tmp5);

			break;

		default:

			if (node instanceof BinaryOperatorNode) {
				visit((BinaryOperatorNode) node);
			} else if (node instanceof ConstantNode) {

				Structure tmp2 = new Structure(((ConstantNode) node).getValue()
						.toString());
				setOperand(tmp2);

			} else if (node instanceof StaticMethodCallNode) {

				StaticMethodCallNodeVisitor tvisitor = new StaticMethodCallNodeVisitor();
				node.accept(tvisitor);
				PrefixOperatorStructure tmp4 = tvisitor.getConditionTree();
				setOperand(tmp4);

			} else if (node instanceof SQLToJavaValueNode) {

			} else {
				throw new StandardException("Unbekannter Parameter: \n "
						+ node.getClass() + "\n" + node.toString());
			}

			break;

		}

		return node;

	}

	/**
	 * Sub visit function for binary operators, delegates current node to a
	 * WhereClauseVisitor.
	 * 
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
	 * Sub visit function for aggregate function, delegates current node to a
	 * AggregateNodeVisitor. Note: This is not the initial node, but its
	 * operands.
	 * 
	 * @param node
	 * @throws StandardException
	 */
	public void visit(AggregateNode node) throws StandardException {

		AggregateNodeVisitor avisitor = new AggregateNodeVisitor();
		node.accept(avisitor);

		PrefixOperatorStructure tmp = avisitor.getConditionTree();

		setOperand(tmp);

	}

	/**
	 * Adds a given structure to operand list.
	 * 
	 * @param tmp
	 */
	public void setOperand(Structure tmp) {

		if (!stopChilds) { // work around

			operands.add(tmp);
			conditionTree = (new PrefixOperatorStructure(operator, operands));

			stopChilds = true;

		}

	}

	/**
	 * @return condition tree
	 */
	public PrefixOperatorStructure getConditionTree() {
		return conditionTree;
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
		return stopChilds;
	}

}
