package com.example.commons;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.UnsupportedCharsetException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OscUtil {

	private static Logger logger = LoggerFactory.getLogger(OscUtil.class);
	private static String client_id = "rVvmjArEXwSioLakrx5M";
	private static String client_secret="ZgU8C2mUKeK6WHS0G4xIvDpclKt6JG2l";
	private static String base_url="https://www.oschina.net";
	
	static String redirect_uri="http://113.135.242.43:808/jersey/webhook/osc/authback";
	
	public static String auth_code="LDHcgP";
	public static String req_state="xyz";
	public static String access_token="08f4d71c-ff3f-4d5e-84b1-3f47c6e3ef71";
	public static String refresh_token="d0ec43d3-1c8a-4288-a6e1-d901d701acc0"; 
	
	
	public static String getOauth2AuthUrl(){
		String url="/action/oauth2/authorize";
		String response_type="code";
		
		String param=String.format("response_type=%s&client_id=%s&state=%s&redirect_uri=%s",response_type,client_id,req_state,URLEncoder.encode(redirect_uri));
		return base_url+url+"?"+param;
	}
	
	public static String fetchOauth2Token(String code){
		String url="/action/openapi/token";
		String param=String.format("dataType=%s&code=%s&grant_type=%s&client_id=%s&client_secret=%s&redirect_uri=%s","json",code,"authorization_code",client_id,client_secret,URLEncoder.encode(redirect_uri));
		logger.info(String.format("req_data is : %s", param));
		String res_data=get_request(String.format("%s%s?%s", base_url,url,param));
		logger.info(String.format("res_data is : %s", res_data));
		return res_data;
	}
	
	public static String search(String catalog,String words){
		String url="/action/openapi/search_list";
		String param=String.format("dataType=%s&access_token=%s&catalog=%s&q=%s","json",access_token,catalog,words);
		logger.info(String.format("req_data is : %s", param));
		String res_data=post_request(url,param);
		logger.info(String.format("res_data is : %s", res_data));
		return res_data;
		
	}
	
	private static String buildReqeustUrl(String relative_url,String all_params){
		return String.format("%s%s?%s", base_url,relative_url,all_params);
	}
	
	public static String post_request(String req_url,String req_data){
		try {
			req_url = buildReqeustUrl(req_url,req_data);
			
			boolean ssl =StringUtils.startsWith(req_url, "https")? true:false;
			CloseableHttpClient httpclient = NetUtil.getHttpClient(ssl);
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
			CloseableHttpClient httpclient = NetUtil.getHttpClient(ssl);
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
	
	private static String request(String method,String req_url,String params){
		try {
			req_url = buildReqeustUrl(req_url,params);
			boolean ssl =StringUtils.startsWith(req_url, "https")? true:false;
			
			CloseableHttpClient httpclient = NetUtil.getHttpClient(ssl);
			
			HttpRequestBase httpRequest = getHttpRequestBase(method,req_url);
			if(httpRequest==null){
				logger.info(String.format("not support method : %s ", method));
				return null;
			}
			
			CloseableHttpResponse response =httpclient.execute(httpRequest);
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
	
	private static HttpRequestBase getHttpRequestBase(String method,String req_url){
		HttpRequestBase httpRequest;
		if(StringUtils.equals(HttpGet.METHOD_NAME, method)){
			httpRequest = new HttpGet(req_url);
		}else if(StringUtils.equals(HttpPost.METHOD_NAME, method)){
			httpRequest = new HttpPost(req_url);
		}else{
			return null;
		}
		httpRequest.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803");
		return httpRequest;
	}
	
	
	
	public static void main(String [] args){
		search("0","java");
	}

	
}
