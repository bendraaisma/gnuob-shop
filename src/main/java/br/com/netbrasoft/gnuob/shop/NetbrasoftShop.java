/*
 * Copyright 2016 Netbrasoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package br.com.netbrasoft.gnuob.shop;

import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CDNJS_CLOUDFLARE_COM;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.FALSE;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CDN_ENABLED_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.CDN_URL_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.SITE_ENCRYPTION_KEY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.INSPECTOR_PAGE;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.JAVASCRIPT_RESOURCE_FILTER_NAME;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.WICKET_APPLICATION;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.getProperty;
import static java.lang.Boolean.valueOf;
import static org.apache.wicket.RuntimeConfigurationType.DEVELOPMENT;
import static org.apache.wicket.settings.SecuritySettings.DEFAULT_ENCRYPTION_KEY;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Page;
import org.apache.wicket.bean.validation.BeanValidationConfiguration;
import org.apache.wicket.devutils.inspector.InspectorPage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.pages.PageExpiredErrorPage;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.crypt.CachingSunJceCryptFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.wicketstuff.wicket.servlet3.auth.ServletContainerAuthenticatedWebApplication;
import org.wicketstuff.wicket.servlet3.auth.ServletContainerAuthenticatedWebSession;

import br.com.netbrasoft.gnuob.api.generic.converter.XmlGregorianCalendarConverter;
import br.com.netbrasoft.gnuob.shop.authorization.AppServletContainerAuthenticatedWebSession;
import br.com.netbrasoft.gnuob.shop.html.NetbrasoftShopThemeProvider;
import br.com.netbrasoft.gnuob.shop.page.MainPage;
import br.com.netbrasoft.gnuob.shop.page.SignInPage;
import br.com.netbrasoft.gnuob.shop.page.error.AccessDeniedPage;
import br.com.netbrasoft.gnuob.shop.page.error.InternalErrorPage;
import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.markup.html.RenderJavaScriptToFooterHeaderResponseDecorator;
import de.agilecoders.wicket.core.settings.BootstrapSettings;
import de.agilecoders.wicket.core.settings.CookieThemeProvider;
import de.agilecoders.wicket.webjars.WicketWebjars;
import de.agilecoders.wicket.webjars.settings.WebjarsSettings;
import net.ftlines.wicketsource.WicketSource;

@EnableCaching
@Service(WICKET_APPLICATION)
public class NetbrasoftShop extends ServletContainerAuthenticatedWebApplication {

  private static final BootstrapSettings BOOTSTRAP_SETTINGS = new BootstrapSettings();
  private static final WebjarsSettings WEBJARS_SETTINGS = new WebjarsSettings();

  static {
    BOOTSTRAP_SETTINGS.useCdnResources(valueOf(getProperty(CDN_ENABLED_KEY, FALSE)));
    BOOTSTRAP_SETTINGS.setJsResourceFilterName(JAVASCRIPT_RESOURCE_FILTER_NAME);
    BOOTSTRAP_SETTINGS.setThemeProvider(NetbrasoftShopThemeProvider.getInstance());
    BOOTSTRAP_SETTINGS.setActiveThemeProvider(new CookieThemeProvider());
    WEBJARS_SETTINGS.cdnUrl(getProperty(CDN_URL_KEY, CDNJS_CLOUDFLARE_COM));
    WEBJARS_SETTINGS.useCdnResources(valueOf(getProperty(CDN_ENABLED_KEY, FALSE)));
  }

  @Override
  protected void init() {
    super.init();
    initDeploymentSettings();
  }

  private void initDeploymentSettings() {
    installBootstrapSettings();
    installWebjarsSettings();
    setupApplicationSettings();
    setupBeanValidationSettings();
    setupSecurityCryptoFactorySettings();
    setupJavaScriptToFooterHeaderResponseDecorator();
    setupSpringCompInjectorForCompInstantListeners();
    setupDevelopmentModeSettings();
  }

  private void installBootstrapSettings() {
    Bootstrap.install(this, BOOTSTRAP_SETTINGS);
  }

  private void installWebjarsSettings() {
    WicketWebjars.install(this, WEBJARS_SETTINGS);
  }

  private void setupApplicationSettings() {
    getApplicationSettings().setUploadProgressUpdatesEnabled(true);
    getApplicationSettings().setInternalErrorPage(InternalErrorPage.class);
    getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);
    getApplicationSettings().setPageExpiredErrorPage(PageExpiredErrorPage.class);
  }

  private void setupBeanValidationSettings() {
    new BeanValidationConfiguration().configure(this);
  }

  private void setupSecurityCryptoFactorySettings() {
    getSecuritySettings()
        .setCryptFactory(new CachingSunJceCryptFactory(getProperty(SITE_ENCRYPTION_KEY, DEFAULT_ENCRYPTION_KEY)));
  }

  private void setupJavaScriptToFooterHeaderResponseDecorator() {
    setHeaderResponseDecorator(new RenderJavaScriptToFooterHeaderResponseDecorator());
  }

  private void setupSpringCompInjectorForCompInstantListeners() {
    getComponentInstantiationListeners().add(new SpringComponentInjector(this));
  }

  private void setupDevelopmentModeSettings() {
    if (isDevelopmentModeEnabled()) {
      enableDevelopmentSettings();
    }
  }

  private boolean isDevelopmentModeEnabled() {
    return DEVELOPMENT == getConfigurationType();
  }

  private void enableDevelopmentSettings() {
    mountInspectorPage();
    enableDevelopmentUtilsAndAjaxDebugMode();
    configureWicketSource();
  }

  private void mountInspectorPage() {
    mountPage(INSPECTOR_PAGE, InspectorPage.class);
  }

  private void enableDevelopmentUtilsAndAjaxDebugMode() {
    getDebugSettings().setDevelopmentUtilitiesEnabled(true);
    getDebugSettings().setAjaxDebugModeEnabled(true);
  }

  private void configureWicketSource() {
    WicketSource.configure(this);
  }

  @Override
  protected Class<? extends ServletContainerAuthenticatedWebSession> getContainerManagedWebSessionClass() {
    return AppServletContainerAuthenticatedWebSession.class;
  }

  @Override
  public Class<? extends Page> getHomePage() {
    return MainPage.class;
  }

  @Override
  protected Class<? extends WebPage> getSignInPageClass() {
    return SignInPage.class;
  }

  @Override
  protected IConverterLocator newConverterLocator() {
    return newXmlGregorianCalanderLocator();
  }

  private ConverterLocator newXmlGregorianCalanderLocator() {
    final ConverterLocator locator = (ConverterLocator) super.newConverterLocator();
    locator.set(XMLGregorianCalendar.class, new XmlGregorianCalendarConverter());
    return locator;
  }
}
