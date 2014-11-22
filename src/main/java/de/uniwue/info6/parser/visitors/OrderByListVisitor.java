package de.uniwue.info6.parser.visitors;

import java.util.LinkedList;

import com.akiban.sql.StandardException;
import com.akiban.sql.parser.NodeTypes;
import com.akiban.sql.parser.OrderByColumn;
import com.akiban.sql.parser.QueryTreeNode;
import com.akiban.sql.parser.Visitable;
import com.akiban.sql.parser.Visitor;

import de.uniwue.info6.parser.structures.OrderColumnStructure;

/**
 * Visitor for getting 'order by' entries.
 * 
 * @author Christian
 * 
 */
public class OrderByListVisitor implements Visitor {

	private LinkedList<OrderColumnStructure> orderBys = new LinkedList<OrderColumnStructure>();

	/**
	 * Main visit function, delegates order by columns to sub visit function.
	 * 
	 * @param node
	 * @throws StandardException
	 * @see com.akiban.sql.parser.Visitor#visit(com.akiban.sql.parser.Visitable)
	 */
	@Override
	public Visitable visit(Visitable node) throws StandardException {

		switch (((QueryTreeNode) node).getNodeType()) {
		case NodeTypes.ORDER_BY_LIST:
			break;
		case NodeTypes.ORDER_BY_COLUMN:
			visit((OrderByColumn) node);
			break;
		default:
			// throw new StandardException("Unbekannter Parameter: \n " +
			// node.getClass() + "\n" + node.toString());
		}

		return node;

	}

	/**
	 * Sub visit function for order by columns
	 * 
	 * @param node
	 */
	public void visit(OrderByColumn node) {
		orderBys.add(new OrderColumnStructure(node.getExpression()
				.getColumnName(), null, node.isAscending()));
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

}
