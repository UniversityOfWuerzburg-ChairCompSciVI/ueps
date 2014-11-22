package de.uniwue.info6.parser.structures;

/**
 * MySQL COLUMNS
 * 
 * @author Christian
 *
 */
public class ColumnStructure extends Structure {

	protected Structure value2;
	protected String tableName;

	public ColumnStructure(Structure value2, String tableName) {
		super("");
		this.value2 = value2;
		this.tableName = tableName;
	}

	public ColumnStructure(String value, String tableName) {
		super(value);
		this.tableName = tableName;
	}

	public Structure getValue2() {
		return value2;
	}

	public String getTableName() {
		return tableName;
	}

	@Override
	public boolean equals(Structure anotherStructure) {

		if (value2 != null && anotherStructure instanceof ColumnStructure)
			return value2.equals(((ColumnStructure) anotherStructure)
					.getValue2());
		else
			return value.equals(anotherStructure.getValue());

	}

	@Override
	public String toString() {

		if (value2 != null)
			return (tableName != null && tableName != "" ? tableName + "." : "")
					+ value2.toString();
		else
			return (tableName != null  && tableName != "" ? tableName + "." : "")
					+ super.toString();

	}

}
