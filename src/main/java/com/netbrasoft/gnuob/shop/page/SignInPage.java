package com.netbrasoft.gnuob.shop.page;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;

public class SignInPage extends BasePage {

  private static final long serialVersionUID = -8219855140262365434L;

  private boolean isSignedIn() {
    return AuthenticatedWebSession.get().isSignedIn();
  }

  @Override
  protected void onConfigure() {
    super.onConfigure();
    if (!isSignedIn()) {
      final String site = getRequest().getClientUrl().getHost();
      signIn(System.getProperty("gnuob." + site + ".username", "guest"), System.getProperty("gnuob." + site + ".password", "guest"));
    }
    setResponsePage(getApplication().getHomePage());
  }

  private boolean signIn(final String username, final String password) {
    return AuthenticatedWebSession.get().signIn(username, password);
  }
}
