package com.netbrasoft.gnuob.shop.authentication;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import com.netbrasoft.gnuob.api.generic.GNUOpenBusinessApplicationException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.OIDCAccessTokenResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponseParser;
import com.nimbusds.openid.connect.sdk.UserInfoErrorResponse;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import com.nimbusds.openid.connect.sdk.UserInfoSuccessResponse;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

public final class OAuthUtils {

   private static final String ISSUER_FACEBOOK = "https://www.facebook.com";
   public static final String ACCOUNTS_GOOGLE_COM = "https://accounts.google.com/.well-known/openid-configuration";
   private static final String GNUOB_SITE_GOOGLE_CLIENT_SECRET = "gnuob.site.google.clientSecret";
   private static final String GNUOB_SITE_GOOGLE_CLIENT_ID = "gnuob.site.google.clientId";
   private static final String GNUOB_SITE_GOOGLE_SCOPE = "gnuob.site.google.scope";

   public static final String ACCOUNTS_FACEBOOK_COM = "http://localhost:8080/json/facebook/openid-configuration";
   private static final String GNUOB_SITE_FACEBOOK_CLIENT_SECRET = "gnuob.site.facebook.clientSecret";
   private static final String GNUOB_SITE_FACEBOOK_CLIENT_ID = "gnuob.site.facebook.clientId";
   private static final String GNUOB_SITE_FACEBOOK_SCOPE = "gnuob.site.facebook.scope";

   public static AuthenticationRequest getAuthenticationRequest(final OIDCProviderMetadata providerConfiguration, final URI issuerURI, final ClientID clientID, final URI redirectURI, Scope scope, State state) {
      return new AuthenticationRequest(providerConfiguration.getAuthorizationEndpointURI(), new ResponseType(ResponseType.Value.CODE), scope, clientID, redirectURI, state, new Nonce());
   }

   public static ClientID getClientID(URI issuerURI) {

      switch (issuerURI.toString()) {
      case ACCOUNTS_GOOGLE_COM:
         return new ClientID(System.getProperty(GNUOB_SITE_GOOGLE_CLIENT_ID));
      case ACCOUNTS_FACEBOOK_COM:
         return new ClientID(System.getProperty(GNUOB_SITE_FACEBOOK_CLIENT_ID));
      }

      return new ClientID();
   }

   public static Scope getScope(URI issuerURI) {
      switch (issuerURI.toString()) {
      case ACCOUNTS_GOOGLE_COM:
         return Scope.parse(System.getProperty(GNUOB_SITE_GOOGLE_SCOPE));
      case ACCOUNTS_FACEBOOK_COM:
         return Scope.parse(System.getProperty(GNUOB_SITE_FACEBOOK_SCOPE));
      }
      return new Scope();
   }

   public static String getClientSecret(URI issuerURI) {
      switch (issuerURI.toString()) {
      case ACCOUNTS_GOOGLE_COM:
         return System.getProperty(GNUOB_SITE_GOOGLE_CLIENT_SECRET);
      case ACCOUNTS_FACEBOOK_COM:
         return System.getProperty(GNUOB_SITE_FACEBOOK_CLIENT_SECRET);
      }

      return "";
   }

   public static OIDCProviderMetadata getProviderConfigurationURL(final URI issuerURI) {
      try {
         URL providerConfigurationURL = issuerURI.toURL();
         InputStream inputStream = providerConfigurationURL.openStream();

         String providerInfo = null;

         try (java.util.Scanner json = new java.util.Scanner(inputStream)) {
            providerInfo = json.useDelimiter("\\A").hasNext() ? json.next() : "";
         }

         return OIDCProviderMetadata.parse(providerInfo);
      } catch (ParseException | IOException e) {
         throw new GNUOpenBusinessApplicationException("Couldn't get OIDCProviderMetadata", e);
      }
   }

