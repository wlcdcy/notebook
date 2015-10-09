package com.example.commons;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeambitionUtils<T> {

	private static Logger logger = LoggerFactory.getLogger(TeambitionUtils.class);

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		List<String> project_events = listWebhookEvent4Projects();
		
		List<Map<String,Object>> projects = listProjects();
		Iterator<Map<String,Object>> it=projects.iterator();
		while(it.hasNext()){
			Map<String,Object> project = it.next();
			String _name = (String)project.get("name");
			String _id = (String)project.get("_id");
			logger.info("project name : "+_name);
			logger.info("project id : " + _id);
			
			String _hook_id = null;
			try {
				ObjectMapper mapper = new ObjectMapper();
				JavaType jt = mapper.getTypeFactory().constructParametricType(List.class, Map.class);
				List hooks = mapper.readValue(listWebhook4Projects(_id),jt);
				Map hook = (Map) hooks.get(0);
				_hook_id = (String) hook.get("_id");
			} catch (IOException e) {
				e.printStackTrace();
			}
			createWebhook4Project(_id, hook_project, project_events);
//			updateWebhook4Project(_id,_hook_id, hook_project, project_events);
			
			listWebhook4Projects(_id);
		}
		
//		List<String> organization_events = listWebhookEvent4Organizations();

	}

	private static String client_key = "f2e07fd0-2390-11e5-973a-8d7d552c3be7";
	private static String client_secret = "3f9575d6-f2ba-4569-9bf9-24d1f6944867";

	private static String oauth_host = "https://account.teambition.com";
	private static String api_host = "https://api.teambition.com";

	private static String req_state = "teambision";
	public static String auth_code = "yynJpTP0DB9kni5wnN6P5-";
	public static String access_token ="CQ-0Hyd0aJTys7xYeXX86EKQHsE=VH8Z1Ud21630f50f852de71389ba30f5ff2a22e5dfb4d834156f1b837c3ba5b021d631cfcdb679a880c0a09d4e481019befdf2e1a86091e5648c80b4dbc870850421058d59c187feaf5fd3ef70a269ca1226ea88bd652501cc14f742c347a92c55d2e961313deb08bc1a1b5c1cbf8222a05fb372";//"VH8Z1Ud21630f50f852de71389ba30f5ff2a22e5dfb4d834156f1b837c3ba5b021d631cfcdb679a880c0a09d4e481019befdf2e1a86091e5648c80b4dbc870850421058d59c187feaf5fd3ef70a269ca1226ea88bd652501cc14f742c347a92c55d2e961313deb08bc1a1b5c1cbf8222a05fb372";// "xFNntgtIi4SPt1VeqSdxTD6MOWA=mK5_dJD130223d6334a2adee18b35aeb769823caf41bed623735052dc0274f7352347506b76ebedf45ceefddaf99b6ef33a33472a23ea71152e57c6d0a6e0e06b18670597e1eaf786983f8c0899e879536732d2935b82c541fb155e2aee64536774b76b01ebfec434159e9126d5f3fa1fbac165f";
	
	public static String redirect_uri="http://36.46.254.51/jersey/webhook/teambition/auth/callback";
	public static String hook_project="http://36.46.254.51/jersey/webhook/teambition/project";
	public static String hook_organization="http://36.46.254.51/jersey/webhook/teambition/organization";

	/**
	 * 获取Teambition认证地址
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getOAuthUrl(String redirect_uri) {
		String url = "/oauth2/authorize";
		String param = String.format("client_id=%s&state=%s&redirect_uri=%s", client_key, req_state,
				URLEncoder.encode(redirect_uri));
		return oauth_host + url + "?" + param;
	}

	/**获取访问token
	 * @param code
	 * @return
	 */
	public static String fetchAccessToken(String code) {
		String url = "/oauth2/access_token";
		String param = String.format("code=%s&grant_type=%s&client_id=%s&client_secret=%s", code, "code", client_key,
				client_secret);
		logger.info(String.format("req_data is : %s", param));
		String res_data = post_request(String.format("%s%s", oauth_host, url), param);
		logger.info(String.format("res_data is : %s", res_data));
		return res_data;
	}

	/**检测访问token的合法性
	 * @param _access_token
	 * @return
	 */
	public static boolean checkAccessToken(String _access_token) {
		String url = "/api/applications/%s/tokens/check";
		String req_url = String.format("%s"+url,api_host, client_key);
		
		if(check_access_token_request(req_url,_access_token)!=null){
			return true;
		}else{
			return false;
		}
	}
	
	/**获取用户所有的工程信息
	 * @return
	 */
	public static List<Map<String,Object>> listProjects(){
		String url="/api/projects/";
		String req_url = String.format("%s%s?access_token=%s", api_host,url,access_token);
		logger.info(String.format("req_url is : %s", req_url));
		String res_data = get_request(req_url);
		logger.info(String.format("res_data is : %s", res_data));
		try {
			
			ObjectMapper mapper = new ObjectMapper();
			JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class,Map.class);
			return mapper.readValue(res_data,javaType);
			
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**获取工程的webhook事件通知类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<String> listWebhookEvent4Projects(){
		String url="/api/projects/webhooks/";
		String req_url = String.format("%s%s?access_token=%s", api_host,url,access_token);
		logger.info(String.format("req_url is : %s", req_url));
		String res_data = get_request(req_url);
		logger.info(String.format("res_data is : %s", res_data));
		try {
			ObjectMapper mapper = new ObjectMapper();
			return (List<String>) mapper.readValue(res_data, Map.class).get("events");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**获取组织机构的webhook事件通知类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<String> listWebhookEvent4Organizations(){
		String url="/api/organizations/webhooks/";
		String res_data = get_request(String.format("%s%s?access_token=%s", api_host,url,access_token));
		logger.info(String.format("res_data is : %s", res_data));
		try {
			ObjectMapper mapper = new ObjectMapper();
			return (List<String>) mapper.readValue(res_data, Map.class).get("events");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**为指定工厂创建一个webhook
	 * @param p_id
	 * @param callbackURL
	 * @param events
	 * @return
	 */
	public static String createWebhook4Project(String p_id,String callbackURL,List<String> events){
		String url="/api/projects/%s/hooks";
		String req_url = String.format("%s"+url+"?access_token=%s", api_host,p_id,access_token);
		
		Map<String,Object> params  = new HashMap<String,Object>();
		params.put("access_token", access_token);
		params.put("callbackURL", callbackURL);
		params.put("active", "true");
		//params.put("events", events);
		
		logger.info(String.format("req_url is : %s", req_url));
		String res_data = post_request(req_url,params);
		logger.info(String.format("res_data is : %s", res_data));
		return res_data;
	}
	
	/**为指定的组织创建一个webhook
	 * @param p_id
	 * @param callbackURL
	 * @param events
	 * @return
	 */
	public static String createWebhook4Organization(String p_id ,String callbackURL,List<String> events){
		String url="/api/organizations/%s/hooks/";
		String req_url = String.format("%s"+url+"?access_token=%s", api_host,p_id,access_token);
		Map<String,Object> params  = new HashMap<String,Object>();
		params.put("access_token", access_token);
		params.put("callbackURL", callbackURL);
		params.put("active", "true");
		//params.put("events", events);
		
		logger.info(String.format("req_url is : %s", req_url));
		String res_data = post_request(req_url,params);
		logger.info(String.format("res_data is : %s", res_data));
		return res_data;
	}
	
	
	/**更新某个工程的某个webhook信息
	 * @param p_id
	 * @param hook_id
	 * @param callbackURL
	 * @param events
	 * @return
	 */
	public static String updateWebhook4Project(String p_id,String hook_id,String callbackURL,List<String> events){
		String url="/api/projects/%s/hooks/%s";
		String req_url = String.format("%s"+url+"?access_token=%s", api_host,p_id,hook_id,access_token);
		
		Map<String,Object> params  = new HashMap<String,Object>();
		params.put("access_token", access_token);
		params.put("callbackURL", callbackURL);
		params.put("active", "true");
		params.put("events", events);
		
		logger.info(String.format("req_url is : %s", req_url));
		String res_data = put_request(req_url,params);
		logger.info(String.format("res_data is : %s", res_data));
		return res_data;
	}
	
	/**更新某个组织的某个webhook信息
	 * @param p_id
	 * @param callbackURL
	 * @param events
	 * @return
	 */
	public static String updateWebhook4Organization(String p_id ,String callbackURL,List<String> events){
		String url="/api/organizations/%s/hooks/";
		String req_url = String.format("%s"+url+"?access_token=%s", api_host,p_id,access_token);
		Map<String,Object> params  = new HashMap<String,Object>();
		params.put("access_token", access_token);
		params.put("callbackURL", callbackURL);
		params.put("active", "true");
		//params.put("events", events);
		
		logger.info(String.format("req_url is : %s", req_url));
		String res_data = post_request(req_url,params);
		logger.info(String.format("res_data is : %s", res_data));
		return res_data;
	}
	
	
	/**获取某个工程的已创建webhook信息
	 * @param p_id
	 * @return
	 */
	public static String listWebhook4Projects(String p_id) {
		String url="/api/projects/%s/hooks/";
		String req_url = String.format("%s"+url+"?access_token=%s", api_host,p_id,access_token);
		logger.info(String.format("req_url is : %s", req_url));
		String res_data = get_request(req_url);
		logger.info(String.format("res_data is : %s", res_data));
		return res_data;
	}
	
	/**获取某个组织的已创建的webhook列表
	 * @param _id
	 * @return
	 */
	public static String listWebhook4Organizations(String _id) {
		String url="/api/organizations/%s/hooks/";
		String res_data = get_request(String.format("%s"+url+"%s?access_token=%s", api_host,_id,access_token));
		logger.info(String.format("res_data is : %s", res_data));
		return res_data;
	}

	
	
//	**********************private method ***************************
	/**验证访问token
	 * @param req_url
	 * @param _access_token
	 * @return
	 */
	private static String check_access_token_request(String req_url,String _access_token) {
		try {
			boolean ssl = StringUtils.startsWith(req_url, "https") ? true : false;
			CloseableHttpClient httpclient = NetUtils.getHttpClient(ssl);
			HttpGet httpget = new HttpGet(req_url);
			httpget.addHeader(HttpHeaders.AUTHORIZATION,_access_token);
			CloseableHttpResponse response = httpclient.execute(httpget);
			logger.info(response.toString());
			if (response.getStatusLine().getStatusCode() < 300) {
				String res_body = EntityUtils.toString(response.getEntity());
				return ""+response.getStatusLine().getStatusCode()+res_body;
			}
		} catch (UnsupportedCharsetException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**发送一个get请求
	 * @param req_url
	 * @return
	 */
	private static String get_request(String req_url) {
		try {
			boolean ssl = StringUtils.startsWith(req_url, "https") ? true : false;
			CloseableHttpClient httpclient = NetUtils.getHttpClient(ssl);
			HttpGet httpget = new HttpGet(req_url);
			CloseableHttpResponse response = httpclient.execute(httpget);
			logger.info(response.toString());
			if (response.getStatusLine().getStatusCode() < 300) {
				String res_body = EntityUtils.toString(response.getEntity(),Consts.UTF_8);
				return res_body;
			}
		} catch (UnsupportedCharsetException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**发送一个form格式数据的post请求
	 * @param req_url
	 * @param params
	 * @return
	 */
	private static String post_request(String req_url, String params) {
		try {
			boolean ssl = StringUtils.startsWith(req_url, "https") ? true : false;
			CloseableHttpClient httpclient = NetUtils.getHttpClient(ssl);
			HttpPost httpPost = new HttpPost(req_url);
			httpPost.addHeader(HttpHeaders.USER_AGENT,
					"Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803");
			httpPost.addHeader(HttpHeaders.CONTENT_ENCODING, "utf-8");
			List<NameValuePair> formparams = URLEncodedUtils.parse(params, Consts.UTF_8, '&');
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
			httpPost.setEntity(entity);
			CloseableHttpResponse response = httpclient.execute(httpPost);

			logger.info(response.toString());
			if (response.getStatusLine().getStatusCode() < 300) {
				String res_body = EntityUtils.toString(response.getEntity());
				return res_body;
			}
		} catch (UnsupportedCharsetException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**发送一个json格式数据的post请求
	 * @param req_url
	 * @param params
	 * @return
	 */
	private static String post_request(String req_url, Map<String,Object> params) {
		try {
			boolean ssl = StringUtils.startsWith(req_url, "https") ? true : false;
			CloseableHttpClient httpclient = NetUtils.getHttpClient(ssl);
			
			HttpPost httpPost = new HttpPost(req_url);
			ContentType contentType = ContentType.create(ContentType.APPLICATION_JSON.getMimeType(), Consts.UTF_8);
			StringEntity entity = new StringEntity(new ObjectMapper().writeValueAsString(params),contentType);
			httpPost.setEntity(entity);
			CloseableHttpResponse response = httpclient.execute(httpPost);

			logger.info(response.toString());
			if (response.getStatusLine().getStatusCode() < 300) {
				String res_body = EntityUtils.toString(response.getEntity());
				return res_body;
			}
		} catch (UnsupportedCharsetException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**发送一个json格式数据的put请求
	 * @param req_url
	 * @param params
	 * @return
	 */
	private static String put_request(String req_url, Map<String,Object> params) {
		try {
			boolean ssl = StringUtils.startsWith(req_url, "https") ? true : false;
			CloseableHttpClient httpclient = NetUtils.getHttpClient(ssl);
			
			HttpPut httpPut = new HttpPut(req_url);
			ContentType contentType = ContentType.create(ContentType.APPLICATION_JSON.getMimeType(), Consts.UTF_8);
			StringEntity entity = new StringEntity(new ObjectMapper().writeValueAsString(params),contentType);
			httpPut.setEntity(entity);
			CloseableHttpResponse response = httpclient.execute(httpPut);

			logger.info(response.toString());
			if (response.getStatusLine().getStatusCode() < 300) {
				String res_body = EntityUtils.toString(response.getEntity());
				return res_body;
			}
		} catch (UnsupportedCharsetException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
