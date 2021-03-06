package de.uniwue.info6.misc;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  TicToc.java
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


/**
 *
 *
 * @author
 */
public class TicToc {

  public static long startTime;

  /**
   *
   *
   */
  public static void toc() {
    toc("");
  }

  /**
   *
   *
   * @param pointName
   */
  public static void tic() {
    startTime = System.currentTimeMillis();
  }

  /**
   *
   *
   * @param pointName
   */
  public static void toc(String pointName) {
    long elapsedTime = System.currentTimeMillis() - startTime;
    System.out.println(String.format("Elapsed time [" + pointName + "]: %d.%03dsec", elapsedTime / 1000, elapsedTime % 1000));
  }

  /**
   *
   *
   * @return
   */
  public static long getElapsedTimeInMillis() {
    long elapsedTime = System.currentTimeMillis() - startTime;
    return elapsedTime;
  }
}
