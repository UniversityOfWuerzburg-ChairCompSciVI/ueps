package de.uniwue.info6.parser.visitors;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  StaticMethodCallNodeVisitor.java
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
import com.akiban.sql.parser.JavaValueNode;
import com.akiban.sql.parser.NodeTypes;
import com.akiban.sql.parser.QueryTreeNode;
import com.akiban.sql.parser.StaticMethodCallNode;
import com.akiban.sql.parser.Visitable;
import com.akiban.sql.parser.Visitor;

import de.uniwue.info6.parser.structures.PrefixOperatorStructure;
import de.uniwue.info6.parser.structures.Structure;

/**
 * Visitor for method calls, like concat, datediff, etc.
 * 
 * @author Christian
 *
 */
public class StaticMethodCallNodeVisitor implements Visitor {

	private PrefixOperatorStructure conditionTree;
	private String operator;
	private LinkedList<Structure> operands = new LinkedList<Structure>();

	/* (non-Javadoc)
	 * @see com.akiban.sql.parser.Visitor#visit(com.akiban.sql.parser.Visitable)
	 */
	@Override
	public Visitable visit(Visitable node) throws StandardException {

		switch (((QueryTreeNode) node).getNodeType()) {
		case NodeTypes.JAVA_TO_SQL_VALUE_NODE:
			break;
		case NodeTypes.STATIC_METHOD_CALL_NODE:

			for (JavaValueNode nodeChild : ((StaticMethodCallNode) node)
					.getMethodParameters()) {

				WhereClauseVisitor wvisitor = new WhereClauseVisitor();
				nodeChild.accept(wvisitor);

				Structure tmp = wvisitor.getConditionTree();
				operands.add(tmp);

			}

			operator = ((StaticMethodCallNode) node).getMethodName();

			if (operator.equals("COUNT(*)")) {
				operands.add(new Structure("*"));
				conditionTree = (new PrefixOperatorStructure("COUNT", operands));
			} else {
				conditionTree = (new PrefixOperatorStructure(operator, operands));
			}

			break;

		default:
			throw new StandardException("Unbekannter Parameter: \n "
					+ node.getClass() + "\n" + node.toString());
		}

		return node;

	}

	/**
	 * @return
	 */
	public PrefixOperatorStructure getConditionTree() {
		return conditionTree;
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

		return false;

	}

}
