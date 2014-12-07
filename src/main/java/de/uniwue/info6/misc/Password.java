package de.uniwue.info6.misc;

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
