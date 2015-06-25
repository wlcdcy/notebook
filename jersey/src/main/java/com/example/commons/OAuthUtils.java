package com.example.commons;

import java.io.IOException;

import org.apache.http.Consts;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

/**
 * @author Administrator
 *
 *	use google-oauth-client api
 */
public class OAuthUtils {
	
	
	public static HttpResponse executeGet(HttpTransport transport,JsonFactory json_factory,String access_token,GenericUrl url) throws IOException{
		Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(access_token);
		HttpRequestFactory req_factory = transport.createRequestFactory(credential);
		return req_factory.buildGetRequest(url).execute();
	}
	
	
	public static void main(String[] args){
		GenericUrl req_url = new GenericUrl(Consts.UTF_8.name());
	}

}
