package com.example.commons;

import java.io.IOException;

import org.apache.http.Consts;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.DataStore;

/**
 * @author Administrator
 *
 *         use google-oauth-client api
 */
public class OauthUtils {

	public static HttpResponse executeGet(HttpTransport transport,
			JsonFactory json_factory, String access_token, GenericUrl url)
			throws IOException {
		Credential credential = new Credential(
				BearerToken.authorizationHeaderAccessMethod())
				.setAccessToken(access_token);
		HttpRequestFactory req_factory = transport
				.createRequestFactory(credential);
		return req_factory.buildGetRequest(url).execute();
	}

	public void useAuthorizationCodeFlow() {
		AuthorizationCodeFlow dd = null;
		try {
			Credential credential = dd.loadCredential("userid");
			// if(credential==null){
			// // ---
			// }
			AuthorizationCodeTokenRequest token_request = dd
					.newTokenRequest("code");
			TokenResponse token_response = token_request.execute();
			dd.createAndStoreCredential(token_response, "userid");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void unuseAuthorizationCodeFlow() {
		DataStore ds = null;
		try {
			Credential credential = (Credential) ds.get("userid");

			// direct the browser to authorization page
			AuthorizationCodeRequestUrl code_req = null;

			// process the authorization response and parse the authorization
			// code;
			AuthorizationCodeResponseUrl code_resp = null;

			// request an access token and possible a refresh token
			AuthorizationCodeTokenRequest token_req = null;

			// create new credential and store it using DataStore.set(String,V)
			new Credential(BearerToken.authorizationHeaderAccessMethod());

			// using credential access protected resources;expired access tokens
			// are automatically refreshed using the refresh token

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		GenericUrl req_url = new GenericUrl(Consts.UTF_8.name());

	}

}
