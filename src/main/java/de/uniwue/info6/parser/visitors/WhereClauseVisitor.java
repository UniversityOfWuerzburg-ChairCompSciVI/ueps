package de.uniwue.info6.parser.visitors;

import java.util.LinkedList;

import com.akiban.sql.StandardException;
import com.akiban.sql.parser.AggregateNode;
import com.akiban.sql.parser.BinaryOperatorNode;
import com.akiban.sql.parser.ColumnReference;
import com.akiban.sql.parser.ConstantNode;
import com.akiban.sql.parser.InListOperatorNode;
import com.akiban.sql.parser.JavaToSQLValueNode;
import com.akiban.sql.parser.RowConstructorNode;
import com.akiban.sql.parser.SQLToJavaValueNode;
import com.akiban.sql.parser.StaticMethodCallNode;
import com.akiban.sql.parser.SubqueryNode;
import com.akiban.sql.parser.TernaryOperatorNode;
import com.akiban.sql.parser.UnaryOperatorNode;
import com.akiban.sql.parser.ValueNode;
import com.akiban.sql.parser.Visitable;
import com.akiban.sql.parser.Visitor;
import com.akiban.sql.parser.NumericConstantNode;

import de.uniwue.info6.parser.structures.ColumnStructure;
import de.uniwue.info6.parser.structures.OperatorStructure;
import de.uniwue.info6.parser.structures.PrefixOperatorStructure;
import de.uniwue.info6.parser.structures.Structure;
import de.uniwue.info6.parser.structures.SubqueryStructure;
import de.uniwue.info6.parser.structures.TableStructure;

/**
 * Visitor for conditions.
 * 
 * @author Christian
 * 
 */
public class WhereClauseVisitor implements Visitor {

	private Structure conditionTree;
	private Structure leftOperand;
	private Structure rightOperand;
	private LinkedList<TableStructure> tables = new LinkedList<TableStructure>();

	private boolean operator = false;

	@SuppressWarnings("unused")
	private boolean stopChilds = false;

	/* (non-Javadoc)
	 * @see com.akiban.sql.parser.Visitor#visit(com.akiban.sql.parser.Visitable)
	 */
	@Override
	public Visitable visit(Visitable node) throws StandardException {

		if (node instanceof BinaryOperatorNode) {

			operator = true;

			visit2(((BinaryOperatorNode) node).getLeftOperand());
			visit2(((BinaryOperatorNode) node).getRightOperand());

			conditionTree = new OperatorStructure(
					((BinaryOperatorNode) node).getOperator(), leftOperand,
					rightOperand);

			stopChilds = true;

		} else if (node instanceof AggregateNode) {
			visit((AggregateNode) node);
		} else if (node instanceof StaticMethodCallNode) {
			visit((StaticMethodCallNode) node);
		} else if (node instanceof TernaryOperatorNode) {

			operator = true;

			visit2(((TernaryOperatorNode) node).getReceiver());
			visit2(((TernaryOperatorNode) node).getLeftOperand());

			conditionTree = new OperatorStructure(
					((TernaryOperatorNode) node).getOperator(), leftOperand,
					rightOperand);

			stopChilds = true;

		} else if (node instanceof UnaryOperatorNode) {

			operator = true;

			LinkedList<Structure> operands = new LinkedList<Structure>();

			visit2(((UnaryOperatorNode) node).getOperand());

			operands.add(leftOperand);

			conditionTree = new PrefixOperatorStructure(
					((UnaryOperatorNode) node).getOperator(), operands);

			stopChilds = true;

		} else if (node instanceof ConstantNode) {
			visit((ConstantNode) node);
		} else if (node instanceof ColumnReference) {
			visit((ColumnReference) node);
		} else if (node instanceof SQLToJavaValueNode) {
			visit((SQLToJavaValueNode) node);
		} else if (node instanceof JavaToSQLValueNode) {
			visit((JavaToSQLValueNode) node);
		} else if (node instanceof InListOperatorNode) {
				
				operator = true;

				LinkedList<Structure> operands = new LinkedList<Structure>();

				for(ValueNode item : ((RowConstructorNode) ((InListOperatorNode) node).getRightOperandList()).getNodeList()){
					if(item instanceof NumericConstantNode){
						operands.add(new Structure("" + ((NumericConstantNode) item).getValue()));
						
					} else {
						visit2(item);
					}
				}

				conditionTree = new PrefixOperatorStructure(
						"IN", operands);

				stopChilds = true;
				
		} else if (node instanceof SubqueryNode) {
			visit((SubqueryNode) node);
		} else
			throw new StandardException("Unbekannter Parameter: \n "
					+ node.getClass() + "\n" + node.toString());

		return node;

	}

