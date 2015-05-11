package com.example.resources;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;




















import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.util.WeiboProvide;


@Path("/hooks")
public class HookResource {
//	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	Logger logger = LoggerFactory.getLogger(HookResource.class);
//	String appKey="1121941913";
	String access_token="0ebc90cad97041ac57615c0af924f729";
	String appSecret="2b3626dc0a956bc98e5b05afd1dbb608";
	
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getIt() {
		return "Hello, Let go! use ResourceConfig Scanning  Hooks";
	}

	@Path("/github")
	@POST
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void github_form(@Context HttpServletRequest req , @FormParam("Payload")String formData) {
		String eventName = req.getHeader("X-Github-Event");
		String signature = req.getHeader("X-Hub-Signature");
		String deliverId = req.getHeader("X-Github-Delivery");
		logger.debug(String.format("[event:%s] [signature:%s] [deliverId:%s]", eventName,signature,deliverId));
		logger.debug(formData);
	}
	
	@Path("/github/{token}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Map<String,Object> github_json(@Context HttpServletRequest req ,@PathParam("token") String token,Map<String,Object> jsonData) {
		String eventName = req.getHeader("X-Github-Event");
		String signature = req.getHeader("X-Hub-Signature");
		String deliverId = req.getHeader("X-Github-Delivery");
		logger.debug(String.format("[event:%s] [signature:%s] [deliverId:%s]", eventName,signature,deliverId));
		logger.debug(jsonData.toString());
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("status", 0);
		return result;
	}
	
	
	@Path("/coding")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Map<String,Object> zcode_json(@Context HttpServletRequest req ,Map<String,Object> jsonData) {
		logger.debug(jsonData.toString());
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("status", 0);
		return result;
	}
	
