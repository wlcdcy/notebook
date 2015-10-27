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
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OscUtils {

	private static Logger logger = LoggerFactory.getLogger(OscUtils.class);
	private static String client_id = "rVvmjArEXwSioLakrx5M";
	private static String client_secret="ZgU8C2mUKeK6WHS0G4xIvDpclKt6JG2l";
	private static String base_url="https://www.oschina.net";
	
	static String redirect_uri="http://xianzhouhe.eicp.net/jersey/webhook/osc/authback";
	
	public static String auth_code="LDHcgP";
	public static String req_state="xyz";
	public static String access_token="a98ed576-00bd-48d8-aa76-b1853e8d9f9e";
	public static String refresh_token="f1e122a3-09bf-43e1-a302-4c782ccea846"; 
	
	
	/**获取osc认证地址
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String getOauth2AuthUrl(){
		String url="/action/oauth2/authorize";
		String response_type="code";
		
		String param=String.format("response_type=%s&client_id=%s&state=%s&redirect_uri=%s",response_type,client_id,req_state,URLEncoder.encode(redirect_uri));
		return base_url+url+"?"+param;
	}
	
	/**获取osc认证后的访问token
	 * @param code
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String fetchOauth2Token(String code){
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
	public String search(String catalog,String words){
		String url="/action/openapi/search_list";
		try {
			words = URLEncoder.encode(words,"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
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
	public String pub_posts(Integer isNoticeMe,Integer catalog ,String title,String content,Long askuser){
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
	
	/**获取动弹信息
	 * @param userid	【0标示热门动弹，-1标示最新动弹，其它值标示自己的动弹】
	 * @return
	 */
	public String list_tweet(long userid){
		String url = "/action/openapi/tweet_list";
		StringBuffer sb = new StringBuffer();
		sb.append("access_token="+access_token).append("&user="+userid);
		String params = sb.toString();
		logger.info(String.format("req_data is : %s", params));
		
		String res_data = post_request(url, params);
		logger.info(String.format("res_data is : %s", res_data));
		return res_data;
	}
	/**	发布一条纯文本的动弹
	 * @param msg	消息内容
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String pub_tweet(String msg){
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
	
	/**	发布一条带图片的的动弹
	 * @param msg	消息内容
	 * @param img	img(object type)可以是File、InputStram、byte[]类型
	 * @return
	 */
	public String pub_tweet(String msg, Object img){
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
	
	/**获取软件列表	recommend-推荐|time-最新|view-热门|cn-国产
	 * @param type
	 * @return
	 */
	public String project_list(String type){
		String url = "/action/openapi/project_list";
		StringBuffer sb = new StringBuffer();
		sb.append("access_token="+access_token).append("&type="+type);
		String params = sb.toString();
		logger.info(String.format("req_data is : %s", params));
		
		String res_data = post_request(url, params);
		logger.info(String.format("res_data is : %s", res_data));
		return res_data;
	}
	
	
	
	private String buildReqeustUrl(String relative_url,String all_params){
		return String.format("%s%s?%s", base_url,relative_url,all_params);
	}
	private String buildReqeustUrlWithOutParam(String relative_url){
		return String.format("%s%s", base_url,relative_url);
	}
	
	public String post_request(String req_url,String req_data){
		try {
			req_url = buildReqeustUrl(req_url,req_data);
			
			boolean ssl =StringUtils.startsWith(req_url, "https")? true:false;
			CloseableHttpClient httpclient = NetUtils.getHttpClient(ssl);
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
	
	private String get_request(String req_url){
		try {
			boolean ssl =StringUtils.startsWith(req_url, "https")? true:false;
			CloseableHttpClient httpclient = NetUtils.getHttpClient(ssl);
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
	
	private String request(String method,String url,String params){
		try {
			String req_url = buildReqeustUrlWithOutParam(url);
			boolean ssl =StringUtils.startsWith(req_url, "https")? true:false;
			
			CloseableHttpClient httpclient = NetUtils.getHttpClient(ssl);
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
	
	private String request(String method,String url,Map<String,Object> params){
		try {
			String req_url = buildReqeustUrlWithOutParam(url);
			boolean ssl =StringUtils.startsWith(req_url, "https")? true:false;
			
			CloseableHttpClient httpclient = NetUtils.getHttpClient(ssl);
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
	
	private HttpGet getHttpGet(String req_url){
		HttpGet httpRequest = new HttpGet(req_url);
		httpRequest.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803");
		return httpRequest;
	}
	
	private HttpPost getHttpPost(String req_url,String params){
		HttpPost httpPost = new HttpPost(req_url);
		httpPost.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803");
		httpPost.addHeader(HttpHeaders.CONTENT_ENCODING,"utf-8");
		List<NameValuePair> formparams = URLEncodedUtils.parse(params, Consts.UTF_8,'&');
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,Consts.UTF_8);
		httpPost.setEntity(entity);
		return httpPost;
	}
	
	private HttpPost getHttpPost(String req_url,Map<String,Object> params){
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
				ContentType contentType = ContentType.create(ContentType.APPLICATION_OCTET_STREAM.getMimeType());
				FileBody fileBody = new FileBody((File)value,contentType);
				entityBuilder.addPart(key, fileBody);
				continue;
			}
			if(value instanceof InputStream){
				InputStreamBody inputBody = new InputStreamBody((InputStream)value, "img.png");
				entityBuilder.addPart(key, inputBody);
				continue;
			}
			if(value instanceof byte[]){
				ByteArrayBody  byteBody = new ByteArrayBody((byte[])value,"img.png");
				entityBuilder.addPart(key, byteBody);
				continue;
			}
			logger.info(String.format("not found object type for %s param",key));
		}
		httpPost.setEntity(entityBuilder.build());
		return httpPost;
	}
	
	
	public static void main(String [] args) throws FileNotFoundException{
		OscUtils oscUtil = new OscUtils();
//		search("news","java");
//		pub_posts(2,100,"java招聘推荐","",null);
		File f =new File("d:/20150612113904.png");
		
//		byte[] b = new byte[Integer.valueOf(""+f.length())] ;
		
		InputStream in = new FileInputStream(f);
		
		try {
//			ImageInputStream  imageIs=null;
//			imageIs = ImageIO.createImageInputStream(in);
//			imageIs.read(b);
			
//			pub_tweet("@开源中国  @乔布斯  @小编辑 【这不是恶搞，这真是一个问题】/action/openapi/tweet_pub中的【img	false	image	图片流	】怎么使用?我用流传输了，发布也正常，文字可以显示出来，可是图片没显示出来，谁知道什么原因？",b);
//			pub_tweet("@开源中国  @乔布斯  @小编辑 【这不是恶搞，这真是一个问题】/action/openapi/tweet_pub中的【img	false	image	图片流	】怎么使用?我用流传输了，发布也正常，文字可以显示出来，可是图片没显示出来，谁知道什么原因？",f);
			//oscUtil.pub_tweet("@开源中国  @乔布斯  @小编辑  【这不是恶搞，这是一次求助】请问一条沉寂了多年的帖子，怎样才能让再置顶呢？回帖有惊喜哦",in);
			oscUtil.list_tweet(-1);
		} finally{
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	
}
