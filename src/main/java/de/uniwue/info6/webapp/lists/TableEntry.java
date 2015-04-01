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

  private String value_00, value_01, value_02, value_03, value_04, value_05,
          value_06, value_07, value_08, value_09, value_10, value_11,
          value_12, value_13, value_14, value_15, value_16, value_17,
          value_18, value_19, value_20, value_21, value_22, value_23,
          value_24, value_25, value_26, value_27, value_28, value_29,
          value_30, value_31, value_32, value_33, value_34, value_35,
          value_36, value_37, value_38, value_39, value_40, value_41,
          value_42, value_43, value_44, value_45, value_46, value_47,
          value_48, value_49, value_50;

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
    case 20:
      value_20 = value;
      break;
    case 21:
      value_21 = value;
      break;
    case 22:
      value_22 = value;
      break;
    case 23:
      value_23 = value;
      break;
    case 24:
      value_24 = value;
      break;
    case 25:
      value_25 = value;
      break;
    case 26:
      value_26 = value;
      break;
    case 27:
      value_27 = value;
      break;
    case 28:
      value_28 = value;
      break;
    case 29:
      value_29 = value;
      break;
    case 30:
      value_30 = value;
      break;
    case 31:
      value_31 = value;
      break;
    case 32:
      value_32 = value;
      break;
    case 33:
      value_33 = value;
      break;
    case 34:
      value_34 = value;
      break;
    case 35:
      value_35 = value;
      break;
    case 36:
      value_36 = value;
      break;
    case 37:
      value_37 = value;
      break;
    case 38:
      value_38 = value;
      break;
    case 39:
      value_39 = value;
      break;
    case 40:
      value_40 = value;
      break;
    case 41:
      value_41 = value;
      break;
    case 42:
      value_42 = value;
      break;
    case 43:
      value_43 = value;
      break;
    case 44:
      value_44 = value;
      break;
    case 45:
      value_45 = value;
      break;
    case 46:
      value_46 = value;
      break;
    case 47:
      value_47 = value;
      break;
    case 48:
      value_48 = value;
      break;
    case 49:
      value_49 = value;
      break;
    case 50:
      value_50 = value;
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
   * @param value_02
   *            the value_02 to set
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
   * @param value_03
   *            the value_03 to set
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
   * @param value_04
   *            the value_04 to set
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
   * @param value_05
   *            the value_05 to set
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
   * @param value_06
   *            the value_06 to set
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
   * @param value_07
   *            the value_07 to set
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
   * @param value_08
   *            the value_08 to set
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
   * @param value_09
   *            the value_09 to set
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
   * @param value_10
   *            the value_10 to set
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
   * @param value_11
   *            the value_11 to set
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
   * @param value_12
   *            the value_12 to set
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
   * @param value_13
   *            the value_13 to set
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
   * @param value_14
   *            the value_14 to set
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
   * @param value_15
   *            the value_15 to set
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
   * @param value_16
   *            the value_16 to set
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
   * @param value_17
   *            the value_17 to set
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
   * @param value_18
   *            the value_18 to set
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
   * @param value_19
   *            the value_19 to set
   */
  public void setValue_19(String value_19) {
    this.value_19 = value_19;
  }

  /**
   * @return the value_20
   */
  public String getValue_20() {
    return value_20;
  }

  /**
   * @param value_20
   *            the value_20 to set
   */
  public void setValue_20(String value_20) {
    this.value_20 = value_20;
  }

  /**
   * @return the value_21
   */
  public String getValue_21() {
    return value_21;
  }

  /**
   * @param value_21
   *            the value_21 to set
   */
  public void setValue_21(String value_21) {
    this.value_21 = value_21;
  }

  /**
   * @return the value_22
   */
  public String getValue_22() {
    return value_22;
  }

  /**
   * @param value_22
   *            the value_22 to set
   */
  public void setValue_22(String value_22) {
    this.value_22 = value_22;
  }

  /**
   * @return the value_23
   */
  public String getValue_23() {
    return value_23;
  }

  /**
   * @param value_23
   *            the value_23 to set
   */
  public void setValue_23(String value_23) {
    this.value_23 = value_23;
  }

  /**
   * @return the value_24
   */
  public String getValue_24() {
    return value_24;
  }

  /**
   * @param value_24
   *            the value_24 to set
   */
  public void setValue_24(String value_24) {
    this.value_24 = value_24;
  }

  /**
   * @return the value_25
   */
  public String getValue_25() {
    return value_25;
  }

  /**
   * @param value_25
   *            the value_25 to set
   */
  public void setValue_25(String value_25) {
    this.value_25 = value_25;
  }

  /**
   * @return the value_26
   */
  public String getValue_26() {
    return value_26;
  }

  /**
   * @param value_26
   *            the value_26 to set
   */
  public void setValue_26(String value_26) {
    this.value_26 = value_26;
  }

  /**
   * @return the value_27
   */
  public String getValue_27() {
    return value_27;
  }

  /**
   * @param value_27
   *            the value_27 to set
   */
  public void setValue_27(String value_27) {
    this.value_27 = value_27;
  }

  /**
   * @return the value_28
   */
  public String getValue_28() {
    return value_28;
  }

  /**
   * @param value_28
   *            the value_28 to set
   */
  public void setValue_28(String value_28) {
    this.value_28 = value_28;
  }

  /**
   * @return the value_29
   */
  public String getValue_29() {
    return value_29;
  }

  /**
   * @param value_29
   *            the value_29 to set
   */
  public void setValue_29(String value_29) {
    this.value_29 = value_29;
  }

  /**
   * @return the value_30
   */
  public String getValue_30() {
    return value_30;
  }

  /**
   * @param value_30
   *            the value_30 to set
   */
  public void setValue_30(String value_30) {
    this.value_30 = value_30;
  }

  /**
   * @return the value_31
   */
  public String getValue_31() {
    return value_31;
  }

  /**
   * @param value_31 the value_31 to set
   */
  public void setValue_31(String value_31) {
    this.value_31 = value_31;
  }

  /**
   * @return the value_32
   */
  public String getValue_32() {
    return value_32;
  }

  /**
   * @param value_32 the value_32 to set
   */
  public void setValue_32(String value_32) {
    this.value_32 = value_32;
  }

  /**
   * @return the value_33
   */
  public String getValue_33() {
    return value_33;
  }

  /**
   * @param value_33 the value_33 to set
   */
  public void setValue_33(String value_33) {
    this.value_33 = value_33;
  }

  /**
   * @return the value_34
   */
  public String getValue_34() {
    return value_34;
  }

  /**
   * @param value_34 the value_34 to set
   */
  public void setValue_34(String value_34) {
    this.value_34 = value_34;
  }

  /**
   * @return the value_35
   */
  public String getValue_35() {
    return value_35;
  }

  /**
   * @param value_35 the value_35 to set
   */
  public void setValue_35(String value_35) {
    this.value_35 = value_35;
  }

  /**
   * @return the value_36
   */
  public String getValue_36() {
    return value_36;
  }

  /**
   * @param value_36 the value_36 to set
   */
  public void setValue_36(String value_36) {
    this.value_36 = value_36;
  }

  /**
   * @return the value_37
   */
  public String getValue_37() {
    return value_37;
  }

  /**
   * @param value_37 the value_37 to set
   */
  public void setValue_37(String value_37) {
    this.value_37 = value_37;
  }

  /**
   * @return the value_38
   */
  public String getValue_38() {
    return value_38;
  }

  /**
   * @param value_38 the value_38 to set
   */
  public void setValue_38(String value_38) {
    this.value_38 = value_38;
  }

  /**
   * @return the value_39
   */
  public String getValue_39() {
    return value_39;
  }

  /**
   * @param value_39 the value_39 to set
   */
  public void setValue_39(String value_39) {
    this.value_39 = value_39;
  }

  /**
   * @return the value_40
   */
  public String getValue_40() {
    return value_40;
  }

  /**
   * @param value_40 the value_40 to set
   */
  public void setValue_40(String value_40) {
    this.value_40 = value_40;
  }

  /**
   * @return the value_41
   */
  public String getValue_41() {
    return value_41;
  }

  /**
   * @param value_41 the value_41 to set
   */
  public void setValue_41(String value_41) {
    this.value_41 = value_41;
  }

  /**
   * @return the value_42
   */
  public String getValue_42() {
    return value_42;
  }

  /**
   * @param value_42 the value_42 to set
   */
  public void setValue_42(String value_42) {
    this.value_42 = value_42;
  }

  /**
   * @return the value_43
   */
  public String getValue_43() {
    return value_43;
  }

  /**
   * @param value_43 the value_43 to set
   */
  public void setValue_43(String value_43) {
    this.value_43 = value_43;
  }

  /**
   * @return the value_44
   */
  public String getValue_44() {
    return value_44;
  }

  /**
   * @param value_44 the value_44 to set
   */
  public void setValue_44(String value_44) {
    this.value_44 = value_44;
  }

  /**
   * @return the value_45
   */
  public String getValue_45() {
    return value_45;
  }

  /**
   * @param value_45 the value_45 to set
   */
  public void setValue_45(String value_45) {
    this.value_45 = value_45;
  }

  /**
   * @return the value_46
   */
  public String getValue_46() {
    return value_46;
  }

  /**
   * @param value_46 the value_46 to set
   */
  public void setValue_46(String value_46) {
    this.value_46 = value_46;
  }

  /**
   * @return the value_47
   */
  public String getValue_47() {
    return value_47;
  }

  /**
   * @param value_47 the value_47 to set
   */
  public void setValue_47(String value_47) {
    this.value_47 = value_47;
  }

  /**
   * @return the value_48
   */
  public String getValue_48() {
    return value_48;
  }

  /**
   * @param value_48 the value_48 to set
   */
  public void setValue_48(String value_48) {
    this.value_48 = value_48;
  }

  /**
   * @return the value_49
   */
  public String getValue_49() {
    return value_49;
  }

  /**
   * @param value_49 the value_49 to set
   */
  public void setValue_49(String value_49) {
    this.value_49 = value_49;
  }

  /**
   * @return the value_50
   */
  public String getValue_50() {
    return value_50;
  }

  /**
   * @param value_50 the value_50 to set
   */
  public void setValue_50(String value_50) {
    this.value_50 = value_50;
  }

}
