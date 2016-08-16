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

package br.com.netbrasoft.gnuob.shop.authentication;

import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.ACCOUNTS_FACEBOOK_COM;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.ACCOUNTS_MICROSOFT_COM;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.ACCOUNTS_PAY_PAL_COM;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.FACEBOOK_CLIENT_ID_PREFIX_PROPERTY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.FACEBOOK_CLIENT_SECRET_PREFIX_PROPERTY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.FACEBOOK_SCOPE_PREFIX_PROPERTY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.GNUOB_PREFIX_PROPERTY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.GOOGLE_CLIENT_ID_PREFIX_PROPERTY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.GOOGLE_CLIENT_SECRET_PREFIX_PROPERTY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.GOOGLE_SCOPE_PREFIX_PROPERTY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.ISSUER_FACEBOOK;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.ISSUER_MICROSOFT;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.ISSUER_PAY_PAL;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.MICROSOFT_CLIENT_ID_PREFIX_PROPERTY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.MICROSOFT_CLIENT_SECRET_PREFIX_PROPERTY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.MICROSOFT_SCOPE_PREFIX_PROPERTY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.PAYPAL_CLIENT_ID_PREFIX_PROPERTY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.PAYPAL_CLIENT_SECRET_PREFIX_PROPERTY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.PAYPAL_SCOPE_PREFIX_PROPERTY;
import static br.com.netbrasoft.gnuob.shop.NetbrasoftShopConstants.getProperty;

import java.io.IOException;
import java.net.URI;
import java.util.Scanner;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponseParser;
import com.nimbusds.openid.connect.sdk.UserInfoErrorResponse;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import com.nimbusds.openid.connect.sdk.UserInfoSuccessResponse;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import br.com.netbrasoft.gnuob.api.generic.GNUOpenBusinessApplicationException;

public final class OAuthUtils {

  private OAuthUtils() {}

  public static AuthenticationRequest getAuthenticationRequest(final OIDCProviderMetadata providerConfiguration,
      final ClientID clientID, final URI redirectURI, final Scope scope, final State state) {
    return new AuthenticationRequest(providerConfiguration.getAuthorizationEndpointURI(),
        new ResponseType(ResponseType.Value.CODE), scope, clientID, redirectURI, state, new Nonce());
  }

  public static ClientID getClientID(final String site, final URI issuerURI) {
    switch (issuerURI.toString()) {
      case ACCOUNTS_FACEBOOK_COM:
        return new ClientID(getProperty(GNUOB_PREFIX_PROPERTY + site + FACEBOOK_CLIENT_ID_PREFIX_PROPERTY));
      case ACCOUNTS_PAY_PAL_COM:
        return new ClientID(getProperty(GNUOB_PREFIX_PROPERTY + site + PAYPAL_CLIENT_ID_PREFIX_PROPERTY));
      case ACCOUNTS_MICROSOFT_COM:
        return new ClientID(getProperty(GNUOB_PREFIX_PROPERTY + site + MICROSOFT_CLIENT_ID_PREFIX_PROPERTY));
      default: // Google.
        return new ClientID(getProperty(GNUOB_PREFIX_PROPERTY + site + GOOGLE_CLIENT_ID_PREFIX_PROPERTY));
    }
  }

  public static Secret getSecret(final String site, final URI issuerURI) {
    switch (issuerURI.toString()) {
      case ACCOUNTS_FACEBOOK_COM:
        return new Secret(getProperty(GNUOB_PREFIX_PROPERTY + site + FACEBOOK_CLIENT_SECRET_PREFIX_PROPERTY));
      case ACCOUNTS_PAY_PAL_COM:
        return new Secret(getProperty(GNUOB_PREFIX_PROPERTY + site + PAYPAL_CLIENT_SECRET_PREFIX_PROPERTY));
      case ACCOUNTS_MICROSOFT_COM:
        return new Secret(getProperty(GNUOB_PREFIX_PROPERTY + site + MICROSOFT_CLIENT_SECRET_PREFIX_PROPERTY));
      default: // Google.
        return new Secret(getProperty(GNUOB_PREFIX_PROPERTY + site + GOOGLE_CLIENT_SECRET_PREFIX_PROPERTY));
    }
  }

