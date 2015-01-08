package de.uniwue.info6.misc.properties;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  Cfg.java
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.ArrayUtils;

import de.uniwue.info6.misc.StringTools;

/**
 *
 *
 * @author
 */
public class Cfg {
  private static Cfg instance;

  private Map<PropertiesFile, String> properties;
  private Map<PropertiesFile, Properties> cachedProperties;

  private final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(getClass());

  public static final String
  RESOURCE_PATH           = "scn";

  /**
   * Let's test some shit!
   */
  // public static void main(String[] args) {
  //   // @formatter:off
  //   Map<PropertiesFile, String> configFiles = ImmutableMap.of(
  //       PropertiesFile.MAIN_CONFIG,     "src/main/resources/config.properties",
  //       PropertiesFile.GERMAN_LANGUAGE, "src/main/resources/text_de.properties"
  //   );
  //   // @formatter:on

  //   PropertiesManager manager = PropertiesManager.inst(configFiles);
  //   String admins = manager.getProp(PropertiesFile.MAIN_CONFIG, PropString.ADMINS);
  //   manager.setProp(PropertiesFile.MAIN_CONFIG, PropString.ADMINS, admins + "_additional_string");
  // }

  /**
   *
   *
   * @return
   */
  public static Cfg inst() {
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
  public static Cfg inst(Map<PropertiesFile, String> properties) {
    if (instance == null) {
      instance = new Cfg(properties);
    }
    return instance;
  }

  /**
   * @param mainPropertiesFilePath
   *
   */
  private Cfg(Map<PropertiesFile, String> properties) {
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
        if (propertiesFile.exists()) {
          this.checkIfValidPropertiesFile(propertiesFile);
          Properties currentProperties = new PropertiesLoader(propertiesFile.getAbsolutePath())
          .getProperties();
          final int fileCountBefore = this.cachedProperties.size();
          this.cachedProperties.put(prop, currentProperties);
          if (fileCountBefore < this.cachedProperties.size()) {
            System.err.println("INFO (ueps): Load '" + propertiesFile.getName() + "' from\n     '" +
                               StringTools.shortenUnixHomePath(propertiesFile.getParent()) + "'");
          }
        }
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
  public String getProp(PropertiesFile file, PropString stringProperty, String... snippets) {
    final String prop = getProp(file, stringProperty.name());
    if (snippets.length == 0) {
      return prop;
    } else {
      return MessageFormat.format(prop, (Object[]) snippets);
    }
  }


  /**
   *
   *
   * @param stringProperty
   * @return
   */
  public String getText(final String ... stringProperty) {
    return this.getProp(PropertiesFile.DEF_LANGUAGE, stringProperty);
  }

  /**
   *
   *
   * @param file
   * @param stringProperty
   * @return
   */
  public String getProp(PropertiesFile file, final String ... stringProperty) {
    String propFilePath = properties.get(file);
    Properties prop = getPropertiesFromFile(propFilePath);

    if (stringProperty != null) {
      String trimmedString = prop.getProperty(stringProperty[0].trim());
      if (trimmedString != null) {
        trimmedString = trimmedString.trim();
        if (stringProperty.length > 1) {
          final String[] snippets = ArrayUtils.remove(stringProperty, 0);
          return MessageFormat.format(trimmedString, (Object[]) snippets);
        } else {
          return trimmedString;
        }
      }

    }



    LOG.error("Can't find Property: \n\"" + stringProperty + "\"\nin properties-file:\n\""
              + propFilePath + "\"\nPossible version mismatch?");
    return "[ERROR]";
  }
}
