package com.weixin.qy.rests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.commons.CommonUtils;
import com.weixin.qy.entity.TextMassegeContent;
import com.weixin.qy.entity.TextMessage;
import com.weixin.qy.entity.WeixinMessage;

public class WeiXinHandler implements Runnable {

	Logger logger = LoggerFactory.getLogger(WeiXinHandler.class);
	private String accessToken;
	private String xmlStr;

	WeiXinHandler(String accessToken, String str) {
		this.xmlStr = str;
		this.accessToken = accessToken;
	}

	@Override
	public void run() {

		WeixinMessage wxm = CommonUtils.xml2Object(xmlStr, WeixinMessage.class);
		String replyMsg = turing(wxm.getContent());
		if (StringUtils.isNotEmpty(replyMsg)) {

			TextMassegeContent tmc = new TextMassegeContent();
			tmc.setContent(replyMsg);

			TextMessage _tm = new TextMessage();
			_tm.setText(tmc);
			_tm.setTouser(wxm.getFromUserName());
			_tm.setAgentid(wxm.getAgentID());
			_tm.setToparty("1");
			// _tm.setTotag(totag);
			String msg = CommonUtils.object2Json(_tm);
			WeiXinAPIUtil.sendMessage(accessToken, msg);
		}
	}

	public static void main(String[] args) {
		String xml = "";
		WeiXinHandler handler = new WeiXinHandler(
				"_woFjJl6sW3tCWME_M_sZmPsgIgN1mLlDwagomU9_Tw4I4_26rloRPUDxT_L2CP2IFu3AumRAFZv71r3l3RC0w",
				xml);
		handler.run();
	}

	private String turing(String content) {
		StringBuffer sb = null;
		BufferedReader reader = null;
		HttpURLConnection connection = null;
		try {
			String APIKEY = "c232f980ef2b261b6934506d67e8f0a8";
			String INFO = URLEncoder.encode(content, "utf-8");
			String getURL = "http://www.tuling123.com/openapi/api?key="
					+ APIKEY + "&info=" + INFO;
			URL getUrl = new URL(getURL);
			connection = (HttpURLConnection) getUrl.openConnection();
			connection.connect();

			// 取得输入流，并使用Reader读取
			reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "utf-8"));
			sb = new StringBuffer();
			String line = "";
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			String resp = sb.toString();
			logger.info("turing return msg: " + resp);
			Map<?, ?> resp_obj = CommonUtils.jsonToObject(Map.class, resp);

			return (String) resp_obj.get("text");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 断开连接
			try {
				if (connection != null)
					connection.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "";
	}

}
