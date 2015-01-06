package de.uniwue.info6.misc;

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
