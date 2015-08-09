package com.netbrasoft.gnuob.shop.specification;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.netbrasoft.gnuob.shop.generic.GenericTypeCacheDataProvider;
import com.netbrasoft.gnuob.shop.security.ShopRoles;
import com.netbrasoft.gnuob.shop.shopper.Shopper;

@AuthorizeAction(action = Action.RENDER, roles = { ShopRoles.GUEST })
public class SpecificationPanel extends Panel {

   private static final long serialVersionUID = -2071564475086309712L;

   private final SpecificationViewPanel specificationViewPanel = new SpecificationViewPanel("specificationViewPanel", Model.of(new Shopper()));

   @SpringBean(name = "ShopperDataProvider", required = true)
   private GenericTypeCacheDataProvider<Shopper> shopperDataProvider;

   public SpecificationPanel(final String id, final IModel<Shopper> model) {
      super(id, model);
   }

   @Override
   protected void onInitialize() {
      if (shopperDataProvider.find(new Shopper()).getCart().getRecords().isEmpty()) {
         throw new RedirectToUrlException("cart.html");
      }

      add(specificationViewPanel.add(specificationViewPanel.new SpecificationViewFragement()).setOutputMarkupId(true));
      super.onInitialize();
   }
}
