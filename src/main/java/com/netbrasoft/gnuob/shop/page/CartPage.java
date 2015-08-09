package com.netbrasoft.gnuob.shop.page;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.model.Model;
import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import com.netbrasoft.gnuob.shop.border.ContentBorder;
import com.netbrasoft.gnuob.shop.cart.CartMainMenuPanel;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

@MountPath("cart.html")
@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class CartPage extends BasePage {

   private static final long serialVersionUID = -7854507374209656133L;

   private CartMainMenuPanel mainMenuPanel = new CartMainMenuPanel("mainMenuPanel", Model.of(new Shopper()));

   private ContentBorder contentBorder = new ContentBorder("contentBorder");

   @Override
   protected void onInitialize() {
      contentBorder.add(mainMenuPanel);
      add(contentBorder);

      super.onInitialize();
   }
}
