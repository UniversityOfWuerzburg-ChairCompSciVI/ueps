package de.uniwue.info6.misc.properties;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 *
 * @author
 */
public class PropertiesLoader {
  private String configFile;
  private Properties properties;

  /**
   *
   */
  public PropertiesLoader(String configFile) {
    this.configFile = configFile;
  }

  /**
   *
   *
   * @return
   *
   * @throws IOException
   */
  public Properties getProperties() throws IOException {
    if (configFile != null) {
      this.properties = new Properties();
      BufferedInputStream stream = new BufferedInputStream(new FileInputStream(configFile));
      this.properties.load(stream);
      stream.close();
      return properties;
    }
    return null;
  }
}
