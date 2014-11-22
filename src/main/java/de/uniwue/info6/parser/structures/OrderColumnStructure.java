package de.uniwue.info6.parser.structures;

/**
 * MySQL ORDER BY
 * 
 * @author Christian
 *
 */
public class OrderColumnStructure extends ColumnStructure {

	private boolean isAscending = true;

	public OrderColumnStructure(Structure value2, String tableName,
			boolean isAscending) {
		super(value2, tableName);
		this.isAscending = isAscending;
	}

	public OrderColumnStructure(String value, String tableName,
			boolean isAscending) {
		super(value, tableName);
		this.isAscending = isAscending;
	}

	public boolean isAcending() {
		return isAscending;
	}

	@Override
	public boolean equals(Structure anotherStructure) {

		if (anotherStructure instanceof OrderColumnStructure)
			return super.equals(anotherStructure)
					&& isAscending == ((OrderColumnStructure) anotherStructure)
							.isAcending();

		return false;

	}

	@Override
	public String toString() {
		return super.toString() + (isAscending ? " ASC" : " DESC");
	}

}
