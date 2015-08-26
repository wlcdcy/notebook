package com.example.oauth;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.evernote.auth.EvernoteService;
import com.example.commons.NetUtils;

public class EvernoteAPIOauth2 implements APIOauth2 {

	EvernoteService evernoteService;
	public static final String evernoteHost = "sandbox.evernote.com";
	private static String oauth_host = "https://sandbox.evernote.com";
	public static final String consumer_key = "hiwork";
	public static final String consumer_secret = "5382250a6f5eb0c8";
	
	
//	acction token : S=s1:U=914eb:E=156b6b7061b:C=14f5f05d958:P=185:A=hiwork:V=2:H=5b8fdfb2ccf47a7bb59218851a11c21e
	
	public static Logger logger = LoggerFactory
			.getLogger(EvernoteAPIOauth2.class);
	Random random = new Random();

	@Override
	public String getOauth2CallbackUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOauth2Url() {

		// String url = EvernoteService.SANDBOX.;
		return null;
	}

	@Override
	public String getOauth2Url(String redirect_uri) {
		String oauth_token = getTemporaryToken(redirect_uri);
		String url = "/OAuth.action";
		String param = String.format("oauth_token=%s", oauth_token);
		return oauth_host + url + "?" + param;
	}

	@Override
	public String getAccessToken(String auth_code) {
		String url = "/oauth"; // sandbox.evernote.com/oauth
		String oauth_token = "hiwork.14F5ED88F55.687474703A2F2F33362E34362E3235342E3230302F6A65727365792F6F617574682F6F7665726E6F74652F63616C6C6261636B.85A3B9DE6F06DE4BDC2AD7E324DC9467";
		String oauth_verifier = "24FC21175F2FA0B30579C0D9936E7B28";

		String param = String
				.format("oauth_consumer_key=%s&oauth_token=%s&oauth_verifier=%s&oauth_nonce=%s&oauth_signature=%s&oauth_signature_method=%s&oauth_timestamp%s&oauth_version=%s",
						consumer_key, oauth_token, oauth_verifier,
						Long.toHexString(random.nextLong()), consumer_secret,
						"PLAINTEXT",
						Long.toString(System.currentTimeMillis() / 1000), "1.0");
		String req_url = oauth_host + url + "?" + param;
		String resp_data = httpGetRequest(req_url);
		if(StringUtils.isEmpty(resp_data))
			return "400 error";
		
		logger.info(resp_data);
		return "200 ok";
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public String getAccessToken(String oauth_token,String oauth_verifier) {
		String url = "/oauth"; // sandbox.evernote.com/oauth
//		String oauth_token ="hiwork.14F5ED88F55.687474703A2F2F33362E34362E3235342E3230302F6A65727365792F6F617574682F6F7665726E6F74652F63616C6C6261636B.85A3B9DE6F06DE4BDC2AD7E324DC9467";
//		String oauth_verifier ="24FC21175F2FA0B30579C0D9936E7B28";

		String param = String
				.format("oauth_consumer_key=%s&oauth_token=%s&oauth_verifier=%s&oauth_nonce=%s&oauth_signature=%s&oauth_signature_method=%s&oauth_timestamp%s&oauth_version=%s",
						consumer_key, oauth_token, oauth_verifier,
						Long.toHexString(random.nextLong()), consumer_secret,
						"PLAINTEXT",
						Long.toString(System.currentTimeMillis() / 1000), "1.0");
		String req_url = oauth_host + url + "?" + param;
		String resp_body = httpGetRequest(req_url);
		if(StringUtils.isEmpty(resp_body))
			return "400 error";
		
		logger.info(resp_body);
		Map<String,String> resp_data = parseAccessTokenResponse(resp_body);
		logger.info("acction token : " + URLDecoder.decode(resp_data.get("oauth_token")));
		logger.info("oauth_token_secret : " + resp_data.get("oauth_token_secret"));
		logger.info("edam_shard : " + resp_data.get("edam_shard"));
		logger.info("edam_userId : " + resp_data.get("edam_userId"));
		logger.info("edam_expires : " + resp_data.get("edam_expires"));
		logger.info("edam_noteStoreUrl : " + URLDecoder.decode(resp_data.get("edam_noteStoreUrl")));
		logger.info("edam_webApiUrlPrefix : " + URLDecoder.decode(resp_data.get("edam_webApiUrlPrefix")));
		return "200 ok";
	}

	@SuppressWarnings("deprecation")
	private String getTemporaryToken(String redirect_uri) {
		String url = "/oauth";
		String param = String
				.format("oauth_callback=%s&oauth_consumer_key=%s&oauth_nonce=%s&oauth_signature=%s&oauth_signature_method=%s&oauth_timestamp%s&oauth_version=%s",
						URLEncoder.encode(redirect_uri), consumer_key,
						Long.toHexString(random.nextLong()), consumer_secret,
						"PLAINTEXT",
						Long.toString(System.currentTimeMillis() / 1000), "1.0");
		String req_url = oauth_host + url + "?" + param;

		CloseableHttpClient http_client = null;
		CloseableHttpResponse response = null;
		try {
			http_client = NetUtils.getHttpClient();
			HttpGet http_get = new HttpGet(req_url);
			response = http_client.execute(http_get);
			logger.info(response.toString());
			if (response.getStatusLine().getStatusCode() < 300) {
				String res_body = EntityUtils.toString(response.getEntity());
				Map<String, String> res_map = parseTemporaryTokenResponse(res_body);
				String oauth_token = res_map.get("oauth_token");
				// String oauth_token_secret =res_map.get("oauth_token_secret");
				// boolean oauth_callback_confirmed =
				// StringUtils.equals(res_map.get("oauth_callback_confirmed"),
				// "true")? true:false;
				return oauth_token;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (http_client != null)
					http_client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private Map<String, String> parseTemporaryTokenResponse(String value) {
		if (StringUtils.isEmpty(value))
			return null;
		String[] valuse = value.split("&");
		Map<String, String> data = new HashMap<String, String>();
		for (String v : valuse) {
			String[] k_v = v.split("=");
			data.put(k_v[0], k_v.length == 2 ? k_v[1] : "");
		}
		return data;
	}
	
	private Map<String, String> parseAccessTokenResponse(String value) {
		if (StringUtils.isEmpty(value))
			return null;
		String[] valuse = value.split("&");
		Map<String, String> data = new HashMap<String, String>();
		for (String v : valuse) {
			String[] k_v = v.split("=");
			data.put(k_v[0], k_v.length == 2 ? k_v[1] : "");
		}
		return data;
	}

	public static void main(String[] args) {
		EvernoteAPIOauth2 d = new EvernoteAPIOauth2();
		String callback = "http://1.84.152.27:9090/plugins/hiwork/api/oauth/evernote/callback";
		System.out.println(d.getTemporaryToken(callback));
	}

	private String httpGetRequest(String req_url) {
		CloseableHttpClient http_client = null;
		CloseableHttpResponse response = null;
		try {
			http_client = NetUtils.getHttpClient();
			HttpGet http_get = new HttpGet(req_url);
			response = http_client.execute(http_get);
			logger.info(response.toString());
			if (response.getStatusLine().getStatusCode() < 300) {
				String res_body = EntityUtils.toString(response.getEntity());
				return res_body;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (http_client != null)
					http_client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

}
