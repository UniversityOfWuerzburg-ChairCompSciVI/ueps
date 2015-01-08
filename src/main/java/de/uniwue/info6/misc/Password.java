package de.uniwue.info6.misc;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  Password.java
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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 *
 * @author Michael
 */
public class Password {
  final String password;

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 31).append(password).toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (!(obj instanceof Password))
      return false;

    Password pass = (Password) obj;
    return new EqualsBuilder().append(password, pass).isEquals();
  }

  /**
   *
   *
   * @param password
   */
  public Password(String password) {
    this.password = password;
  }

  /**
   * {@inheritDoc}
   *
   * @see Object#toString()
   */
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < password.length(); i++)
      sb.append("*");
    return sb.toString();
  }
}
