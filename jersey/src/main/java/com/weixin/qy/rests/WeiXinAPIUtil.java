package com.weixin.qy.rests;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.commons.NETUtils;

public class WeiXinAPIUtil {
	static Logger logger = LoggerFactory.getLogger(WeiXinAPIUtil.class);

	/**
	 * 获取访问token
	 * 
	 * @return
	 */
	public static String getAccessToken() {
		// https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=id&corpsecret=secrect
		String req_url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s",
						WeixinResource.appCorpID,
						WeixinResource.appEncodingAESKey);
		CloseableHttpClient hc = NETUtils.getHttpClient(req_url
				.indexOf("https") == 0 ? true : false);
		HttpGet get = new HttpGet(req_url);
		try {
			CloseableHttpResponse response = hc.execute(get);

			if (response.getStatusLine().getStatusCode() == 200) {
				String res_body = EntityUtils.toString(response.getEntity());
				logger.info(res_body);
				return res_body;
			} else {
				String res_body = EntityUtils.toString(response.getEntity());
				logger.info(res_body);
			}
			;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 发送消息
	 * 
	 * @param access_token
	 * @param msg
	 */
	public static void sendMessage(String access_token, String msg) {
		// "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN"
		String req_url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=%s",
						access_token);

		boolean ssl = req_url.indexOf("https") == 0 ? true : false;

		CloseableHttpClient hc = NETUtils.getHttpClient(ssl);
		HttpPost post = new HttpPost(req_url);

		StringEntity entity = new StringEntity(msg,
				ContentType.APPLICATION_JSON);
		post.setEntity(entity);
		try {
			CloseableHttpResponse response = hc.execute(post);
			String res_body = EntityUtils.toString(response.getEntity());
			logger.info(res_body);
			if (response.getStatusLine().getStatusCode() != 200) {
				// String res_body = EntityUtils.toString(response.getEntity());
				// logger.info(res_body);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param access_token
	 * @param pid
	 *            id可以参数，当id不能空时，或者指定部门及其下的子部门，
	 * @return
	 */
	public String getDept(String access_token, String pid) {
		// https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=ACCESS_TOKEN&id=ID
		String url = String
				.format("https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=%s",
						access_token);
		if (StringUtils.isNotEmpty(pid)) {
			url += "&id=" + pid;
		}
		return NETUtils.request4GET(url);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> String object2XML(T obj) {

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Class<?> clazz = obj.getClass();
		Marshaller marshaller = null;
		if (marshaller == null) {
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
				marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
			} catch (JAXBException e) {
				throw new IllegalArgumentException(e);
			}
		}
		try {
			XmlRootElement rootElement = (XmlRootElement) clazz
					.getAnnotation(XmlRootElement.class);
			if (rootElement == null
					|| rootElement.name().equals(
							XmlRootElement.class.getMethod("name")
									.getDefaultValue().toString())) {
				marshaller.marshal(
						new JAXBElement(new QName("xml"), clazz, obj), os);
			} else {
				marshaller.marshal(obj, os);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					;
				}
			}
		}
		return new String(os.toByteArray());
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public static <T> T xml2Object(String xmlStr, Class<T> clazz) {

		Unmarshaller unmarshaller = null;
		if (unmarshaller == null) {
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
				unmarshaller = jaxbContext.createUnmarshaller();
				Reader reader = new StringReader(xmlStr);
				return (T) unmarshaller.unmarshal(reader);
			} catch (JAXBException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return null;
	}

	public static <T> String object2Json(T clazz) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(clazz);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T jsonToObject(Class<T> clazz, String jsonStr) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(jsonStr, clazz);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {

	}
}
