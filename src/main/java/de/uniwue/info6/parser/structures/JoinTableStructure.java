package de.uniwue.info6.parser.structures;

/**
 * MySQL JOIN
 * 
 * @author Christian
 *
 */
public class JoinTableStructure extends TableStructure {

	private TableStructure leftTable;
	private String joinType;
	private TableStructure rightTable;
	private Structure joinClause;

	public JoinTableStructure(TableStructure leftTable,
			TableStructure rightTable, Structure joinClause, String joinType) {

		super("");
		this.leftTable = leftTable;
		this.rightTable = rightTable;
		this.joinClause = joinClause;
		this.joinType = joinType;

	}

	@Override
	public String toString() {
		return joinType + "(" + leftTable.toString() + ", "
				+ rightTable.toString() + ") ON " + joinClause.toString();
	}

	@Override
	public boolean equals(Structure anotherStructure) {

		if (anotherStructure instanceof JoinTableStructure) {
			return leftTable.equals(((JoinTableStructure) anotherStructure)
					.getLeftTable())
					&& rightTable
							.equals(((JoinTableStructure) anotherStructure)
									.getRightTable())
					&& joinClause
							.equals(((JoinTableStructure) anotherStructure)
									.getJoinClause())
					&& joinType.equals(((JoinTableStructure) anotherStructure)
							.getJoinType());
		}

		return false;

	}

	private String getJoinType() {
		return joinType;
	}

	public TableStructure getLeftTable() {
		return leftTable;
	}

	public TableStructure getRightTable() {
		return rightTable;
	}

	public Structure getJoinClause() {
		return joinClause;
	}

}
