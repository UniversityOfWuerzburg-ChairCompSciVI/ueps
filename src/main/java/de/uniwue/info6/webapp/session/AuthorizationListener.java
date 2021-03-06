package de.uniwue.info6.webapp.session;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  AuthorizationListener.java
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

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 *
 * @author Michael
 */

public class AuthorizationListener implements PhaseListener, Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private final static String loginPage = "login";


  /**
   *
   */
  public AuthorizationListener() {
    //
  }

  /**
   * {@inheritDoc}
   * @see PhaseListener#afterPhase(PhaseEvent)
   */
  public void afterPhase(PhaseEvent event) {
    // redirect user on ajax request if session is invalid
    SessionObject ac = SessionObject.pullFromSession();
    if (ac == null) {
      loginUser(event.getFacesContext());
    }
  }

  /**
   *
   *
   * @param facesContext
   */
  private void loginUser(FacesContext facesContext) {
    NavigationHandler nh = facesContext.getApplication().getNavigationHandler();
    nh.handleNavigation(facesContext, null, loginPage);
  }


  /**
   * {@inheritDoc}
   * @see PhaseListener#beforePhase(PhaseEvent)
   */
  public void beforePhase(PhaseEvent event) {
  }

  /**
   * {@inheritDoc}
   * @see PhaseListener#getPhaseId()
   */
  public PhaseId getPhaseId() {
    return PhaseId.RESTORE_VIEW;
  }
}
