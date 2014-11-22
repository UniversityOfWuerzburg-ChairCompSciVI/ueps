package de.uniwue.info6.webapp.session;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 *
 * @author Michael
 */
@ManagedBean(name = "startup_error")
@ViewScoped
public class StartupError implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static final Log LOGGER = LogFactory.getLog(StartupError.class);
	private String error;



	/**
	 *
	 */
	public StartupError() {
	}

	/**
	 *
	 *
	 */
	@PostConstruct
	public void init() {
		error = "Server start error:<br/>" + AuthorizationFilter.errorDescription;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}

}
