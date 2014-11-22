package de.uniwue.info6.parser.visitors;

import java.util.LinkedList;

import com.akiban.sql.StandardException;
import com.akiban.sql.parser.GroupByColumn;
import com.akiban.sql.parser.NodeTypes;
import com.akiban.sql.parser.QueryTreeNode;
import com.akiban.sql.parser.Visitable;
import com.akiban.sql.parser.Visitor;

import de.uniwue.info6.parser.structures.ColumnStructure;

/**
 * Visitor for collecting 'group by' entries.
 * 
 * @author Christian
 * 
 */
public class GroupByListVisitor implements Visitor {

	private LinkedList<ColumnStructure> groupBys = new LinkedList<ColumnStructure>();

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
		case NodeTypes.GROUP_BY_LIST:
			break;
		case NodeTypes.GROUP_BY_COLUMN:
			visit((GroupByColumn) node);
			break;
		default:
			// throw new StandardException("Unbekannter Parameter: \n " +
			// node.getClass() + "\n" + node.toString());
		}

		return node;

	}

	/**
	 * Adds current node to 'group by' list
	 * 
	 * @param node
	 */
	public void visit(GroupByColumn node) {

		groupBys.add(new ColumnStructure(node.getColumnName(), null));

	}

	/**
	 * @return 'group by' list
	 */
	public LinkedList<ColumnStructure> getGroupBys() {
		return groupBys;
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
