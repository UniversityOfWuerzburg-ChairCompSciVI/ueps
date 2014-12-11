package de.uniwue.info6.webapp.misc;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.common.collect.ImmutableMap;

import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.misc.properties.PropertiesFile;

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