	/**
	 * @param node
	 * @throws StandardException
	 */
	public void visit(StaticMethodCallNode node) throws StandardException {

		StaticMethodCallNodeVisitor tvisitor = new StaticMethodCallNodeVisitor();
		node.accept(tvisitor);

		PrefixOperatorStructure tmp = tvisitor.getConditionTree();

		setOperand(tmp);

	}
	
	/**
	 * @param node
	 * @throws StandardException
	 */
	public void visit(SubqueryNode node) throws StandardException {

		RootVisitor rvisitor = new RootVisitor();
		node.accept(rvisitor);

		tables.addAll(rvisitor.getTables());
		
		SubqueryStructure tmp = new SubqueryStructure("subtree", rvisitor);

		setOperand(tmp);

	}

	/**
	 * @param node
	 * @throws StandardException
	 */
	public void visit(SQLToJavaValueNode node) throws StandardException {

		WhereClauseVisitor wvisitor = new WhereClauseVisitor();
		node.getSQLValueNode().accept(wvisitor);
		
		tables.addAll(wvisitor.getTables());

		Structure tmp = wvisitor.getConditionTree();

		setOperand(tmp);
	}

	/**
	 * @param node
	 * @throws StandardException
	 */
	public void visit(JavaToSQLValueNode node) throws StandardException {

		WhereClauseVisitor wvisitor = new WhereClauseVisitor();
		node.getJavaValueNode().accept(wvisitor);
		
		tables.addAll(wvisitor.getTables());

		Structure tmp = wvisitor.getConditionTree();

		setOperand(tmp);
	}

	/**
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
	 * @param node
	 */
	public void visit(ConstantNode node) {
		Structure tmp = new Structure(node.getValue().toString());
		setOperand(tmp);
	}

	/**
	 * @param node
	 */
	public void visit(ColumnReference node) {
		ColumnStructure tmp = new ColumnStructure(node.getColumnName(),
				node.getTableName());
		setOperand(tmp);
	}

	/**
	 * @param node
	 * @throws StandardException
	 */
	public void visit(BinaryOperatorNode node) throws StandardException {

		WhereClauseVisitor wvisitor = new WhereClauseVisitor();
		node.accept(wvisitor);
		
		tables.addAll(wvisitor.getTables());

		Structure tmp = wvisitor.getConditionTree();

		setOperand(tmp);

	}

	/**
	 * Delegates operands to a WhereClauseVisitor
	 * 
	 * @param node
	 * @throws StandardException
	 */
	public void visit2(ValueNode node) throws StandardException {

		WhereClauseVisitor wvisitor = new WhereClauseVisitor();
		node.accept(wvisitor);
		
		tables.addAll(wvisitor.getTables());

		Structure tmp = wvisitor.getConditionTree();

		setOperand(tmp);

	}

	/**
	 * @param tmp
	 */
	public void setOperand(Structure tmp) {

		if (operator) {

			if (leftOperand == null) {
				leftOperand = tmp;
			} else if (rightOperand == null) {
				rightOperand = tmp;
			}

		} else {

			conditionTree = tmp;
			stopChilds = true;

		}

	}
	
	/**
	 * 
	 * @return
	 */
	public LinkedList<TableStructure> getTables() {
		return tables;
	}

	/**
	 * @return
	 */
	public Structure getConditionTree() {
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
		return true;
	}

}
