package de.uniwue.info6.parser.structures;

/**
 * MySQL UPDATE COLUMN
 * 
 * @author Christian
 *
 */
public class UpdateColumnStructure extends ColumnStructure {

	private String setValue;

	public UpdateColumnStructure(Structure value2, String tableName,
			String setValue) {

		super(value2, tableName);
		this.setValue = setValue;

	}

	public UpdateColumnStructure(String value, String tableName, String setValue) {

		super(value, tableName);
		this.setValue = setValue;

	}

	@Override
	public boolean equals(Structure anotherStructure) {

		if (anotherStructure instanceof UpdateColumnStructure)
			return super.equals(anotherStructure)
					&& setValue
							.equals(((UpdateColumnStructure) anotherStructure)
									.getSetValue());

		return false;

	}

	public String getSetValue() {
		return setValue;
	}

	@Override
	public String toString() {
		return super.toString() + " = " + setValue;
	}

}
