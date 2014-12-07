package de.uniwue.info6.webapp.misc;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.common.collect.ImmutableMap;

import de.uniwue.info6.misc.OldPropertiesManager;
import de.uniwue.info6.misc.properties.PropString;
import de.uniwue.info6.misc.properties.PropertiesFile;
import de.uniwue.info6.misc.properties.PropertiesManager;

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
    //
  }

  /**
   * @param event
   */
  @Override
  public void contextInitialized(final ServletContextEvent event) {
    this.initPropertyManager();

    // hier lassen sich startscripte einfuegen
    OldPropertiesManager pr = OldPropertiesManager.instance();
    pr.loadProperties("config.properties");
    pr.loadProperties("text_de.properties");
  }

  /**
   *
   *
   */
  private void initPropertyManager() {
    // @formatter:off
    Map<PropertiesFile, String> configFiles = ImmutableMap.of(
        PropertiesFile.MAIN_CONFIG,     this.getClass().getResource("/config.properties").getFile().toString(),
        PropertiesFile.GERMAN_LANGUAGE, this.getClass().getResource("/text_de.properties").getFile().toString()
    );
    // @formatter:on
    PropertiesManager manager = PropertiesManager.inst(configFiles);

    System.out.println(manager.getProp(PropertiesFile.GERMAN_LANGUAGE, "HELP"));
  }
}
