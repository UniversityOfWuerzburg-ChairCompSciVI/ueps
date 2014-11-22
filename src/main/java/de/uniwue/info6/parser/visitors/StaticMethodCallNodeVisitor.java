package de.uniwue.info6.parser.visitors;

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
