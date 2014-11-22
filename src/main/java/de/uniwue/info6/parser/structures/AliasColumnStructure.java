package de.uniwue.info6.parser.structures;

/**
 * MySQL ALIAS
 * 
 * @author Christian
 *
 */
public class AliasColumnStructure extends ColumnStructure {

	private String aliasValue;

	public AliasColumnStructure(Structure value2, String tableName,
			String aliasValue) {
		super(value2, tableName);
		this.aliasValue = aliasValue;
	}

	public AliasColumnStructure(String value, String tableName,
			String aliasValue) {
		super(value, tableName);
		this.aliasValue = aliasValue;
	}

	public String getAliasValue() {
		return aliasValue;
	}

	@Override
	public boolean equals(Structure anotherStructure) {

		if (anotherStructure instanceof AliasColumnStructure)
			return super.equals(anotherStructure)
					&& aliasValue
							.equals(((AliasColumnStructure) anotherStructure)
									.getAliasValue());

		return false;

	}

	@Override
	public String toString() {
		return super.toString() + " AS " + aliasValue;
	}

}
