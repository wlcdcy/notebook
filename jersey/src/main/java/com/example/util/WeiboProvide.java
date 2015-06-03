package com.example.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeiboProvide {
	
	public static Logger logger = LoggerFactory.getLogger(WeiboProvide.class);
	
	static String app_key="4158348570";//"1121941913";
	//static String access_token="2.00qaELFG_2YvNB01357c5fdatiIVPB";
	static String app_secret ="668d4cb0010c190c39e750cce37d06ca";//"2b3626dc0a956bc98e5b05afd1dbb608";
	static List<Map<String,Object>> ls = new ArrayList<Map<String,Object>>();
	static int since_id=0;

	public static void main(String[] args) throws ClientProtocolException, IOException {
		
		getAccessToken("");
//		friendsTimeLine("2.00HOqPrC_2YvNB281434b0feURlrzC",0);
	}
	
	public static long friendsTimeLine(String access_token,long since_id){
		String url = "https://api.weibo.com/2/statuses/friends_timeline.json";
		//long since_id=0;		//false	int64若指定此参数，则返回ID比since_id大的微博（即比since_id时间晚的微博），默认为0。
		int max_id=0;		//	false	int64	若指定此参数，则返回ID小于或等于max_id的微博，默认为0。
		int count=30;		//	false	int	单页返回的记录条数，最大不超过100，默认为20。
		int page=1;			//	false	int	返回结果的页码，默认为1。
		int base_app=0;		//	false	int	是否只获取当前应用的数据。0为否（所有数据），1为是（仅当前应用），默认为0。
		int feature=0;		//	false	int	过滤类型ID，0：全部、1：原创、2：图片、3：视频、4：音乐，默认为0。
		int trim_user=0;	//	false	int	返回值中user字段开关，0：返回完整user字段、1：user字段仅返回user_id，默认为0。

		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String getUrl = String.format("%s?source=%s&access_token=%s&since_id=%s", url,app_key,access_token,since_id);
		logger.info(getUrl); 
		HttpGet httpGet = new HttpGet(getUrl);
		//HttpGet httpGet = new HttpGet(String.format("%s?&access_token=%s", url,access_token));
		CloseableHttpResponse response=null;
		
		try {
			response = httpclient.execute(httpGet);
		    HttpEntity entity = response.getEntity();
		    String resp_content=EntityUtils.toString(entity);
		    EntityUtils.consume(entity);
		    
		    logger.info(resp_content); 
		    
		    JSONObject jsonObj=JSONObject.fromObject(resp_content);  
		    List<Map> msgs =JSONArray.toList((JSONArray)jsonObj.get("statuses"),Map.class);
		    if(msgs!=null && msgs.size()>0){
		    	ls.clear();
//		    	获取最新微博的id
			    since_id = (Long)msgs.get(0).get("id");
			    for(Map msg :msgs){
			    	logger.info(String.format("%d %s %s",msg.get("id"), msg.get("created_at"), msg.get("text")));
			    	
			    	Map<String,Object> m=new HashMap<String,Object>();
			    	m.put("id",msg.get("id"));
			    	m.put("created_at", msg.get("created_at"));
			    	m.put("text", msg.get("text"));
			    	ls.add(m);
			    }
//			    推送消息到hiwork
			    pustHiwork(ls);
		    }
		   
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		    try {
		    	if(response!=null)
		    		response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return since_id;
	}
	
	
	public static String getAccessToken(String auth_code) throws ClientProtocolException, IOException{
		//auth_code = "0ebc90cad97041ac57615c0af924f729";//
		String url = "https://api.weibo.com/oauth2/access_token";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		
		
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("client_id", app_key));
		nvps.add(new BasicNameValuePair("client_secret", app_secret));
		nvps.add(new BasicNameValuePair("grant_type", "authorization_code"));
		nvps.add(new BasicNameValuePair("code", auth_code));
		nvps.add(new BasicNameValuePair("redirect_uri", "http://1.84.152.224:808/jersey/hooks/weibo/auth"));
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response = httpclient.execute(httpPost);

		try {
		    logger.info(""+response.getStatusLine()); 
		    HttpEntity entity = response.getEntity();
		    String resp_content=EntityUtils.toString(entity);
		    EntityUtils.consume(entity);
		    
		    logger.info(resp_content); 
		    
		    JSONObject jsonObj=JSONObject.fromObject(resp_content);
//		    jsonObj.get("expires_in");
//		    jsonObj.get("remind_in");
//		    jsonObj.get("uid");
		    return (String)jsonObj.get("access_token");
		} finally {
		    response.close();
		}
	}

	
	public static void pustHiwork(List<Map<String,Object>> l){
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String url="http://36.44.54.111:9090/plugins/zhservice/api/sendmsg";
		String token="d20647cb-f3dc-4ef1-90ca-d028cdc25a43";
		Map<String, Object> map =new HashMap<String, Object>();
		map.put("token", token);
		map.put("data", l);
		String json =JSONObject.fromObject(map).toString();
		logger.info(json);
		
		HttpPost httpPost = new HttpPost(url);
		StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
		httpPost.setEntity(entity);
		CloseableHttpResponse response=null;
		try {
			response =httpclient.execute(httpPost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