	@Path("/gitlab")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void gitlab_json(@Context HttpServletRequest req ,Map<String,Object> jsonData) {
		ObjectMapper omap = new ObjectMapper();
		try {
			logger.debug(omap.writeValueAsString(jsonData));
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@POST
	@Path("/weibo")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String weibo_json(@Context HttpServletRequest req,String text) throws IOException {
		String timestamp = req.getParameter("timestamp");
		String signature = req.getParameter("signature");
		String nonce = req.getParameter("nonce");
		String echostr = req.getParameter("echostr");
		logger.debug(String.format("[signature:%s] [timestamp:%s] [nonce:%s] [echostr:%s]", signature,timestamp,nonce,echostr));
		System.out.println(String.format("[signature:%s] [timestamp:%s] [nonce:%s] [echostr:%s]", signature,timestamp,nonce,echostr));
		if(validateSHA(signature,nonce,timestamp)){
			if(org.apache.commons.lang.StringUtils.isNotBlank(echostr)){
				return echostr; 
			}else{
				//正常推送消息时不会存在echoStr参数。
				//接收post过来的消息数据
//				StringBuilder sb = new StringBuilder();
//				BufferedReader in = req.getReader();
//		        String line;
//		        while ((line = in.readLine()) != null) {
//		            sb.append(line);
//		        }
				//TODO 根据业务对消息进行处理。处理完成可以返回空串，也可以返回回复消息。
		        System.out.println("received message : " + text);
		        JSONObject jsonObj=JSONObject.fromObject(text);  
		        String msg =(String )jsonObj.get("text");
		        String type = (String )jsonObj.get("type");
		        
		        String senderId = String.valueOf(jsonObj.get("sender_id"));//回复消息的发送方id。即蓝v自己的uid
		        String receiverId =String.valueOf(jsonObj.get("receiver_id")); //回复消息的接收方id。蓝v粉丝的uid。这个字段需要在接收的推送消息中获取。
		        String created_at = (String )jsonObj.get("created_at");
		        
		        //需要回复消息时，修改returnContent为对应消息内容
		        //String returnContent = generateReplyMsg(textMsg(), "text", senderId, receiverId);//回复text类型消息
		        //returnContent = generateReplyMsg(articleMsg(), "articles", senderId, receiverId); //回复article类型消息
		        //returnContent = generateReplyMsg(positionMsg(), "position", senderId, receiverId);//回复position类型的消息
		        //System.out.println("returnContent : " + text);
		        return "";
			}
		}else{
			return "sign error!";
		}
		
	}
	
	
	@GET
	@Path("/weibo")
	@Produces(MediaType.APPLICATION_JSON)
	public String weibo_get(@Context HttpServletRequest req) throws IOException {
		String timestamp = req.getParameter("timestamp");
		String signature = req.getParameter("signature");
		String nonce = req.getParameter("nonce");
		String echostr = req.getParameter("echostr");
		logger.debug(String.format("[signature:%s] [timestamp:%s] [nonce:%s] [echostr:%s]", signature,timestamp,nonce,echostr));

		if(validateSHA(signature,nonce,timestamp)){
			if(org.apache.commons.lang.StringUtils.isNotBlank(echostr)){
				return echostr; 
			}
			return "request data error";
		}else{
			return "sign error!";
		}
		
	}
	
	@GET
	@Path("/weibo/auth")
	public void auth(@QueryParam("code") String auth_token){
		System.out.println(auth_token);
		try {
			String access_token = WeiboProvide.getAccessToken(auth_token);
			WeiboProvide.friendsTimeLine(access_token,0);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@GET
	@Path("/weibo/index.html")
	public void index(@Context HttpServletResponse resp) throws IOException{
		StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
		Configuration cfg = Configuration.defaultConfiguration();
		GroupTemplate gt= new GroupTemplate(resourceLoader,cfg);
		Template tmpl = gt.getTemplate("");
		tmpl.binding("","");
		String htm = tmpl.render();
		resp.getOutputStream().write(htm.getBytes());
	}
	
	@GET
	@Path("/weibo/unauth")
	public void unauth(@QueryParam("code") String access_token){
		System.out.println(access_token);
	}

	private boolean validateSHA(String data,String signture){
		try {
			MessageDigest md=MessageDigest.getInstance("SHA-1");
			md.update(data.getBytes());
			return StringUtils.equals(signture, Hex.encodeHexString(md.digest()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 验证sha1签名，验证通过返回true，否则返回false
	 * @param signature
	 * @param nonce
	 * @param timestamp
	 * @return
	 */
	private boolean validateSHA(String signature, String nonce,String timestamp) {
		if (signature == null || nonce == null || timestamp == null) {
			return false;
		}
//		String sign = sha1(getSignContent(nonce, timestamp, appSecret));
//		if (!signature.equals(sign)) {
//			return false;
//		}
//		return true;
		return validateSHA(getSignContent(nonce, timestamp, appSecret),signature);
		
	}
	
	/**
	 * 对非空参数按字典顺序升序构造签名串
	 * 
	 * @param params
	 * @return
	 */
	private static String getSignContent(String... params) {
		List<String> list = new ArrayList<String>(params.length);
		for(String temp : params){
			if(StringUtils.isNotBlank(temp)){
				list.add(temp);
			}	
		}
		Collections.sort(list);
		StringBuilder strBuilder = new StringBuilder();
		for (String element : list) {
			strBuilder.append(element);
		}
		return strBuilder.toString();
	}
	
	/**
	 * 生产sha1签名
	 * @param strSrc
	 * @return
	 */	
	private static String sha1(String strSrc) {
		MessageDigest md = null;
		String strDes = null;

		byte[] bt = strSrc.getBytes();
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(bt);
			strDes = bytes2Hex(md.digest()); // to HexString
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return strDes;
	}
	
	private static String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;

		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));

			if (tmp.length() == 1) {
				des += "0";
			}

			des += tmp;
		}

		return des;
	}
	
	/**
	 * 生成回复的消息。（发送被动响应消息）
	 * @param data  消息的内容。
	 * @param type  消息的类型
	 * @param senderId 回复消息的发送方uid。蓝v用户自己
	 * @param receiverId 回复消息的接收方  蓝v用户的粉丝uid
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	private String generateReplyMsg(String data, String type, String senderId, String receiverId) {
		JSONObject jo = new JSONObject();
		jo.put("result",true);
		jo.put("sender_id", senderId);
		jo.put("receiver_id", receiverId);
		jo.put("type", type);
		try {
			jo.put("data", URLEncoder.encode(data, "utf-8")); //data字段的内容需要进行utf8的urlencode
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jo.toString();
	}
	
	/**
	 * 生成文本类型的消息data字段
	 * @return
	 */
	private static String textMsg(){
		JSONObject jo = new JSONObject();
		jo.put("text", "中文消息");
		return jo.toString();
	}
	
	/**
	 * 生成文本类型的消息data字段
	 * @return
	 */
	private static String articleMsg(){
		JSONObject jo = new JSONObject();
		JSONArray ja = new JSONArray();
		for(int i = 0; i< 1; i ++){
			JSONObject temp = new JSONObject();
			temp.put("display_name", "两个故事");
			temp.put("summary", "今天讲两个故事，分享给你。谁是公司？谁又是中国人？​");
			temp.put("image", "http://storage.mcp.weibo.cn/0JlIv.jpg");
			temp.put("url", "http://e.weibo.com/mediaprofile/article/detail?uid=1722052204&aid=983319");
			ja.add(temp);
		}
		jo.put("articles", ja);
		return jo.toString();
	}
	
	/**
	 * 生成文本类型的消息data字段
	 * @return
	 */
	private static String positionMsg(){
		JSONObject jo = new JSONObject();
		jo.put("longitude", "344.3344");
		jo.put("latitude", "232.343434");
		return jo.toString();
	}
	
}
