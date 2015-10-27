package com.weixin.qy.rests;

import java.io.StringReader;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.qq.weixin.mp.aes.AesException;
import com.qq.weixin.mp.aes.WXBizMsgCrypt;

@Path("weixin/qy")
public class EnterpriseResource {

	Logger logger = LoggerFactory.getLogger(EnterpriseResource.class);

	private String sToken="YLc4SlkHpNQLoWFOfiUgYIeiQr";
	private String sEncodingAESKey = "5A5iDCGKwTDn6aujq7t14XqsKsRIdCXPwsml4f2tmAZ";
	private String sCorpID="wx3c21617732d86306";
	
	WXBizMsgCrypt wxcpt;
	@GET
	@Path("hello")
	@Produces(MediaType.TEXT_PLAIN)
	public String hello() {
		return String.format("this is [%s] service",
				EnterpriseResource.class.getName());
	}

	@Path("address")
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
			sEchoStr = wxcpt.VerifyURL(msg_signature, timestamp,
					nonce, echostr);
			logger.info("verifyurl echostr: " + sEchoStr);
			// 验证URL成功，将sEchoStr返回
			// HttpUtils.SetResponse(sEchoStr);
		} catch (Exception e) {
			//验证URL失败，错误原因请查看异常
			e.printStackTrace();
		}
		
		return sEchoStr;
	}
	
	@Path("address")
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
			
			// TODO: 解析出明文xml标签的内容进行处理
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader(sMsg);
			InputSource is = new InputSource(sr);
			Document document = db.parse(is);

			Element root = document.getDocumentElement();
			NodeList  nodes = root.getChildNodes();
			for(int i=0;i<nodes.getLength();i++){
				Node node =nodes.item(i);
				String name = node.getNodeName();
				NodeList nodelist1 = root.getElementsByTagName(name);
				String Content = nodelist1.item(0).getTextContent();
				logger.info(String.format("%s : %s", name,Content));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "";
	}
}
