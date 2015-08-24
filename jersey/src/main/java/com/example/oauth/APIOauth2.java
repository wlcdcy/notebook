package com.example.oauth;

public interface APIOauth2 {
	public String getOauth2CallbackUrl();

	public String getOauth2Url();

	public String getOauth2Url(String redirect_uri);

	public String getAccessToken(String auth_code);
}
