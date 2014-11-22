package de.uniwue.info6.misc;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.uniwue.info6.webapp.misc.InitVariables;

/**
 *
 *
 * @author Michael
 */
public class PropertiesManager {

	private static PropertiesManager instance;
	private static final Log LOGGER = LogFactory.getLog(PropertiesManager.class);

	/**
	 *
	 */
	private PropertiesManager() {
	}

	/**
	 *
	 *
	 * @return
	 */
	public static synchronized PropertiesManager instance() {
		if (instance == null) {
			instance = new PropertiesManager();
		}
		return instance;
	}

	/**
	 *
	 *
	 * @param name
	 */
	public synchronized PropertiesManager loadProperties(String name) {
		final Properties propsFromFile = new Properties();
		try {
			propsFromFile.load(InitVariables.class.getClassLoader().getResourceAsStream(name));
			for (String prop : propsFromFile.stringPropertyNames()) {
				if (System.getProperty(prop) == null) {
					String p = propsFromFile.getProperty(prop).trim();
					if (p.startsWith("~/")) {
						p = p.replace("~/", System.getProperty("user.home") + "/");
					}
					System.setProperty(prop, p);
				}
			}

		} catch (final Exception e) {
			LOGGER.error("LOADING PROPERTIES FILE WITH NAME \"" + name + "\" FAILED!", e);
		}
		return instance();
	}
}
