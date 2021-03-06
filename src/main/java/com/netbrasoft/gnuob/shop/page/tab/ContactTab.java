package com.netbrasoft.gnuob.shop.page.tab;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.netbrasoft.gnuob.api.Category;
import com.netbrasoft.gnuob.shop.panel.ContactPanel;

public class ContactTab extends AbstractTab {

  private static final long serialVersionUID = 4835579949680085443L;

  public ContactTab(final IModel<String> title) {
    super(title);
  }

  @Override
  public WebMarkupContainer getPanel(final String panelId) {
    return new ContactPanel(panelId, Model.of(new Category()));
  }
}
