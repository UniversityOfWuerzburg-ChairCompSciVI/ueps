package de.uniwue.info6.misc.properties;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.ImmutableMap;

/**
 *
 *
 * @author
 */
public class PropertiesManager {
  private static PropertiesManager instance;

  private Map<PropertiesFile, String> properties;
  private Map<PropertiesFile, Properties> cachedProperties;

  private final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(getClass());

  /**
   * Let's test some shit!
   */
  public static void main(String[] args) {
    // @formatter:off
    Map<PropertiesFile, String> configFiles = ImmutableMap.of(
        PropertiesFile.MAIN_CONFIG,     "src/main/resources/config.properties",
        PropertiesFile.GERMAN_LANGUAGE, "src/main/resources/text_de.properties"
    );
    // @formatter:on

    PropertiesManager manager = PropertiesManager.inst(configFiles);
    String admins = manager.getProp(PropertiesFile.MAIN_CONFIG, PropString.ADMINS);
    manager.setProp(PropertiesFile.MAIN_CONFIG, PropString.ADMINS, admins + "_additional_string");
  }

  /**
   *
   *
   * @return
   */
  public static PropertiesManager inst() {
    if (instance == null) {
      throw new NullPointerException("Not initialized!");
    }
    return instance;
  }

  /**
   *
   *
   * @param mainPropertiesFilePath
   * @return
   */
  public static PropertiesManager inst(Map<PropertiesFile, String> properties) {
    if (instance == null) {
      instance = new PropertiesManager(properties);
    }
    return instance;
  }

  /**
   * @param mainPropertiesFilePath
   *
   */
  private PropertiesManager(Map<PropertiesFile, String> properties) {
    this.cachedProperties = new HashMap<PropertiesFile, Properties>();
    this.properties = properties;
    this.updateFileProperties();
  }

  /**
   *
   *
   * @param property
   */
  private void checkIfValidPropertiesFile(File property) {
    if (!property.exists() || !property.isFile()) {
      throw new IllegalArgumentException("Not a valid property file: \""
          + property.getAbsolutePath() + "\"\n");
    } else if (!property.canRead() || !property.canWrite()) {
      throw new IllegalArgumentException(
          "Check writing and reading permissions for property file: \""
              + property.getAbsolutePath() + "\"\n");
    }
  }

  /**
   * @throws IOException
   *
   *
   */
  private void updateFileProperties() {
    try {
      for (PropertiesFile prop : properties.keySet()) {
        File propertiesFile = new File(properties.get(prop));
        this.checkIfValidPropertiesFile(propertiesFile);
        Properties currentProperties = new PropertiesLoader(propertiesFile.getAbsolutePath())
            .getProperties();
        this.cachedProperties.put(prop, currentProperties);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   *
   *
   * @param file
   * @return
   */
  public Properties getPropertiesFromFile(String file) {
    Properties prop = null;
    try {
      prop = new PropertiesLoader(file).getProperties();
    } catch (Exception e) {
      // TODO: logging
      e.printStackTrace();
    }
    return prop;
  }

  // ------------------------------------------------ //
  // -- modifying properties:
  // ------------------------------------------------ //

  /**
   *
   *
   * @param property
   * @param bool
   */
  public boolean setProp(PropertiesFile file, PropBool property, boolean bool) {
    if (bool != getProp(file, property)) {
      this.setProp(file, property.name(), String.valueOf(bool).toLowerCase());
      return true;
    }
    return false;
  }

  /**
   *
   *
   * @param property
   * @param bool
   */
  public boolean setProp(PropertiesFile file, PropInteger property, int integer) {
    if (integer != getProp(file, property)) {
      this.setProp(file, property.name(), String.valueOf(integer));
      return true;
    }
    return false;
  }

  /**
   *
   *
   * @param property
   * @param bool
   */
  public boolean setProp(PropertiesFile file, PropString property, String string) {
    if (!string.equals(getProp(file, property))) {
      this.setProp(file, property.name(), string);
      return true;
    }
    return false;
  }

  /**
   * @param configFilePath
   * @param property
   * @param value
   *
   *
   */
  private void setProp(PropertiesFile file, String property, String value) {
    checkNotNull(property);
    checkNotNull(value);

    PropertiesChanger propertiesChanger = new PropertiesChanger(true);
    value = value.replace("\\", "\\\\");

    FileInputStream in = null;
    FileOutputStream out = null;

    String configFilePath = properties.get(file);

    try {
      in = new FileInputStream(configFilePath);
      propertiesChanger.load(in);
      propertiesChanger.setProperty(property, value);
      out = new FileOutputStream(configFilePath);
      propertiesChanger.save(out);
      this.updateFileProperties();
    } catch (Exception e) {
      // TODO: logging
      e.printStackTrace();
    } finally {
      try {
        if (in != null) {
          in.close();
        }
        if (out != null) {
          out.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  // ------------------------------------------------ //
  // -- retrieving properties:
  // ------------------------------------------------ //

  /**
   *
   *
   * @param property
   * @return
   */
  public boolean getProp(PropertiesFile file, PropBool boolProperty) {
    String propFilePath = properties.get(file);
    Properties prop = getPropertiesFromFile(propFilePath);
    if (boolProperty != null) {
      String par = prop.getProperty(boolProperty.name().trim());
      if (par != null) {
        if (par.trim().equalsIgnoreCase("true"))
          return true;
        else
          return false;
      }
    }

    LOG.error("Can't find Property: \n\"" + boolProperty.name() + "\"\nin properties-file:\n\""
        + propFilePath + "\"\nPossible version mismatch?");
    throw new NullPointerException();
  }

  /**
   *
   *
   * @param intProperty
   * @return
   */
  public int getProp(PropertiesFile file, PropInteger intProperty) {
    String propFilePath = properties.get(file);
    Properties prop = getPropertiesFromFile(propFilePath);
    if (intProperty != null) {
      String par = prop.getProperty(intProperty.name().trim());
      if (par != null)
        if (!par.trim().isEmpty()) {
          try {
            return Integer.parseInt(par.trim());
          } catch (NumberFormatException e) {
            return -1;
          }
        } else {
          return -1;
        }
    }
    LOG.error("Can't find Property: \n\"" + intProperty.name() + "\"\nin properties-file:\n\""
        + propFilePath + "\"\nPossible version mismatch?");
    throw new NullPointerException();
  }

  /**
   *
   *
   * @param stringProperty
   * @return
   */
  public String getProp(PropertiesFile file, PropString stringProperty) {
    return getProp(file, stringProperty.name());
  }

  /**
   *
   *
   * @param file
   * @param stringProperty
   * @return
   */
  public String getProp(PropertiesFile file, String stringProperty) {
    String propFilePath = properties.get(file);
    Properties prop = getPropertiesFromFile(propFilePath);

    if (stringProperty != null) {
      String trimmedString = prop.getProperty(stringProperty.trim());
      if (trimmedString != null) {
        return trimmedString.trim();
      }
    }

    LOG.error("Can't find Property: \n\"" + stringProperty + "\"\nin properties-file:\n\""
        + propFilePath + "\"\nPossible version mismatch?");
    throw new NullPointerException();
  }
}
