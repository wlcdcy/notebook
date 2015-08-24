package com.example.oauth;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class Oauth2Factory {

	private static final Oauth2Factory oauth2Factory = new Oauth2Factory();
	private static Map<String, Object> apiOauth2s = new HashMap<String, Object>();

	private Oauth2Factory() {

	}

	public static Oauth2Factory getInstance() {
		return oauth2Factory;
	}

	public APIOauth2 createAPPOauth2Pravider(Class<?> clazz) {
		try {
			return (APIOauth2) clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public APIOauth2 createAPPOauth2Pravider(String className) {
		try {
			return (APIOauth2) Class.forName(className).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public APIOauth2 getAppOauth2Pravider(String app_code) {
		APIOauth2 api = (APIOauth2) apiOauth2s.get("app_code");
		if (api != null) {
			return api;
		}
		if (StringUtils.equals(app_code, "overnote")) {
			api = createAPPOauth2Pravider(EvernoteAPIOauth2.class);
			apiOauth2s.put(app_code, api);
			return api;
		}

		return null;
	}

}
