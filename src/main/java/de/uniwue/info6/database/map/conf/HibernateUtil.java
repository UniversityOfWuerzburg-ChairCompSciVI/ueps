package de.uniwue.info6.database.map.conf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

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
      Configuration config = new Configuration().configure("hibernate.cfg.xml");

      config.setProperty("hibernate.bytecode.use_reflection_optimizer", "false");
      config.setProperty("hibernate.search.autoregister_listeners", "false");
      config.setProperty("hibernate.current_session_context_class", "thread");
      // config.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
      // config.setProperty("hibernate.connection.driver_class", "org.drizzle.jdbc.DrizzleDriver");
      config.setProperty("hibernate.connection.driver_class", "org.mariadb.jdbc.Driver");
      config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect");
      // config.setProperty("hibernate.connection.url", "jdbc:mysql://" + System.getProperty("MAIN_DBHOST") + ":"
      // config.setProperty("hibernate.connection.url", "jdbc:mysql:thin://" + System.getProperty("MAIN_DBHOST") + ":"
      config.setProperty("hibernate.connection.url", "jdbc:mariadb://" + System.getProperty("MAIN_DBHOST") + ":"
          + System.getProperty("MAIN_DBPORT") + "?autoReconnect=true");
      config.setProperty("hibernate.default_catalog", System.getProperty("MAIN_DBNAME"));
      config.setProperty("hibernate.connection.username", System.getProperty("MAIN_DBUSER"));
      config.setProperty("hibernate.connection.password", System.getProperty("MAIN_DBPASS"));

      StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();

      serviceRegistryBuilder.applySettings(config.getProperties());
      ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();
      SessionFactory sessionFactory = config.buildSessionFactory(serviceRegistry);

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
