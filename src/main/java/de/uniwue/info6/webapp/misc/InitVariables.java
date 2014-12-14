package de.uniwue.info6.webapp.misc;

import java.io.Serializable;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.common.collect.ImmutableMap;

import de.uniwue.info6.database.jdbc.ConnectionTools;
import de.uniwue.info6.database.map.conf.HibernateUtil;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.misc.properties.PropertiesFile;

/**
 *
 *
 * @author Michael
 */
public class InitVariables implements ServletContextListener, Serializable {

  private final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(getClass());

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param event
   */
  @Override
  public void contextDestroyed(final ServletContextEvent event) {
    HibernateUtil.shutdown();
    ConnectionTools.inst().cleanUp();

    Enumeration<Driver> drivers = DriverManager.getDrivers();
    while (drivers.hasMoreElements()) {
        Driver driver = drivers.nextElement();
        try {
            DriverManager.deregisterDriver(driver);
            LOG.info(String.format("deregistering jdbc driver: %s", driver));
        } catch (SQLException e) {
            LOG.error(String.format("Error deregistering driver %s", driver), e);
        }
    }
  }

  /**
   * @param event
   */
  @Override
  public void contextInitialized(final ServletContextEvent event) {
    this.initPropertyManager();
  }

  /**
   *
   *
   */
  private void initPropertyManager() {
    // @formatter:off
    Map<PropertiesFile, String> configFiles = ImmutableMap.of(
        PropertiesFile.MAIN_CONFIG,     this.getClass().getResource("/config.properties").getFile().toString(),
        PropertiesFile.DEF_LANGUAGE, this.getClass().getResource("/text_de.properties").getFile().toString()
    );
    // @formatter:on
    Cfg.inst(configFiles);
  }
}
