package com.example.commons;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
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

		String body = getMembers(); // getMembers("overturn");
		List<Map> boards = getBoards(body);
		for (Map board : boards) {
			String name = (String) board.get("name");
			boolean closed = (Boolean) board.get("closed");
			String idOrganization = (String) board.get("idOrganization");
			String pinned = (String) board.get("pinned");
			String id = (String) board.get("id");
			logger.info(String.format("name : %s ###### id : %s", name, id));

			if (StringUtils.equals(name, "hiwork")) {
				String resp_webhook = createWebHook(
						"http://36.45.175.60:808/jersey/webhook/trello/board/callback",
						"board webhook", id);
				ObjectMapper mapper = new ObjectMapper();
				try {
					my_webhook_id = (String) mapper.readValue(resp_webhook,
							Map.class).get("id");
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}

		}

		updateWebookWithModelId(my_webhook_id, "5582849aea1bb63179d15e57");
		deleteWebhook(my_webhook_id);
	}

	public static Logger logger = LoggerFactory.getLogger(TrelloUtils.class);
	public static String key = "f0b19e018eb3e79393f381e6b73bb687";
	public static String secret = "b97c687707005760d7a7c710a66f28ae9eddb964916decb93e171549f6d4fcd3";
	public static String board_id = "4d5ea62fd76aa1136000000c";

	public static String my_auth_token = "3ae0ffa2bf8a13456ff882fe09a507eb165207dea8750cc2f3fe48a00d687286";
	public static String my_access_token = "3ae0ffa2bf8a13456ff882fe09a507eb165207dea8750cc2f3fe48a00d687286";
	public static String my_webhook_id = "558a15e0a7fff9c6a1d795d4";

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

	/**
	 * 获取使用Oauth方式的授权认证地址
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getOauthUrl() {
		String callback = "http://36.45.175.60:808/jersey/webhook/trello/auth/callback";
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

	/**
	 * 获取使用Oauth方式授权后的访问token
	 * 
	 * @param Oauth_token
	 * @param oauth_verifier
	 * @return
	 */
	public static String getAccessToken(String Oauth_token,
			String oauth_verifier) {
		OAuthService service = new ServiceBuilder().provider(TrelloApi.class)
				.debug().apiKey(key).apiSecret(secret).build();
		Token request_token = service.getRequestToken();
		Token access_token = service.getAccessToken(request_token,
				new Verifier(oauth_verifier));
		return access_token.getToken();
	}

	/**
	 * 创建一个board的webhook,该board上的事件变化都会推送到callback地址上。
	 * 
	 * @param callback
	 *            接收通知服务地址
	 * @param desc
	 *            描述信息
	 * @param model
	 *            board id。
	 * @return
	 */
	public static String createWebHook(String callback, String desc,
			String board_id) {
		// https://trello.com/1/tokens/[USER_TOKEN]/webhooks/?key=[APPLICATION_KEY]"
		String req_url = String.format(
				"https://trello.com/1/tokens/%s/webhooks/?key=%s",
				my_access_token, key);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> value = new HashMap<String, String>();
		value.put("callbackURL", callback);
		value.put("description", desc);
		value.put("idModel", board_id);
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
			if (response.getStatusLine().getStatusCode() == 200) {
				String resp_body = EntityUtils.toString(response.getEntity());
				logger.info("resp_body : " + resp_body);
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
		return null;
	}

	/**
	 * @param webhook_id
	 * @param model_id
	 * @return
	 */
	public static String updateWebookWithModelId(String webhook_id,
			String model_id) {
		String req_url = String.format(
				"https://trello.com/1/webhooks/%s/idModel", webhook_id);
		CloseableHttpClient hpclient = NetClientUtils.createHttpClient(true);
		HttpPut hpput = new HttpPut(req_url);
		HttpEntity entity = EntityBuilder
				.create()
				.setParameters(new BasicNameValuePair("value", model_id),
						new BasicNameValuePair("key", key),
						new BasicNameValuePair("token", my_access_token))
				.build();
		hpput.setEntity(entity);
		try {
			CloseableHttpResponse response = hpclient.execute(hpput);
			if (response.getStatusLine().getStatusCode() == 200) {
				String resp_body = EntityUtils.toString(response.getEntity());
				logger.info("resp_body : " + resp_body);
				return resp_body;
			} else if (response.getStatusLine().getStatusCode() == 404) {
				logger.info(String
						.format("webhook update failed : webhook id[%s] is not exist: ",
								webhook_id));
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 删除一个board的webhook
	 * 
	 * @param webhook_id
	 *            成功创建webhook时返回的id
	 * @return
	 */
	public static String deleteWebhook(String webhook_id) {
		// https://trello.com/1/webhooks/[WEBHOOK_ID]?key=[APPLICATION_KEY]&token=[USER_TOKEN]
		String req_url = String.format(
				"https://trello.com/1/webhooks/%s?key=%s&token=%s", webhook_id,
				key, my_access_token);
		HttpDelete hpdelete = new HttpDelete(req_url);
		CloseableHttpClient hpclient = NetClientUtils.createHttpClient(true);
		try {
			CloseableHttpResponse response = hpclient.execute(hpdelete);
			if (response.getStatusLine().getStatusCode() == 200) {
				String resp_body = EntityUtils.toString(response.getEntity());
				logger.info("resp_body : " + resp_body);
				return resp_body;
			} else if (response.getStatusLine().getStatusCode() == 404) {
				logger.info(String.format(
						"webhook delete error : webhook id[%s] is not exist: ",
						webhook_id));
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取授权用户的信息
	 * 
	 * @return
	 */
	public static String getMembers() {
		return getMembers("me");
	}

	/**
	 * 获取授权用户的信息（包含所有的board）。
	 * 
	 * @param username
	 *            用户名称 default=me
	 * @return
	 */
	public static String getMembers(String username) {
		if (username == null || StringUtils.isEmpty(username)) {
			username = "me";
		}
		String url = "https://trello.com/1/members";
		String req_url = String.format("%s/%s?boards=all&key=%s&token=%s", url,
				username, key, my_access_token);
		String resp_body = NetClientUtils.request(HttpGet.METHOD_NAME, req_url,
				"");
		logger.info(resp_body);
		return resp_body;
	}

	/**
	 * 获取授权用户的所有board
	 * 
	 * @param member_info
	 *            用户信息（members方法的返回值）
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Map> getBoards(String member_info) {
		if (member_info == null || StringUtils.isEmpty(member_info)) {
			return null;
		}
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
