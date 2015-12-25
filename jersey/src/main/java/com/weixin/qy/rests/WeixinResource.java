package com.weixin.qy.rests;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.commons.CommonUtils;
import com.qq.weixin.mp.aes.AesException;
import com.qq.weixin.mp.aes.WXBizMsgCrypt;
import com.weixin.qy.entity.MaterialQuery;
import com.weixin.qy.entity.WeixinMessage;

@Path("weixin/qy")
public class WeixinResource {

	Logger logger = LoggerFactory.getLogger(WeixinResource.class);

	public static String sToken = "YLc4SlkHpNQLoWFOfiUgYIeiQr";
	public static String sEncodingAESKey = "5A5iDCGKwTDn6aujq7t14XqsKsRIdCXPwsml4f2tmAZ";
	public static String sCorpID = "wx6109d2b23a0abd2f";

	public static String appEncodingAESKey = "Na4fKP0Ugc0gd0Vf4l7luvMyYngDhq23XbMzs7TMfAzadVehcPiEAwc5FoZz51-p";
	public static String appCorpID = "wx6109d2b23a0abd2f";

	String accessToken;

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
			// WeixinGlobalObject.getInstance().getBqueue().add(sMsg);

			WeixinMessage wxm = CommonUtils.xml2Object(sMsg,
					WeixinMessage.class);

			if (StringUtils.equals(wxm.getMsgType(), "text")) {
				String textContent = WeiXinHandler.turing(wxm.getContent());

				String replyMsg = "<xml><ToUserName><![CDATA[%s]]></ToUserName><FromUserName><![CDATA[%s]]></FromUserName><CreateTime>%s</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[%s]]></Content></xml>";
				replyMsg = String.format(replyMsg, wxm.getFromUserName(),
						wxm.getToUserName(), wxm.getCreateTime(), textContent);
				logger.info(String.format("replyMsg : %s", replyMsg));
				String encrypt = wxcpt.EncryptMsg(replyMsg, timeStamp, nonce);
				logger.info(String.format("encrypt : %s", encrypt));
				return encrypt;
			} else if (StringUtils.equals(wxm.getMsgType(), "voice")) {
				MaterialQuery param = new MaterialQuery();
				param.setAgentid(0);
				param.setType("voice");
				param.setOffset(0);
				param.setCount(10);
				if (StringUtils.isEmpty(accessToken)) {
					String resp = WeiXinAPIUtil.getAccessToken();
					Map<?, ?> resp_obj = CommonUtils.jsonToObject(Map.class,
							resp);
					accessToken = (String) resp_obj.get("access_token");
				}

				String jsonString = WeiXinAPIUtil.materialList(accessToken,
						param);
				Map<?, ?> result = CommonUtils.jsonToObject(Map.class,
						jsonString);
				int index = RandomUtils.nextInt(0, 4);
				@SuppressWarnings("rawtypes")
				String media_id = (String) ((Map) ((List) result
						.get("itemlist")).get(index)).get("media_id");
				String replyMsg = "<xml><ToUserName><![CDATA[%s]]></ToUserName><FromUserName><![CDATA[%s]]></FromUserName><CreateTime>%s</CreateTime><MsgType><![CDATA[voice]]></MsgType><Voice><MediaId><![CDATA[%s]]></MediaId></Voice></xml>";
				replyMsg = String.format(replyMsg, wxm.getFromUserName(),
						wxm.getToUserName(), wxm.getCreateTime(), media_id);
				logger.info(String.format("replyMsg : %s", replyMsg));
				String encrypt = wxcpt.EncryptMsg(replyMsg, timeStamp, nonce);
				logger.info(String.format("encrypt : %s", encrypt));
				return encrypt;
			}

		} catch (AesException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
