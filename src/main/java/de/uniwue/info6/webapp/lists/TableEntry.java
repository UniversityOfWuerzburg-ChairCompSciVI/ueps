package de.uniwue.info6.webapp.lists;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  TableEntry.java
 * ************************************************************************
 * %%
 * Copyright (C) 2014 - 2015 Institute of Computer Science, University of Wuerzburg
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author Michael
 */
public class TableEntry implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private List<String> columnNames;
	private List<String> columnValues;

	private String value_00, value_01, value_02, value_03, value_04, value_05, value_06, value_07, value_08, value_09,
			value_10, value_11, value_12, value_13, value_14, value_15, value_16, value_17, value_18, value_19;

	/**
	 *
	 */
	public TableEntry(List<String> columnNames) {
		columnValues = new ArrayList<String>();
		this.columnNames = columnNames;
	}

	/**
	 *
	 *
	 * @param value
	 */
	public void addValue(String value, Integer index) {
		columnValues.add(value);

		switch (index) {
		case 0:
			value_00 = value;
			break;
		case 1:
			value_01 = value;
			break;
		case 2:
			value_02 = value;
			break;
		case 3:
			value_03 = value;
			break;
		case 4:
			value_04 = value;
			break;
		case 5:
			value_05 = value;
			break;
		case 6:
			value_06 = value;
			break;
		case 7:
			value_07 = value;
			break;
		case 8:
			value_08 = value;
			break;
		case 9:
			value_09 = value;
			break;
		case 10:
			value_10 = value;
			break;
		case 11:
			value_11 = value;
			break;
		case 12:
			value_12 = value;
			break;
		case 13:
			value_13 = value;
			break;
		case 14:
			value_14 = value;
			break;
		case 15:
			value_15 = value;
			break;
		case 16:
			value_16 = value;
			break;
		case 17:
			value_17 = value;
			break;
		case 18:
			value_18 = value;
			break;
		case 19:
			value_19 = value;
			break;
		}
	}

	/**
	 *
	 *
	 * @param column
	 */
	public String getValueByName(String column) {
		if (columnNames != null && columnValues != null) {
			if (columnNames.contains(column)) {
				int index = columnNames.indexOf(column);
				if (columnValues.size() > index) {
					return columnValues.get(index);
				}
			}
		}
		return null;
	}

	/**
	 *
	 *
	 * @param column
	 */
	public String getValue(Integer colIndex) {
		if (columnNames != null && columnValues != null) {
			if (columnValues.size() > colIndex) {
				return columnValues.get(colIndex);
			}
		}
		return null;
	}

	/**
	 * @return the value_00
	 */
	public String getValue_00() {
		return value_00;
	}

	/**
	 * @param value_00
	 *            the value_00 to set
	 */
	public void setValue_00(String value_00) {
		this.value_00 = value_00;
	}

	/**
	 * @return the value_01
	 */
	public String getValue_01() {
		return value_01;
	}

	/**
	 * @param value_01
	 *            the value_01 to set
	 */
	public void setValue_01(String value_01) {
		this.value_01 = value_01;
	}

	/**
	 * @return the value_02
	 */
	public String getValue_02() {
		return value_02;
	}

	/**
	 * @param value_02 the value_02 to set
	 */
	public void setValue_02(String value_02) {
		this.value_02 = value_02;
	}

	/**
	 * @return the value_03
	 */
	public String getValue_03() {
		return value_03;
	}

	/**
	 * @param value_03 the value_03 to set
	 */
	public void setValue_03(String value_03) {
		this.value_03 = value_03;
	}

	/**
	 * @return the value_04
	 */
	public String getValue_04() {
		return value_04;
	}

	/**
	 * @param value_04 the value_04 to set
	 */
	public void setValue_04(String value_04) {
		this.value_04 = value_04;
	}

	/**
	 * @return the value_05
	 */
	public String getValue_05() {
		return value_05;
	}

	/**
	 * @param value_05 the value_05 to set
	 */
	public void setValue_05(String value_05) {
		this.value_05 = value_05;
	}

	/**
	 * @return the value_06
	 */
	public String getValue_06() {
		return value_06;
	}

	/**
	 * @param value_06 the value_06 to set
	 */
	public void setValue_06(String value_06) {
		this.value_06 = value_06;
	}

	/**
	 * @return the value_07
	 */
	public String getValue_07() {
		return value_07;
	}

	/**
	 * @param value_07 the value_07 to set
	 */
	public void setValue_07(String value_07) {
		this.value_07 = value_07;
	}

	/**
	 * @return the value_08
	 */
	public String getValue_08() {
		return value_08;
	}

	/**
	 * @param value_08 the value_08 to set
	 */
	public void setValue_08(String value_08) {
		this.value_08 = value_08;
	}

	/**
	 * @return the value_09
	 */
	public String getValue_09() {
		return value_09;
	}

	/**
	 * @param value_09 the value_09 to set
	 */
	public void setValue_09(String value_09) {
		this.value_09 = value_09;
	}

	/**
	 * @return the value_10
	 */
	public String getValue_10() {
		return value_10;
	}

	/**
	 * @param value_10 the value_10 to set
	 */
	public void setValue_10(String value_10) {
		this.value_10 = value_10;
	}

	/**
	 * @return the value_11
	 */
	public String getValue_11() {
		return value_11;
	}

	/**
	 * @param value_11 the value_11 to set
	 */
	public void setValue_11(String value_11) {
		this.value_11 = value_11;
	}

	/**
	 * @return the value_12
	 */
	public String getValue_12() {
		return value_12;
	}

	/**
	 * @param value_12 the value_12 to set
	 */
	public void setValue_12(String value_12) {
		this.value_12 = value_12;
	}

	/**
	 * @return the value_13
	 */
	public String getValue_13() {
		return value_13;
	}

	/**
	 * @param value_13 the value_13 to set
	 */
	public void setValue_13(String value_13) {
		this.value_13 = value_13;
	}

	/**
	 * @return the value_14
	 */
	public String getValue_14() {
		return value_14;
	}

	/**
	 * @param value_14 the value_14 to set
	 */
	public void setValue_14(String value_14) {
		this.value_14 = value_14;
	}

	/**
	 * @return the value_15
	 */
	public String getValue_15() {
		return value_15;
	}

	/**
	 * @param value_15 the value_15 to set
	 */
	public void setValue_15(String value_15) {
		this.value_15 = value_15;
	}

	/**
	 * @return the value_16
	 */
	public String getValue_16() {
		return value_16;
	}

	/**
	 * @param value_16 the value_16 to set
	 */
	public void setValue_16(String value_16) {
		this.value_16 = value_16;
	}

	/**
	 * @return the value_17
	 */
	public String getValue_17() {
		return value_17;
	}

	/**
	 * @param value_17 the value_17 to set
	 */
	public void setValue_17(String value_17) {
		this.value_17 = value_17;
	}

	/**
	 * @return the value_18
	 */
	public String getValue_18() {
		return value_18;
	}

	/**
	 * @param value_18 the value_18 to set
	 */
	public void setValue_18(String value_18) {
		this.value_18 = value_18;
	}

	/**
	 * @return the value_19
	 */
	public String getValue_19() {
		return value_19;
	}

	/**
	 * @param value_19 the value_19 to set
	 */
	public void setValue_19(String value_19) {
		this.value_19 = value_19;
	}

}
