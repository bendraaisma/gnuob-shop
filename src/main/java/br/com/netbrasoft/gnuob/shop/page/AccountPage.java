package br.com.netbrasoft.gnuob.shop.page;

import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SHOPPER_DATA_PROVIDER_NAME;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import br.com.netbrasoft.gnuob.shop.account.AccountMainMenuPanel;
import br.com.netbrasoft.gnuob.shop.border.ContentBorder;
import br.com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import br.com.netbrasoft.gnuob.shop.security.ShopRoles;
import br.com.netbrasoft.gnuob.shop.shopper.Shopper;

@MountPath(AccountPage.ACCOUNT_HTML_VALUE)
@AuthorizeAction(action = Action.RENDER, roles = {ShopRoles.GUEST})
public class AccountPage extends BasePage {

  private static final String CONTENT_BORDER_ID = "contentBorder";

  private static final String MAIN_MENU_PANEL_ID = "mainMenuPanel";

  public static final String ACCOUNT_HTML_VALUE = "account.html";

  private static final long serialVersionUID = 4051343927877779621L;

  private final AccountMainMenuPanel mainMenuPanel;

  private final ContentBorder contentBorder;

  @SpringBean(name = SHOPPER_DATA_PROVIDER_NAME, required = true)
  private transient GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

  public AccountPage() {
    mainMenuPanel = new AccountMainMenuPanel(MAIN_MENU_PANEL_ID);
    contentBorder = new ContentBorder(CONTENT_BORDER_ID, Model.of(new Shopper()));
  }

  @Override
  protected void onInitialize() {
    contentBorder.setDefaultModelObject(shopperDataProvider.find(new Shopper()));
    contentBorder.add(mainMenuPanel);
    add(contentBorder);
    super.onInitialize();
  }
}
