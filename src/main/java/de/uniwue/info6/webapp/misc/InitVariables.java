package de.uniwue.info6.webapp.misc;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  InitVariables.java
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.URL;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;

import com.google.common.collect.ImmutableMap;

import de.uniwue.info6.database.jdbc.ConnectionTools;
import de.uniwue.info6.database.map.conf.HibernateUtil;
import de.uniwue.info6.misc.StringTools;
import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.misc.properties.PropBool;
import de.uniwue.info6.misc.properties.PropString;
import de.uniwue.info6.misc.properties.PropertiesFile;

/**
 *
 *
 * @author Michael
 */
public class InitVariables implements ServletContextListener, Serializable {

  private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(InitVariables.class);

  private static final String
  FILE_PATTERN      = "%n%-5p: %d{yyyy-MM-dd HH:mm:ss} -- %c{1}%n[Thread:%t][%l]%n%m%n",
  CONFIG_FILE_NAME  = "config.properties",
  TEXT_FILE_NAME    = "text_de.properties",
  LOG_FILE_NAME_OFF = "ueps-webapp-offline.log",
  LOG_FILE_NAME     = "ueps-webapp.log",
  CONSOLE_PATTERN   = StringUtils.repeat("-", 60) +
                      "%n%-5p: %d{yyyy-MM-dd HH:mm:ss} -- %c{1}%n[Thread:%t][%l]%n%m%n" +
                      StringUtils.repeat("-", 60) + "%n";

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
        LOGGER.info(String.format("deregistering jdbc driver: %s", driver));
      } catch (SQLException e) {
        LOGGER.error(String.format("Error deregistering driver %s", driver), e);
      }
    }
  }

  /**
   * @param event
   */
  @Override
  public void contextInitialized(final ServletContextEvent event) {
    try {
      this.initPropertyManager(false);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   *
   *
   */
  public void initPropertyManager(boolean offline) throws Exception {
    final URL config = this.getClass().getResource("/" + CONFIG_FILE_NAME);
    final URL text = this.getClass().getResource("/" + TEXT_FILE_NAME);

    if (config == null) {
      throw new FileNotFoundException("'" + CONFIG_FILE_NAME + "' NOT FOUND IN CLASSPATH");
    }

    if (text == null) {
      throw new FileNotFoundException("'" + TEXT_FILE_NAME + "' NOT FOUND IN CLASSPATH");
    }

    Map<PropertiesFile, String> configFiles = ImmutableMap.of(
          PropertiesFile.MAIN_CONFIG,  this.getClass().getResource("/config.properties").getFile().toString(),
          PropertiesFile.DEF_LANGUAGE, this.getClass().getResource("/text_de.properties").getFile().toString()
        );
    Cfg.inst(configFiles);

    if (Cfg.inst().getProp(PropertiesFile.MAIN_CONFIG, PropBool.DEBUG_MODE)) {
      this.setDebugVariables();
    }

    this.checkProperties();
    this.initLogger(offline);
  }

  /**
   *
   *
   */
  private void checkProperties() {
    try {
      final String resourceDirPath = Cfg.inst().getProp(PropertiesFile.MAIN_CONFIG, PropString.SCENARIO_RESOURCES_PATH);
      File resourceDir = null;

      if (resourceDirPath == null || resourceDirPath.trim().isEmpty()) {
        resourceDir = new File(this.getClass().getResource("/" + CONFIG_FILE_NAME).getFile()).getParentFile();
        if (resourceDir.exists()) {
          Cfg.inst().setProp(PropertiesFile.MAIN_CONFIG, PropString.SCENARIO_RESOURCES_PATH, resourceDir.getAbsolutePath());
        }
      } else {
        resourceDir = new File(resourceDirPath);
      }

      final String logPath = Cfg.inst().getProp(PropertiesFile.MAIN_CONFIG, PropString.LOG_PATH);
      if (logPath == null || logPath.trim().isEmpty()) {
        File logFile = new File(resourceDir, "log");
        Cfg.inst().setProp(PropertiesFile.MAIN_CONFIG, PropString.LOG_PATH, logFile.getAbsolutePath());
      }

    } catch (Exception e) {
      // TODO: logging
      e.printStackTrace();
    }
  }

  /**
   *
   *
   * @param offlineMode
   */
  private void initLogger(boolean offlineMode) {
    FileAppender fileAppender = new FileAppender();
    final String logDirPath = Cfg.inst().getProp(PropertiesFile.MAIN_CONFIG, PropString.LOG_PATH);
    File logFile = null;


    if (!logDirPath.trim().isEmpty()) {
      logFile = new File(Cfg.inst().getProp(PropertiesFile.MAIN_CONFIG, PropString.LOG_PATH),
                         offlineMode ? LOG_FILE_NAME_OFF : LOG_FILE_NAME);
      if (!logFile.getParentFile().exists()) {
        logFile.getParentFile().mkdir();
      }
    }

    if (logFile != null && logFile.getParentFile().exists()) {
      System.err.println("INFO (ueps): Init logger with filepath\n"
                         + "     '" + StringTools.shortenUnixHomePath(logFile.getAbsolutePath()) + "'");

      fileAppender.setFile(logFile.getAbsolutePath());

      fileAppender.setLayout(new PatternLayout(FILE_PATTERN));
      fileAppender.setThreshold(Level.INFO);
      fileAppender.setAppend(true);
      fileAppender.activateOptions();
      org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();
      logger.addAppender(fileAppender);

      ConsoleAppender console = new ConsoleAppender();
      console.setLayout(new PatternLayout(CONSOLE_PATTERN));
      console.setThreshold(Level.ERROR);
      console.setTarget(ConsoleAppender.SYSTEM_ERR);
      console.activateOptions();
      logger.addAppender(console);

      // LOGGER.info("this is a logger test");
    }
  }

  /**
   *
   *
   */
  private void setDebugVariables() {
    Cfg.inst().setProp(PropertiesFile.MAIN_CONFIG, PropString.MASTER_DBHOST, "127.0.0.1");
    Cfg.inst().setProp(PropertiesFile.MAIN_CONFIG, PropString.MASTER_DBPORT, "3306");
    Cfg.inst().setProp(PropertiesFile.MAIN_CONFIG, PropString.MASTER_DBNAME, "ueps_master");
    Cfg.inst().setProp(PropertiesFile.MAIN_CONFIG, PropString.MASTER_DBUSER, "test_user");
    Cfg.inst().setProp(PropertiesFile.MAIN_CONFIG, PropString.MASTER_DBPASS, "3ti4k4tm270kg");

    Cfg.inst().setProp(PropertiesFile.MAIN_CONFIG, PropBool.IMPORT_DB_IF_EMPTY, true);
    Cfg.inst().setProp(PropertiesFile.MAIN_CONFIG, PropBool.IMPORT_EXAMPLE_SCENARIO, true);
    Cfg.inst().setProp(PropertiesFile.MAIN_CONFIG, PropBool.FORCE_RESET_DATABASE, true);
    Cfg.inst().setProp(PropertiesFile.MAIN_CONFIG, PropBool.USE_MOODLE_LOGIN, false);

  }
}
