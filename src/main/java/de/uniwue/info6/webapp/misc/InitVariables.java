package de.uniwue.info6.webapp.misc;

import java.io.Serializable;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import de.uniwue.info6.misc.PropertiesManager;

/**
 *
 *
 * @author Michael
 */
public class InitVariables implements ServletContextListener, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param event
	 */
	@Override
	public void contextDestroyed(final ServletContextEvent event) {
		//
	}

	/**
	 * @param event
	 */
	@Override
	public void contextInitialized(final ServletContextEvent event) {
		// hier lassen sich startscripte einfuegen
		PropertiesManager pr = PropertiesManager.instance();
		pr.loadProperties("config.properties");
		pr.loadProperties("text_de.properties");
	}
}
