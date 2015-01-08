package de.uniwue.info6.misc;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  Crypt.java
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

import java.security.MessageDigest;

public class Crypt {

  public static String md5(String s) {
    if (s == null)
      return null;
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      md5.reset();
      md5.update(s.getBytes());
      byte[] tmp = md5.digest();

      StringBuffer res = new StringBuffer();
      for (int i = 0; i < tmp.length; i++) {
        String hex = Integer.toHexString(0xFF & tmp[i]);
        if (hex.length() < 2)
          hex = "0" + hex;
        res.append(hex);
      }

      return res.toString();
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }
}
