package de.uniwue.info6.webapp.session;

import java.io.Serializable;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 *
 *
 * @author Michael
 */

public class AuthorizationListener implements PhaseListener, Serializable
{

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private final static String loginPage = "login";


  /**
   *
   */
  public AuthorizationListener()
  {
    //
  }

  /**
   * {@inheritDoc}
   * @see PhaseListener#afterPhase(PhaseEvent)
   */
  public void afterPhase(PhaseEvent event)
  {
    // redirect user on ajax request if session is invalid
    SessionObject ac = new SessionCollector().getSessionObject();
    if (ac == null)
    {
      loginUser(event.getFacesContext());
    }
  }

  /**
   *
   *
   * @param facesContext
   */
  private void loginUser(FacesContext facesContext)
  {
    NavigationHandler nh = facesContext.getApplication().getNavigationHandler();
    nh.handleNavigation(facesContext, null, loginPage);
  }


  /**
   * {@inheritDoc}
   * @see PhaseListener#beforePhase(PhaseEvent)
   */
  public void beforePhase(PhaseEvent event)
  {
    //
  }

  /**
   * {@inheritDoc}
   * @see PhaseListener#getPhaseId()
   */
  public PhaseId getPhaseId()
  {
    return PhaseId.RESTORE_VIEW;
  }
}
