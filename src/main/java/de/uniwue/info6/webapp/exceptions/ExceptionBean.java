package de.uniwue.info6.webapp.exceptions;

import java.io.Serializable;
import java.sql.SQLException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 *
 * @author Michael
 */
@ManagedBean
@RequestScoped
public class ExceptionBean implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public void throwRuntimeException() {
        throw new RuntimeException("RUNTIME-ERROR");
    }

    public void throwSQLException() throws SQLException {
        throw new SQLException("DB FAIL");
    }
}
