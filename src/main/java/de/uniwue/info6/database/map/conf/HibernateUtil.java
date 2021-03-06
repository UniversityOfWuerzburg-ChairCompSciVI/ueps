package de.uniwue.info6.database.map.conf;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  HibernateUtil.java
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

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import de.uniwue.info6.misc.properties.Cfg;
import de.uniwue.info6.misc.properties.PropString;
import de.uniwue.info6.misc.properties.PropertiesFile;

/**
 *
 *
 * @author Michael
 */
public class HibernateUtil {

  private static final SessionFactory sessionFactory = buildSessionFactory();
  private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(HibernateUtil.class);

  /**
   *
   *
   * @return
   */
  private static SessionFactory buildSessionFactory() {
    try {
      Cfg prop = Cfg.inst();

      Configuration hibernate = new Configuration().configure("hibernate.cfg.xml");
      hibernate.setProperty("hibernate.bytecode.use_reflection_optimizer", "false");
      hibernate.setProperty("hibernate.search.autoregister_listeners", "false");
      hibernate.setProperty("hibernate.current_session_context_class", "thread");
      hibernate.setProperty("hibernate.connection.driver_class", "org.mariadb.jdbc.Driver");
      hibernate.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect");

      hibernate.setProperty("hibernate.connection.url", "jdbc:mariadb://"
                            + prop.getProp(PropertiesFile.MAIN_CONFIG, PropString.MASTER_DBHOST) + ":"
                            + prop.getProp(PropertiesFile.MAIN_CONFIG, PropString.MASTER_DBPORT)
                            + "?autoReconnect=true");

      hibernate.setProperty("hibernate.default_catalog", prop.getProp(PropertiesFile.MAIN_CONFIG,
                            PropString.MASTER_DBNAME));
      hibernate.setProperty("hibernate.connection.username", prop.getProp(
                              PropertiesFile.MAIN_CONFIG, PropString.MASTER_DBUSER));
      hibernate.setProperty("hibernate.connection.password", prop.getProp(
                              PropertiesFile.MAIN_CONFIG, PropString.MASTER_DBPASS));

      StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
      serviceRegistryBuilder.applySettings(hibernate.getProperties());
      ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();
      SessionFactory sessionFactory = hibernate.buildSessionFactory(serviceRegistry);

      return sessionFactory;
    } catch (Exception ex) {
      if (LOGGER != null) {
        LOGGER.error("Initial SessionFactory creation failed.", ex);
      }
      throw new ExceptionInInitializerError(ex);
    }
  }

  /**
   *
   *
   * @return
   */
  public static SessionFactory getSessionFactory() {
    return sessionFactory;
  }


  /**
   *
   *
   */
  public static void shutdown() {
    getSessionFactory().close();
  }
}