   private static BearerAccessToken getTokenRequest(final OIDCProviderMetadata providerConfiguration, final ClientID clientID, final AuthorizationCode authorizationCode, final URI redirectURI, String clientSecret)
         throws SerializeException, ParseException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, java.text.ParseException, JOSEException {
      TokenRequest tokenRequest = new TokenRequest(providerConfiguration.getTokenEndpointURI(), clientID, new AuthorizationCodeGrant(authorizationCode, redirectURI));
      HTTPRequest httpRequest = tokenRequest.toHTTPRequest();

      if (clientSecret != null && !clientSecret.equals("")) {
         Map<String, String> parameters = httpRequest.getQueryParameters();
         parameters.put("client_secret", clientSecret);
         httpRequest.setQuery(URLUtils.serializeParameters(parameters));
      }

      TokenResponse tokenResponse = OIDCTokenResponseParser.parse(httpRequest.send());

      if (tokenResponse instanceof TokenErrorResponse) {
         ErrorObject error = ((TokenErrorResponse) tokenResponse).getErrorObject();
         throw new GNUOpenBusinessApplicationException(error.getDescription());
      }

      OIDCAccessTokenResponse oidcAccessTokenResponse = ((OIDCAccessTokenResponse) tokenResponse);

      return oidcAccessTokenResponse.getBearerAccessToken();
   }

   private static UserInfo getUserInfo(final OIDCProviderMetadata providerConfiguration, final BearerAccessToken bearerAccessToken) throws ParseException, SerializeException, IOException {
      UserInfoRequest userInfoRequest = new UserInfoRequest(providerConfiguration.getUserInfoEndpointURI(), bearerAccessToken);

      UserInfoResponse userInfoResponse;

      switch (providerConfiguration.getIssuer().getValue()) {

      case ISSUER_FACEBOOK:
         userInfoResponse = FacebookUserInfoResponse.parse(userInfoRequest.toHTTPRequest().send());
         break;
      default:
         userInfoResponse = UserInfoResponse.parse(userInfoRequest.toHTTPRequest().send());
         break;
      }

      if (userInfoResponse instanceof UserInfoErrorResponse) {
         ErrorObject error = ((UserInfoErrorResponse) userInfoResponse).getErrorObject();
         throw new GNUOpenBusinessApplicationException(error.getDescription());
      }

      return ((UserInfoSuccessResponse) userInfoResponse).getUserInfo();
   }

   public static UserInfo getUserInfo(final OIDCProviderMetadata providerConfiguration, final URI issuerURI, final ClientID clientID, final State state, final URI requestURI, final URI redirectURI, String clientSecret) {
      try {
         AuthorizationCode authorizationCode = retrieveAuthenticationCode(requestURI, state);
         BearerAccessToken bearerAccessToken = getTokenRequest(providerConfiguration, clientID, authorizationCode, redirectURI, clientSecret);
         return getUserInfo(providerConfiguration, bearerAccessToken);
      } catch (ParseException | SerializeException | IOException | NoSuchAlgorithmException | InvalidKeySpecException | java.text.ParseException | JOSEException e) {
         throw new GNUOpenBusinessApplicationException("Couldn't get UserInfo", e);
      }
   }

   private static AuthorizationCode retrieveAuthenticationCode(final URI requestURI, final State state) throws ParseException {
      AuthenticationResponse authenticationResponse = AuthenticationResponseParser.parse(requestURI);

      if (authenticationResponse instanceof AuthenticationErrorResponse) {
         ErrorObject error = ((AuthenticationErrorResponse) authenticationResponse).getErrorObject();
         throw new GNUOpenBusinessApplicationException(error.getDescription());
      }

      if (((AuthenticationSuccessResponse) authenticationResponse).getState() == null || !((AuthenticationSuccessResponse) authenticationResponse).getState().getValue().equals(state.getValue())) {
         throw new GNUOpenBusinessApplicationException("State verification failed, recieved stated is not correct");
      }

      return ((AuthenticationSuccessResponse) authenticationResponse).getAuthorizationCode();
   }

   private OAuthUtils() {
   }
}
