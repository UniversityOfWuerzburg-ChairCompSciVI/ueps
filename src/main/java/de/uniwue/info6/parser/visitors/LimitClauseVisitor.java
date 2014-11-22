package de.uniwue.info6.parser.visitors;

import com.akiban.sql.StandardException;
import com.akiban.sql.parser.ConstantNode;
import com.akiban.sql.parser.Visitable;
import com.akiban.sql.parser.Visitor;

/**
 * Visitor for getting limit offsets.
 * 
 * @author Christian
 * 
 */
public class LimitClauseVisitor implements Visitor {

	private int offset;

	/**
	 * Main visit function, gets limit offset.
	 * 
	 * @param node
	 * @throws StandardException
	 * @see com.akiban.sql.parser.Visitor#visit(com.akiban.sql.parser.Visitable)
	 */
	@Override
	public Visitable visit(Visitable node) throws StandardException {

		if (node instanceof ConstantNode) {
			offset = (int) ((ConstantNode) node).getValue();
		}

		return node;

	}

	/**
	 * @return offset
	 */
	public int getOffset() {
		return offset;
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
		return true;
	}

}
