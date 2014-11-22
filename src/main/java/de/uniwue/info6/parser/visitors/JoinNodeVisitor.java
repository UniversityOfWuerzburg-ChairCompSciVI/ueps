package de.uniwue.info6.parser.visitors;

import com.akiban.sql.StandardException;
import com.akiban.sql.parser.BinaryOperatorNode;
import com.akiban.sql.parser.FromTable;
import com.akiban.sql.parser.JoinNode;
import com.akiban.sql.parser.NodeTypes;
import com.akiban.sql.parser.QueryTreeNode;
import com.akiban.sql.parser.Visitable;
import com.akiban.sql.parser.Visitor;

import de.uniwue.info6.parser.structures.JoinTableStructure;
import de.uniwue.info6.parser.structures.Structure;
import de.uniwue.info6.parser.structures.TableStructure;

/**
 * Visitor for analyzing joins.
 * 
 * @author Christian
 * 
 */
public class JoinNodeVisitor implements Visitor {

	private JoinTableStructure joinTree;
	private TableStructure leftTable;
	private TableStructure rightTable;
	private Structure joinClause;
	private String joinType;
	private boolean stopChilds = false;

	/**
	 * Main visit function, gets join type and delegates join tables and join
	 * clause to the according sub visit function.
	 * 
	 * @param node
	 * @throws StandardException
	 * @see com.akiban.sql.parser.Visitor#visit(com.akiban.sql.parser.Visitable)
	 */
	@Override
	public Visitable visit(Visitable node) throws StandardException {

		switch (((QueryTreeNode) node).getNodeType()) {
		case NodeTypes.JOIN_NODE:
			joinType = "JOIN";
			visit((JoinNode) node);
			break;
		case NodeTypes.FULL_OUTER_JOIN_NODE:
			joinType = "FULL_OUTER_JOIN";
			visit((JoinNode) node);
			break;
		case NodeTypes.HALF_OUTER_JOIN_NODE:
			joinType = "HALF_OUTER_JOIN";
			visit((JoinNode) node);
			break;
		case NodeTypes.FROM_BASE_TABLE:		
			setTable(new TableStructure(((FromTable) node).getOrigTableName().getTableName()));
			break;
		default:

			if (node instanceof BinaryOperatorNode)
				visit((BinaryOperatorNode) node);
			else
				throw new StandardException("Unbekannter Parameter: \n "
						+ node.getClass() + "\n" + node.toString());

			break;

		}

		return node;

	}

	/**
	 * Sub visit function for the join clause.
	 * 
	 * @param node
	 * @throws StandardException
	 */
	public void visit(BinaryOperatorNode node) throws StandardException {

		WhereClauseVisitor wvisitor = new WhereClauseVisitor();
		node.accept(wvisitor);

		joinClause = wvisitor.getConditionTree();

	}

	/**
	 * Sub visit function if join table contains another join.
	 * 
	 * @param node
	 * @throws StandardException
	 */
	public void visit(JoinNode node) throws StandardException {

		if (stopChilds) {

			JoinNodeVisitor wvisitor = new JoinNodeVisitor();
			node.accept(wvisitor);
			setTable(wvisitor.getJoinTree());

		} else {
			stopChilds = true;
		}

	}

	/**
	 * Attaches given table structure to table field.
	 * 
	 * @param tmp
	 */
	public void setTable(TableStructure tmp) {

		if (leftTable == null)
			leftTable = tmp;
		else if (rightTable == null)
			rightTable = tmp;

	}

	/**
	 * @return join tree
	 */
	public JoinTableStructure getJoinTree() {

		if (joinTree == null)
			joinTree = new JoinTableStructure(leftTable, rightTable,
					joinClause, joinType);

		return joinTree;

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

		switch (((QueryTreeNode) node).getNodeType()) {
		case NodeTypes.JOIN_NODE:
		case NodeTypes.FULL_OUTER_JOIN_NODE:
		case NodeTypes.HALF_OUTER_JOIN_NODE:
			if (stopChilds == true)
				return true;
			break;
		}

		if (node instanceof BinaryOperatorNode)
			return true;

		return false;

	}

}
