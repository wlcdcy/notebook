package com.example.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OscUtils {

	private static Logger logger = LoggerFactory.getLogger(OscUtils.class);
	private static String client_id = "rVvmjArEXwSioLakrx5M";
	private static String client_secret="ZgU8C2mUKeK6WHS0G4xIvDpclKt6JG2l";
	private static String base_url="https://www.oschina.net";
	
	static String redirect_uri="http://113.135.242.43:808/jersey/webhook/osc/authback";
	
	public static String auth_code="LDHcgP";
	public static String req_state="xyz";
	public static String access_token="08f4d71c-ff3f-4d5e-84b1-3f47c6e3ef71";
	public static String refresh_token="d0ec43d3-1c8a-4288-a6e1-d901d701acc0"; 
	
	
	/**获取osc认证地址
	 * @return
	 */
	public static String getOauth2AuthUrl(){
		String url="/action/oauth2/authorize";
		String response_type="code";
		
		String param=String.format("response_type=%s&client_id=%s&state=%s&redirect_uri=%s",response_type,client_id,req_state,URLEncoder.encode(redirect_uri));
		return base_url+url+"?"+param;
	}
	
	/**获取osc认证后的访问token
	 * @param code
	 * @return
	 */
	public static String fetchOauth2Token(String code){
		String url="/action/openapi/token";
		String param=String.format("dataType=%s&code=%s&grant_type=%s&client_id=%s&client_secret=%s&redirect_uri=%s","json",code,"authorization_code",client_id,client_secret,URLEncoder.encode(redirect_uri));
		logger.info(String.format("req_data is : %s", param));
		String res_data=get_request(String.format("%s%s?%s", base_url,url,param));
		logger.info(String.format("res_data is : %s", res_data));
		return res_data;
	}
	
	/**osc搜索【news-新闻，blog-博客，project-开源软件，post-帖子、问答】
	 * @param catalog	enum【"news","blog","project","post","0"】
	 * @param words
	 * @return
	 */
	public static String search(String catalog,String words){
		String url="/action/openapi/search_list";
		String param=String.format("dataType=%s&access_token=%s&catalog=%s&q=%s","json",access_token,catalog,words);
		logger.info(String.format("req_data is : %s", param));
		String res_data=post_request(url,param);
		logger.info(String.format("res_data is : %s", res_data));
		return res_data;
		
	}
	
	/**osc发帖
	 * @param isNoticeMe
	 * @param catalog
	 * @param title
	 * @param content
	 * @param askuser
	 * @return
	 */
	public static String pub_posts(Integer isNoticeMe,Integer catalog ,String title,String content,Long askuser){
		String url="/action/openapi/post_pub";
//		access_token	true	string	oauth2_token获取的access_token	
//		isNoticeMe		false	int	有回答是否邮件通知 2是邮件通知	0
//		catalog			true	int	类别ID 1-问答 2-分享 3-IT杂烩(综合) 4-站务 100-职业生涯	1
//		title			true	string	帖子标题	
//		content			true	string	帖子内容	
//		askuser			false	long	用户id（向某人提问）
		
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("access_token="+access_token).append("&catalog="+catalog).append("&title="+URLEncoder.encode(title, "utf-8")).append("&content="+URLEncoder.encode(content, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(isNoticeMe!=null){
			sb.append("&isNoticeMe="+isNoticeMe);
		}
		if(askuser!=null){
			sb.append("&askuser="+askuser);
		}
		String params = sb.toString();
		logger.info(String.format("req_data is : %s", params));
		String res_data = request(HttpPost.METHOD_NAME, url, params);
		logger.info(String.format("res_data is : %s", res_data));
		return res_data;
	}
	
	public static String pub_tweet(String msg){
//		access_token	true	string	oauth2_token获取的access_token	
//		msg	true		string	动弹内容	
//		img	false		image	图片流
		
		String url = "/action/openapi/tweet_pub";
		StringBuffer sb = new StringBuffer();
		sb.append("access_token="+access_token).append("&msg="+URLEncoder.encode(msg));
		String params = sb.toString();
		logger.info(String.format("req_data is : %s", params));
		String res_data = request(HttpPost.METHOD_NAME, url, params);
		logger.info(String.format("res_data is : %s", res_data));
		return res_data;
	}
	
	public static String pub_tweet(String msg,InputStream img){
		if(img==null){
			return pub_tweet(msg);
		}
		String url = "/action/openapi/tweet_pub";
		Map<String,Object> params= new HashMap<String,Object>();
		params.put("access_token", access_token);
		params.put("msg", msg);
		params.put("img", img);	
		logger.info(String.format("req_data is : %s", params));
		String res_data = request(HttpPost.METHOD_NAME, url, params);
		logger.info(String.format("res_data is : %s", res_data));
		return res_data;
	}
	
	
	
	private static String buildReqeustUrl(String relative_url,String all_params){
		return String.format("%s%s?%s", base_url,relative_url,all_params);
	}
	private static String buildReqeustUrlWithOutParam(String relative_url){
		return String.format("%s%s", base_url,relative_url);
	}
	
	public static String post_request(String req_url,String req_data){
		try {
			req_url = buildReqeustUrl(req_url,req_data);
			
			boolean ssl =StringUtils.startsWith(req_url, "https")? true:false;
			CloseableHttpClient httpclient = NetClientUtils.createHttpClient(ssl);
			HttpPost httpPost = new HttpPost(req_url);
			httpPost.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803");
			CloseableHttpResponse response =httpclient.execute(httpPost);
			logger.info(response.toString());
			if(response.getStatusLine().getStatusCode()<300){
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
	
	private static String get_request(String req_url){
		try {
			boolean ssl =StringUtils.startsWith(req_url, "https")? true:false;
			CloseableHttpClient httpclient = NetClientUtils.createHttpClient(ssl);
			HttpGet httpget = new HttpGet(req_url);
			httpget.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803");
			CloseableHttpResponse response =httpclient.execute(httpget);
			logger.info(response.toString());
			if(response.getStatusLine().getStatusCode()<300){
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
	
	private static String request(String method,String url,String params){
		try {
			String req_url = buildReqeustUrlWithOutParam(url);
			boolean ssl =StringUtils.startsWith(req_url, "https")? true:false;
			
			CloseableHttpClient httpclient = NetClientUtils.createHttpClient(ssl);
			CloseableHttpResponse response = null ;
			
			if(StringUtils.equals(HttpGet.METHOD_NAME, method)){
				response =httpclient.execute(getHttpGet(req_url));
			}else if(StringUtils.equals(HttpPost.METHOD_NAME, method)){
				response =httpclient.execute(getHttpPost(req_url,params));
			}else{
				return null;
			}
			logger.info(response.toString());
			if(response.getStatusLine().getStatusCode()<300){
				return EntityUtils.toString(response.getEntity());
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
	
	private static String request(String method,String url,Map<String,Object> params){
		try {
			String req_url = buildReqeustUrlWithOutParam(url);
			boolean ssl =StringUtils.startsWith(req_url, "https")? true:false;
			
			CloseableHttpClient httpclient = NetClientUtils.createHttpClient(ssl);
			CloseableHttpResponse response = null ;
			
			if(StringUtils.equals(HttpGet.METHOD_NAME, method)){
				response =httpclient.execute(getHttpGet(req_url));
			}else if(StringUtils.equals(HttpPost.METHOD_NAME, method)){
				response =httpclient.execute(getHttpPost(req_url,params));
			}else{
				return null;
			}
			logger.info(response.toString());
			if(response.getStatusLine().getStatusCode()<300){
				return EntityUtils.toString(response.getEntity());
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
	
	private static HttpGet getHttpGet(String req_url){
		HttpGet httpRequest = new HttpGet(req_url);
		httpRequest.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803");
		return httpRequest;
	}
	
	private static HttpPost getHttpPost(String req_url,String params){
		HttpPost httpPost = new HttpPost(req_url);
		httpPost.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803");
		httpPost.addHeader(HttpHeaders.CONTENT_ENCODING,"utf-8");
		List<NameValuePair> formparams = URLEncodedUtils.parse(params, Consts.UTF_8,'&');
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,Consts.UTF_8);
		httpPost.setEntity(entity);
		return httpPost;
	}
	
	private static HttpPost getHttpPost(String req_url,Map<String,Object> params){
		HttpPost httpPost = new HttpPost(req_url);
		httpPost.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803");
		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
		Iterator<String> keys = params.keySet().iterator();
		while(keys.hasNext()){
			String key  = keys.next();
			Object value  = params.get(key);
			if(value instanceof String){
//				使用part代替body解决乱码问题[entityBuilder.addTextBody(key, (String) value,ContentType.TEXT_PLAIN);]
				ContentType contentType = ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), Consts.UTF_8);
				StringBody stringBody = new StringBody((String)value,contentType);
				entityBuilder.addPart(key, stringBody);
				continue;
			}
			if(value instanceof File){
				entityBuilder.addBinaryBody(key, (File)value);
				continue;
			}
			if(value instanceof InputStream){
				entityBuilder.addBinaryBody(key, (InputStream)value);
				continue;
			}
			if(value instanceof byte[]){
				entityBuilder.addBinaryBody(key, (byte[])value);
				continue;
			}
			logger.info(String.format("not found object type for %s param",key));
		}
		httpPost.setEntity(entityBuilder.build());
		return httpPost;
	}
	
	
	public static void main(String [] args) throws FileNotFoundException{
//		search("news","java");
//		pub_posts(2,100,"java招聘推荐","",null);
		
		pub_tweet("@小编辑 ：/action/openapi/tweet_pub中的【img	false	image	图片流	】怎么使用，",new FileInputStream("d:/20150612113904.png"));
		
		
	}

	
}