  public static FacebookAuthenticationRequest getFacebookAuthenticationRequest(
      final OIDCProviderMetadata providerConfiguration, final ClientID clientID, final URI redirectURI,
      final Scope scope, final State state) {
    return new FacebookAuthenticationRequest(providerConfiguration.getAuthorizationEndpointURI(),
        new ResponseType(ResponseType.Value.CODE), scope, clientID, redirectURI, state);
  }

  public static MicrosoftAuthenticationRequest getMicrosoftAuthenticationRequest(
      final OIDCProviderMetadata providerConfiguration, final ClientID clientID, final URI redirectURI,
      final Scope scope, final State state) {
    return new MicrosoftAuthenticationRequest(providerConfiguration.getAuthorizationEndpointURI(),
        new ResponseType(ResponseType.Value.CODE), scope, clientID, redirectURI, state);
  }

  public static OIDCProviderMetadata getOIDCProviderMetaData(final URI issuerURI) {
    try {
      try (Scanner json = new java.util.Scanner(issuerURI.toURL().openStream())) {
        return OIDCProviderMetadata.parse(json.useDelimiter("\\A").hasNext() ? json.next() : "");
      }
    } catch (ParseException | IOException e) {
      throw new GNUOpenBusinessApplicationException("Couldn't get OIDCProviderMetadata", e);
    }
  }

  public static Scope getScope(final String site, final URI issuerURI) {
    switch (issuerURI.toString()) {
      case ACCOUNTS_FACEBOOK_COM:
        return Scope.parse(getProperty(GNUOB_PREFIX_PROPERTY + site + FACEBOOK_SCOPE_PREFIX_PROPERTY));
      case ACCOUNTS_PAY_PAL_COM:
        return Scope.parse(getProperty(GNUOB_PREFIX_PROPERTY + site + PAYPAL_SCOPE_PREFIX_PROPERTY));
      case ACCOUNTS_MICROSOFT_COM:
        return Scope.parse(getProperty(GNUOB_PREFIX_PROPERTY + site + MICROSOFT_SCOPE_PREFIX_PROPERTY));
      default: // Google.
        return Scope.parse(getProperty(GNUOB_PREFIX_PROPERTY + site + GOOGLE_SCOPE_PREFIX_PROPERTY));
    }
  }

  private static BearerAccessToken getBearerAccessToken(final OIDCProviderMetadata providerConfiguration,
      final ClientID clientID, final AuthorizationCode authorizationCode, final URI redirectURI,
      final Secret clientSecret) throws ParseException, IOException {
    final TokenResponse tokenResponse =
        getTokenResponse(providerConfiguration, clientID, authorizationCode, redirectURI, clientSecret);
    if (tokenResponse instanceof TokenErrorResponse) {
      final ErrorObject error = ((TokenErrorResponse) tokenResponse).getErrorObject();
      throw new GNUOpenBusinessApplicationException(error.getDescription());
    }
    if (tokenResponse instanceof OIDCTokenResponse) {
      return ((OIDCTokenResponse) tokenResponse).getOIDCTokens().getBearerAccessToken();
    }
    if (tokenResponse instanceof AccessTokenResponse) {
      return ((AccessTokenResponse) tokenResponse).getTokens().getBearerAccessToken();
    }
    throw new GNUOpenBusinessApplicationException("Couldn't get a BearerAccessToken");
  }

  private static TokenResponse getTokenResponse(final OIDCProviderMetadata providerConfiguration,
      final ClientID clientID, final AuthorizationCode authorizationCode, final URI redirectURI,
      final Secret clientSecret) throws ParseException, IOException {
    TokenResponse tokenResponse;
    switch (providerConfiguration.getIssuer().getValue()) {
      case ISSUER_FACEBOOK:
        tokenResponse = AccessTokenResponse
            .parse(getSecretTokenRequest(providerConfiguration, clientID, authorizationCode, redirectURI, clientSecret)
                .toHTTPRequest().send());
        break;
      case ISSUER_PAY_PAL:
        tokenResponse = OIDCTokenResponseParser
            .parse(getSecretTokenRequest(providerConfiguration, clientID, authorizationCode, redirectURI, clientSecret)
                .toHTTPRequest().send());
        break;
      case ISSUER_MICROSOFT:
        tokenResponse = AccessTokenResponse
            .parse(getSecretTokenRequest(providerConfiguration, clientID, authorizationCode, redirectURI, clientSecret)
                .toHTTPRequest().send());
        break;
      default: // Google.
        tokenResponse = OIDCTokenResponseParser
            .parse(getSecretTokenRequest(providerConfiguration, clientID, authorizationCode, redirectURI, clientSecret)
                .toHTTPRequest().send());
        break;
    }
    return tokenResponse;
  }

