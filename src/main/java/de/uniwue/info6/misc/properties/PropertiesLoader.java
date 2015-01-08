package de.uniwue.info6.misc.properties;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  PropertiesLoader.java
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
