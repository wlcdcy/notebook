package com.example.commons;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TrelloApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrelloUtils {

	public static void main(String[] args) {
		// get();
		// getAuthUrl();

		String body = members("overturn");
		List<Map> boards = getBoards(body);
		for (Map board : boards) {
			String name = (String) board.get("name");
			boolean closed = (Boolean) board.get("closed");
			String idOrganization = (String) board.get("idOrganization");
			String pinned = (String) board.get("pinned");
			String id = (String) board.get("id");
			logger.info(String.format("name : %s ###### id : %s", name, id));

			if (StringUtils.equals(name, "hiwork")) {
				createWebHook(
						"http://36.46.254.24:808/jersey/webhook/trello/board/callback",
						"broad webhook", id);
				break;
			}

		}

	}

	public static Logger logger = LoggerFactory.getLogger(TrelloUtils.class);
	public static String key = "f0b19e018eb3e79393f381e6b73bb687";
	public static String secret = "b97c687707005760d7a7c710a66f28ae9eddb964916decb93e171549f6d4fcd3";
	public static String board_id = "4d5ea62fd76aa1136000000c";

	public static String my_auth_token = "7bbdc42be2f211072520d2a01c42688b";
	public static String my_access_token = "cb8004d7fa87d18c852f49f99c78ff2a196143cabe7622455a2dd570914da1bf";

	public static String get() {
		String url = "https://api.trello.com/1/board/4d5ea62fd76aa1136000000c";
		String req_url = String.format("%s?key=%s&cards=open&lists=open", url,
				key);
		String resp_body = NetClientUtils.request(HttpGet.METHOD_NAME, req_url,
				"");
		logger.info(resp_body);
		return resp_body;

	}

	public static String getAuthUrl() {
		// "https://trello.com/1/authorize?key=substitutewithyourapplicationkey&name=My+Application&expiration=30days&response_type=token";
		String url = "https://trello.com/1/authorize";
		String name = "hiwork";
		String expires = "30days";
		String resp_type = "token";
		String req_url = String
				.format("%s?key=%s&name=%s&expiration=%s&response_type=%s&scope=read,write",
						url, key, name, expires, resp_type);
		logger.info(req_url);
		return req_url;
	}

	public static String getOauthUrl() {
		String callback = "http://36.46.254.24:808/jersey/webhook/trello/auth/callback";
		String scope = "read,write,account";
		OAuthService service = new ServiceBuilder().provider(TrelloApi.class)
				.apiKey(key).apiSecret(secret).callback(callback).scope(scope)
				.build();
		Token token = service.getRequestToken();
		String auth_url = service.getAuthorizationUrl(token);
		// https://trello.com/1/OAuthAuthorizeToken?oauth_token=31b6535658bc1f9c1d2d50b3c3dfe405&name=%E7%80%91%E5%B8%83IM&scope=read%2Cwrite&expiration=never&oauth_callback=http%3A%2F%2Fbeta.pubu.im%2Fservices%2Fauth%2Ftrello%2Fcallback%3Foauth_token_secret%3D92d78a7dcd71aefd71d3c2fefd589afe%26state%3D%7B%22team_id%22%3A%22553992c2a02678ed4448e8d9%22%2C%22user_id%22%3A%22553993dea02678ed4448e8ef%22%2C%22next%22%3A%22https%3A%2F%2Fzhouhe.pubu.im%2Fpassports%22%7D

		String name = "hiwork";
		String expires = "never"; // [30days,never,1day]
		String req_url = String.format(
				"%s&name=%s&scope=%s&expiration=%s&oauth_callback=%s",
				auth_url, name, URLEncoder.encode(scope), expires,
				URLEncoder.encode(callback));
		logger.info("OAuthAuthorizeToken URL : " + req_url);
		return req_url;
	}

	public static String getAccessToken(String Oauth_token,
			String oauth_verifier) {
		OAuthService service = new ServiceBuilder().provider(TrelloApi.class)
				.apiKey(key).apiSecret(secret).build();
		Token request_token = service.getRequestToken();
		Token access_token = service.getAccessToken(request_token,
				new Verifier(oauth_verifier));
		return access_token.getToken();
	}

	public static String createWebHook(String callback, String desc,
			String model) {
		// https://trello.com/1/tokens/[USER_TOKEN]/webhooks/?key=[APPLICATION_KEY]"
		String req_url = String.format(
				"https://trello.com/1/tokens/%s/webhooks/?key=%s",
				my_access_token, key);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> value = new HashMap<String, String>();
		value.put("callbackURL", callback);
		value.put("description", desc);
		value.put("idModel", model);
		String req_data = null;
		try {
			req_data = mapper.writeValueAsString(value);
		} catch (JsonGenerationException e1) {
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		CloseableHttpClient hpclient = NetClientUtils.createHttpClient(true);
		try {

			HttpPost hppost = new HttpPost(req_url);
			StringEntity entity = new StringEntity(req_data,
					ContentType.APPLICATION_JSON);
			hppost.setEntity(entity);
			CloseableHttpResponse response = hpclient.execute(hppost);
			String resp_body = EntityUtils.toString(response.getEntity());
			logger.info("resp_body : " + resp_body);
			if (response.getStatusLine().getStatusCode() == 200) {
				return resp_body;
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				hpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	/**
	 * @return
	 */
	public static String members(String username) {
		String url = "https://trello.com/1/members";
		String req_url = String.format("%s/%s?boards=all&key=%s&token=%s", url,
				username, key, my_access_token);
		String resp_body = NetClientUtils.request(HttpGet.METHOD_NAME, req_url,
				"");
		logger.info(resp_body);
		return resp_body;
	}

	public static List<Map> getBoards(String member_info) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map obj = mapper.readValue(member_info, Map.class);
			List<Map> boards = (List<Map>) obj.get("boards");
			return boards;

		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
