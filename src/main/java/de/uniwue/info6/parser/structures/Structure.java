package de.uniwue.info6.parser.structures;

import de.uniwue.info6.comparator.SqlQuery;
import de.uniwue.info6.comparator.SqlQueryComparator;

/**
 * 
 * @author Christian
 *
 */
public class Structure {

	protected String value;
	protected SqlQueryComparator comparator;

	public Structure(String value) {
		this.value = SqlQuery.dejustPlainString(value);
	}

	public String getValue() {
		return value;
	}
	
	public boolean equals(Structure anotherStructure, SqlQueryComparator comparator) {
		this.comparator = comparator;
		return equals(anotherStructure);
	}

	public boolean equals(Structure anotherStructure) {
		return value.equals(anotherStructure.getValue());
	}

	public String toString() {
		return value;
	}

}
