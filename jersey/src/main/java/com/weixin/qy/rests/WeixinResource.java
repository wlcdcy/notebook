package com.weixin.qy.rests;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qq.weixin.mp.aes.AesException;
import com.qq.weixin.mp.aes.WXBizMsgCrypt;

@Path("weixin/qy")
public class WeixinResource {

	Logger logger = LoggerFactory.getLogger(WeixinResource.class);

	public static String sToken = "YLc4SlkHpNQLoWFOfiUgYIeiQr";
	public static String sEncodingAESKey = "5A5iDCGKwTDn6aujq7t14XqsKsRIdCXPwsml4f2tmAZ";
	public static String sCorpID = "wx6109d2b23a0abd2f";

	public static String appEncodingAESKey = "Na4fKP0Ugc0gd0Vf4l7luvMyYngDhq23XbMzs7TMfAzadVehcPiEAwc5FoZz51-p";
	public static String appCorpID = "wx6109d2b23a0abd2f";

	WXBizMsgCrypt wxcpt;

	@GET
	@Path("hello")
	@Produces(MediaType.TEXT_PLAIN)
	public String hello() {
		return String.format("this is [%s] service",
				WeixinResource.class.getName());
	}

	/**
	 * 企业号 建立链接【回调模式】验证
	 * 
	 * @param msg_signature
	 * @param timestamp
	 * @param nonce
	 * @param echostr
	 * @return
	 */
	@Path("callback")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String addressBook(
			@QueryParam("msg_signature") String msg_signature,
			@QueryParam("timestamp") String timestamp,
			@QueryParam("nonce") String nonce,
			@QueryParam("echostr") String echostr) {

		try {
			wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);
		} catch (AesException e) {
			e.printStackTrace();
		}

		String sEchoStr = null;
		try {
			sEchoStr = wxcpt
					.VerifyURL(msg_signature, timestamp, nonce, echostr);
			logger.info("verifyurl echostr: " + sEchoStr);
			// 验证URL成功，将sEchoStr返回
			// HttpUtils.SetResponse(sEchoStr);
		} catch (Exception e) {
			// 验证URL失败，错误原因请查看异常
			e.printStackTrace();
		}

		return sEchoStr;
	}

	/**
	 * 企业号 建立链接【回调模式】
	 * 
	 * @param msgSignature
	 * @param timeStamp
	 * @param nonce
	 * @param postData
	 * @return
	 */
	@Path("callback")
	@POST
	@Produces(MediaType.APPLICATION_XML)
	public String addressBook_post(
			@QueryParam("msg_signature") String msgSignature,
			@QueryParam("timestamp") String timeStamp,
			@QueryParam("nonce") String nonce,

			String postData) {

		try {
			wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);
		} catch (AesException e) {
			e.printStackTrace();
		}

		String sMsg = null;
		try {
			sMsg = wxcpt.DecryptMsg(msgSignature, timeStamp, nonce, postData);
			logger.info("after decrypt msg: " + sMsg);
			WeixinGlobalObject.getInstance().getBqueue().add(sMsg);

			// // TODO: 解析出明文xml标签的内容进行处理
			// DocumentBuilderFactory dbf =
			// DocumentBuilderFactory.newInstance();
			// DocumentBuilder db = dbf.newDocumentBuilder();
			// StringReader sr = new StringReader(sMsg);
			// InputSource is = new InputSource(sr);
			// Document document = db.parse(is);
			//
			// Element root = document.getDocumentElement();
			// NodeList nodes = root.getChildNodes();
			// for (int i = 0; i < nodes.getLength(); i++) {
			// Node node = nodes.item(i);
			// String name = node.getNodeName();
			// NodeList nodelist1 = root.getElementsByTagName(name);
			// if (nodelist1.item(0) != null) {
			// String Content = nodelist1.item(0).getTextContent();
			// logger.info(String.format("%s : %s", name, Content));
			// }
			// }

			// WeixinMessage wxm = WeiXinAPIUtil.xml2Object(sMsg,
			// WeixinMessage.class);
			// String
			// replyMsg="<xml><ToUserName><![CDATA[%s]]></ToUserName><FromUserName><![CDATA[%s]]></FromUserName><CreateTime>%s</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[%s]]></Content></xml>";
			// replyMsg = String.format(replyMsg,
			// wxm.getFromUserName(),wxm.getToUserName(),wxm.getCreateTime(),"你还");
			// logger.info(String.format("replyMsg : %s", replyMsg));
			// String encrypt= wxcpt.EncryptMsg(replyMsg, timeStamp, nonce);
			// logger.info(String.format("encrypt : %s", encrypt));
			// return encrypt;
		} catch (AesException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