  private static SecretTokenRequest getSecretTokenRequest(final OIDCProviderMetadata providerConfiguration,
      final ClientID clientID, final AuthorizationCode authorizationCode, final URI redirectURI,
      final Secret clientSecret) {
    return new SecretTokenRequest(providerConfiguration.getTokenEndpointURI(), clientID, clientSecret,
        new AuthorizationCodeGrant(authorizationCode, redirectURI));
  }

  private static UserInfo getUserInfo(final OIDCProviderMetadata providerConfiguration,
      final BearerAccessToken bearerAccessToken) throws IOException, ParseException {
    final UserInfoResponse userInfoResponse = getUserInfoResponse(providerConfiguration, bearerAccessToken);
    if (userInfoResponse instanceof UserInfoErrorResponse) {
      final ErrorObject error = ((UserInfoErrorResponse) userInfoResponse).getErrorObject();
      throw new GNUOpenBusinessApplicationException(error.getDescription());
    }
    return ((UserInfoSuccessResponse) userInfoResponse).getUserInfo();
  }

  private static UserInfoResponse getUserInfoResponse(final OIDCProviderMetadata providerConfiguration,
      final BearerAccessToken bearerAccessToken) throws ParseException, IOException {
    UserInfoResponse userInfoResponse;
    switch (providerConfiguration.getIssuer().getValue()) {
      case ISSUER_FACEBOOK:
        userInfoResponse = FacebookUserInfoResponse
            .parse(getUserInfoRequest(providerConfiguration, bearerAccessToken).toHTTPRequest().send());
        break;
      case ISSUER_PAY_PAL:
        userInfoResponse = PayPalUserInfoResponse
            .parse(getUserInfoRequest(providerConfiguration, bearerAccessToken).toHTTPRequest().send());
        break;
      case ISSUER_MICROSOFT:
        userInfoResponse = MicrosoftUserInfoResponse
            .parse(getUserInfoRequest(providerConfiguration, bearerAccessToken).toHTTPRequest().send());
        break;
      default: // Google.
        userInfoResponse =
            UserInfoResponse.parse(getUserInfoRequest(providerConfiguration, bearerAccessToken).toHTTPRequest().send());
        break;
    }
    return userInfoResponse;
  }

  private static UserInfoRequest getUserInfoRequest(final OIDCProviderMetadata providerConfiguration,
      final BearerAccessToken bearerAccessToken) {
    return new UserInfoRequest(providerConfiguration.getUserInfoEndpointURI(), bearerAccessToken);
  }

  public static UserInfo getUserInfo(final OIDCProviderMetadata providerConfiguration, final ClientID clientID,
      final State state, final URI requestURI, final URI redirectURI, final Secret clientSecret) {
    try {
      return getUserInfo(providerConfiguration, getBearerAccessToken(providerConfiguration, clientID,
          getAuthenticationCode(requestURI, state), redirectURI, clientSecret));
    } catch (ParseException | SerializeException | IOException e) {
      throw new GNUOpenBusinessApplicationException("Couldn't get UserInfo", e);
    }
  }

  private static AuthorizationCode getAuthenticationCode(final URI requestURI, final State state)
      throws ParseException {
    final AuthenticationResponse authenticationResponse = AuthenticationResponseParser.parse(requestURI);
    if (authenticationResponse instanceof AuthenticationErrorResponse) {
      final ErrorObject error = ((AuthenticationErrorResponse) authenticationResponse).getErrorObject();
      throw new GNUOpenBusinessApplicationException(error.getDescription());
    }
    if (((AuthenticationSuccessResponse) authenticationResponse).getState() == null
        || !((AuthenticationSuccessResponse) authenticationResponse).getState().getValue().equals(state.getValue())) {
      throw new GNUOpenBusinessApplicationException("State verification failed, recieved stated is not correct");
    }
    return ((AuthenticationSuccessResponse) authenticationResponse).getAuthorizationCode();
  }
}