package com.example.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

public class OAuthCallbackServlet extends
		AbstractAuthorizationCodeCallbackServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String clientId = "";

	@Override
	protected AuthorizationCodeFlow initializeFlow() throws ServletException,
			IOException {
		return new AuthorizationCodeFlow.Builder(
				BearerToken.authorizationHeaderAccessMethod(),
				new NetHttpTransport(), new JacksonFactory(), new GenericUrl(
						"/token"), new BasicAuthentication("", ""), clientId,
				"https://server.example.com/authorize").setCredentialDataStore(
				StoredCredential.getDefaultDataStore(new FileDataStoreFactory(
						new File("")))).build();
	}

	@Override
	protected String getRedirectUri(HttpServletRequest req)
			throws ServletException, IOException {
		GenericUrl url = new GenericUrl(req.getRequestURI().toString());
		url.setRawPath("/oauth2callback");
		return url.build();
	}

	@Override
	protected String getUserId(HttpServletRequest req) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
