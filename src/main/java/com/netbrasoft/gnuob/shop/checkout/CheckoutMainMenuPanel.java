package com.netbrasoft.gnuob.shop.checkout;

import java.util.ArrayList;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.netbrasoft.gnuob.shop.page.tab.AccountTab;
import com.netbrasoft.gnuob.shop.page.tab.HomeTab;
import com.netbrasoft.gnuob.shop.page.tab.WishListTab;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.BootstrapTabbedPanel;

@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class CheckoutMainMenuPanel extends Panel {

  @AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
  class MainMenuTabbedPanel extends BootstrapTabbedPanel<ITab> {

    private static final long serialVersionUID = 6838221105862530322L;

    public MainMenuTabbedPanel() {
      super("mainMenuTabbedPanel", new ArrayList<ITab>());
    }

    @Override
    public String getTabContainerCssClass() {
      return "nav nav-pills nav-justified";
    }
  }

  private static final long serialVersionUID = 4037036072135523233L;

  private final MainMenuTabbedPanel mainMenuTabbedPanel;

  public CheckoutMainMenuPanel(final String id, final IModel<Shopper> model) {
    super(id, model);

    mainMenuTabbedPanel = new MainMenuTabbedPanel();
  }

  @Override
  protected void onInitialize() {
    mainMenuTabbedPanel.getTabs().add(new HomeTab(Model.of(getString("homeMessage", new Model<String>(), "HOME").toUpperCase())));
    mainMenuTabbedPanel.getTabs().add(new AccountTab(Model.of(getString("accountMessage", new Model<String>(), "ACCOUNT").toUpperCase())));
    mainMenuTabbedPanel.getTabs().add(new WishListTab(Model.of(getString("wishListMessage", new Model<String>(), "OFFER").toUpperCase())));
    mainMenuTabbedPanel.getTabs().add(new CheckoutTab(Model.of(getString("checkoutMessage", new Model<String>(), "ORDERS").toUpperCase())));
    mainMenuTabbedPanel.setSelectedTab(3);

    add(mainMenuTabbedPanel.setOutputMarkupId(true));
    super.onInitialize();
  }
}
