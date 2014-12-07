package de.uniwue.info6.database.map.conf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import de.uniwue.info6.misc.properties.PropString;
import de.uniwue.info6.misc.properties.PropertiesFile;
import de.uniwue.info6.misc.properties.PropertiesManager;

/**
 *
 *
 * @author Michael
 */
public class HibernateUtil {

  private static final SessionFactory sessionFactory = buildSessionFactory();
  private static final Log LOGGER = LogFactory.getLog(HibernateUtil.class);

  /**
   *
   *
   * @return
   */
  private static SessionFactory buildSessionFactory() {
    try {
      PropertiesManager prop = PropertiesManager.inst();

      Configuration hibernate = new Configuration().configure("hibernate.cfg.xml");
      hibernate.setProperty("hibernate.bytecode.use_reflection_optimizer", "false");
      hibernate.setProperty("hibernate.search.autoregister_listeners", "false");
      hibernate.setProperty("hibernate.current_session_context_class", "thread");
      hibernate.setProperty("hibernate.connection.driver_class", "org.mariadb.jdbc.Driver");
      hibernate.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect");

      hibernate.setProperty("hibernate.connection.url", "jdbc:mariadb://"
          + prop.getProp(PropertiesFile.MAIN_CONFIG, PropString.MAIN_DBHOST) + ":"
          + prop.getProp(PropertiesFile.MAIN_CONFIG, PropString.MAIN_DBPORT)
          + "?autoReconnect=true");

      hibernate.setProperty("hibernate.default_catalog", prop.getProp(PropertiesFile.MAIN_CONFIG,
          PropString.MAIN_DBNAME));
      hibernate.setProperty("hibernate.connection.username", prop.getProp(
          PropertiesFile.MAIN_CONFIG, PropString.MAIN_DBUSER));
      hibernate.setProperty("hibernate.connection.password", prop.getProp(
          PropertiesFile.MAIN_CONFIG, PropString.MAIN_DBPASS));

      StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
      serviceRegistryBuilder.applySettings(hibernate.getProperties());
      ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();
      SessionFactory sessionFactory = hibernate.buildSessionFactory(serviceRegistry);

      return sessionFactory;
    } catch (Exception ex) {
      LOGGER.error("Initial SessionFactory creation failed.", ex);
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
