package com.netbrasoft.gnuob.shop.wishlist;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

@SuppressWarnings("unchecked")
@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class WishListPanel extends Panel {

   private static final long serialVersionUID = 2034566325989232879L;

   private WishListViewPanel wishListViewPanel = new WishListViewPanel("wishListViewPanel", (IModel<Shopper>) getDefaultModel());

   public WishListPanel(final String id, final IModel<Shopper> model) {
      super(id, model);
   }

   @Override
   protected void onInitialize() {
      add(wishListViewPanel.add(wishListViewPanel.new WishtListViewFragment()).setOutputMarkupId(true));
      super.onInitialize();
   }
}
