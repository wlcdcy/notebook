package com.example.socket;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.commons.CommonUtils;
import com.example.commons.NETUtils;

/**
 * 流量卡平台接口
 * 
 * @author Administrator
 *
 */
public class FlowCardAPIUtil {
	static Logger logger = LoggerFactory.getLogger(FlowCardAPIUtil.class);
	static String access_tokan = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6Imxhbmp6eWtqIiwiZXhwIjoxNDU2MzA3NzA5fQ.mbKB9XtApxnThF1KhRLhnnwdH5iR6fc66--4Wrbm2v8";

	/**
	 * 获取token
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public String getAccessToken(String username, String password) {
		String url = "http://120.26.213.169/api/access_token/";
		String params = String.format("username=%s&password=%s", username,
				password);
		String resultJson = NETUtils.httpPostWithForm(url, params);
		logger.info(resultJson);
		@SuppressWarnings("unchecked")
		Map<String, Object> resultObj = CommonUtils.jsonToObject(Map.class,
				resultJson);
		int codeValue = (Integer) resultObj.get("code");
		if (codeValue == 200) {
			return (String) resultObj.get("token");
		}
		return null;
	}

	public static void main(String[] args) {
		FlowCardAPIUtil api = new FlowCardAPIUtil();
//		access_tokan = api.getAccessToken("lanjzykj", "123123");
//		logger.info(access_tokan);

		List mobiles = new ArrayList();
		mobiles.add("1064822442750");
		// mobiles.add("1064910303586");

		String content = "api test"; // "这是流量卡平台接口API的短信接口测试信息。"
		api.sendMobileMessage(mobiles, content);
		
		List<Map> billGroupdata = api.billGroup();
		Map group = billGroupdata.get(1);
		String carrier = (String)group.get("carrier");
		String name = (String)group.get("name");
		int data_plan =(Integer)group.get("data_plan");
		String bg_code = (String)group.get("bg_code");
		
		api.billGroupCards(bg_code, null, null);
	} 

	/**
	 * 短信发送
	 * 
	 * @param mobiles
	 * @param content
	 * @return
	 */
	public String sendMobileMessage(List mobiles, String content) {
		String url = "http://120.26.213.169/api/sms/";
		Map msgMap = new HashMap();
		msgMap.put("mobiles", mobiles);
		msgMap.put("content", content);
		String paramJson = CommonUtils.object2Json(msgMap);

		HttpPost hpost = NETUtils.createPOSTWithJson(url, paramJson);
		hpost.setHeader("Authorization", "JWT " + access_tokan);
		
		boolean https = StringUtils.startsWith(url, "https") ? true : false;
		String resultJson = new String(NETUtils.httpPost(hpost, https));
		
		logger.info(resultJson);
		return resultJson;
	}

	/**计费组列表
	 * 
	 */
	public List billGroup() {
		String url = "http://120.26.213.169/api/billing_group/";
		
		HttpGet get =NETUtils.createHttpGet(url);
		get.setHeader("Authorization", "JWT " + access_tokan);
		
		boolean https = StringUtils.startsWith(url, "https") ? true : false;
		String resultJson;
		resultJson = NETUtils.httpGet(get,https);
		logger.info(resultJson);
		Map resultObj = CommonUtils.jsonToObject(Map.class, resultJson);
		int codeValue = (Integer) resultObj.get("code");
		if (codeValue == 200) {
			return (List) resultObj.get("data");
		}
		return null;
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public Map billGroupCards(String bg_code,Integer page,Integer per_page){
		String url ="http://120.26.213.169/api/card/";
		int _page = page==null?1:page.intValue();
		int _pre_page = per_page==null?100:per_page.intValue();
		String param = String.format("%s&%s&%s", bg_code,_page,_pre_page);
		
		HttpGet get =NETUtils.createHttpGet(String.format("%s?%s", url,param));
		get.setHeader("Authorization", "JWT " + access_tokan);
		
		boolean https = StringUtils.startsWith(url, "https") ? true : false;
		String resultJson;
		resultJson = NETUtils.httpGet(get,https);
		logger.info(resultJson);
		Map resultObj = CommonUtils.jsonToObject(Map.class, resultJson);
		int codeValue = (Integer) resultObj.get("code");
		return resultObj;
	}
	
}
