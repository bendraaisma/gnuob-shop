package com.netbrasoft.gnuob.shop.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.wicketstuff.wicket.mount.core.annotation.MountPath;

import com.netbrasoft.gnuob.shop.security.ShopRoles;

@MountPath("store.html")
@AuthorizeInstantiation({ ShopRoles.GUEST })
public class StorePage extends BasePage {

   private static final long serialVersionUID = -7721060791790055851L;

}
