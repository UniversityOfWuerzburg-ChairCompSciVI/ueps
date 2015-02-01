package de.uniwue.info6.webapp.session;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  StartupError.java
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

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;




/**
 *
 *
 * @author Michael
 */
@ManagedBean(name = "startup_error")
@ViewScoped
public class StartupError implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  @SuppressWarnings("unused")
  private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(StartupError.class);
  private String error;



  /**
   *
   */
  public StartupError() {
  }

  /**
   *
   *
   */
  @PostConstruct
  public void init() {
    error = "<span style='font-size:20px;font-weight:bold'>Server start error:</span><br/>" + AuthorizationFilter.errorDescription;
  }

  /**
   * @return the error
   */
  public String getError() {
    return error;
  }

  /**
   * @param error the error to set
   */
  public void setError(String error) {
    this.error = error;
  }

}
