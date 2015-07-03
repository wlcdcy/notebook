package com.example.resources;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import javax.ws.rs.HEAD;
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
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jivesoftware.smack.util.MD5;
import org.jsoup.nodes.Document;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TrelloApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.commons.OscUtils;
import com.example.commons.TrelloUtils;
import com.example.util.WeiboProvide;
import com.helger.html.hc.html.HCBody;
import com.helger.html.hc.html.HCHead;
import com.helger.html.hc.html.HCHtml;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Path("/webhook")
public class HookResource {
	Logger logger = LoggerFactory.getLogger(HookResource.class);

	// 微博全局变量声明
	// String appKey="1121941913";
	String weibo_access_token = "0ebc90cad97041ac57615c0af924f729";
	String weibo_app_secret = "2b3626dc0a956bc98e5b05afd1dbb608";

	public static String trello_access_token = "";

	// 监控宝全局变量声明

	/**
	 * 推送的一个msgid的集合。防止重复接收。
	 */
	public static final List<String> jkbao_msgIds = new ArrayList<String>();
	/**
	 * 监控宝生成的，需要在hiwork配置时，设置这个token,做数据校验用，确保请求来自于监控宝 （类似签名的效果）
	 */
	public static String jkbao_token = "efc4f368e17fceb424074e52672e544d";

	// 金数据全局变量声明
	/**
	 * 推送表单的serial_number的集合。防止重复接收。
	 */
	public static final List<String> jsj_serials = new ArrayList<String>();

	/**
	 * 测试服务状态服务
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getIt() {
		return "Hello, Let go! use ResourceConfig Scanning  Hooks";
	}

	/**
	 * github webhook回调服务：request data use form format
	 * 
	 * @param req
	 * @param formData
	 */
	@Path("/github")
	@POST
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void github_form(@Context HttpServletRequest req, @FormParam("Payload") String formData) {
		String eventName = req.getHeader("X-Github-Event");
		String signature = req.getHeader("X-Hub-Signature");
		String deliverId = req.getHeader("X-Github-Delivery");
		logger.debug(String.format("[event:%s] [signature:%s] [deliverId:%s]", eventName, signature, deliverId));
		logger.debug(formData);
	}

	/**
	 * github webhook回调服务：request data use json format
	 * 
	 * @param req
	 * @param token
	 * @param jsonData
	 * @return
	 */
	@Path("/github/{token}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Map<String, Object> github_json(@Context HttpServletRequest req, @PathParam("token") String token,
			Map<String, Object> jsonData) {
		String eventName = req.getHeader("X-Github-Event");
		String signature = req.getHeader("X-Hub-Signature");
		String deliverId = req.getHeader("X-Github-Delivery");
		logger.debug(String.format("[event:%s] [signature:%s] [deliverId:%s]", eventName, signature, deliverId));
		logger.debug(jsonData.toString());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", 0);
		return result;
	}

	/**
	 * coding webhook回调服务
	 * 
	 * @param req
	 * @param jsonData
	 * @return
	 */
	@Path("/coding")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Map<String, Object> zcode_json(@Context HttpServletRequest req, Map<String, Object> jsonData) {
		logger.debug(jsonData.toString());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", 0);
		return result;
	}

	/**
	 * gitlab webhook回调服务
	 * 
	 * @param req
	 * @param jsonData
	 */
	@Path("/gitlab")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void gitlab_json(@Context HttpServletRequest req, Map<String, Object> jsonData) {
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

	/**
	 * git@osc webhook回调服务
	 * 
	 * @param req
	 * @param data
	 */
	@Path("/gitosc")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String gitosc_json(@Context HttpServletRequest req, @FormParam("hook") String data) {
		logger.debug(data);
		return "";
	}

	/**
	 * 
	 * @param msg_id
	 *            告警消息ID
	 * @param task_id
	 *            监控项目ID
	 * @param task_type
	 *            监控项目类型，参考 监控项目
	 * @param fault_time
	 *            故障发生时间(unix时间戳)
	 * @param task_status
	 *            监控任务状态， 1 为不可用，0 为恢复可用
	 * @param task_summary
	 *            监控项目摘要
	 * @param content
	 *            告警消息内容,对内容进行了urlencode，需要urldecode得到内容
	 * @param token
	 *            使用msg_id、task_id、fault_time和您的回调token 这4个参数连接并MD5后的值，用来您对消息做校验
	 */
	@Path("/jkbao")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String jkbao_get(@QueryParam("msg_id") String msg_id, @QueryParam("task_id") String task_id,
			@QueryParam("task_type") String task_type, @QueryParam("fault_time") String fault_time,
			@QueryParam("task_status") String task_status, @QueryParam("task_summary") String task_summary,
			@QueryParam("content") String content, @QueryParam("token") String token) {

		// 检查msg_id是否已经接收过，接收过的可以忽略，不重复接收
		if (jkbao_msgIds.contains(msg_id)) {
			return "";
		}

		if (StringUtils.endsWith(token, MD5.hex(String.format("%s%s%s%s", msg_id, task_id, fault_time, jkbao_token)))) {
			try {
				String msg = URLDecoder.decode(content, "UTF-8");
				logger.info(msg);
				// TODO 模板渲染，推送到指定频道。
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			jkbao_msgIds.add(msg_id);
		}

		return "";
	}

	@Path("/jkbao")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void jkbao_post(@FormDataParam("msg_id") List<FormDataBodyPart> msg_idObjs,
			@FormDataParam("task_id") List<FormDataBodyPart> task_idObjs,
			@FormDataParam("task_type") List<FormDataBodyPart> task_typeObjs,
			@FormDataParam("fault_time") List<FormDataBodyPart> fault_timeObjs,
			@FormDataParam("message_type") List<FormDataBodyPart> message_typeObjs,
			@FormDataParam("message_status") List<FormDataBodyPart> message_statusObjs,
			@FormDataParam("task_summary") List<FormDataBodyPart> task_summaryObjs,
			@FormDataParam("content") List<FormDataBodyPart> contentObjs,
			@FormDataParam("token") List<FormDataBodyPart> tokenObjs,
			@FormDataParam("message_detail") List<FormDataBodyPart> message_detailObjs) {

		String msg_id = jkbao_parseFormDataBodyParts(msg_idObjs);
		String task_id = jkbao_parseFormDataBodyParts(task_idObjs);
		String task_type = jkbao_parseFormDataBodyParts(task_typeObjs);
		String fault_time = jkbao_parseFormDataBodyParts(fault_timeObjs);
		String message_type = jkbao_parseFormDataBodyParts(message_typeObjs);
		String message_status = jkbao_parseFormDataBodyParts(message_statusObjs);
		String task_summary = jkbao_parseFormDataBodyParts(task_summaryObjs);
		String content = jkbao_parseFormDataBodyParts(contentObjs);
		String token = jkbao_parseFormDataBodyParts(tokenObjs);
		String message_detail = jkbao_parseFormDataBodyParts(message_detailObjs);

		// 检查msg_id是否已经接收过，接收过的可以忽略，不重复接收
		if (jkbao_msgIds.contains(msg_id)) {
			return;
		}

		if (StringUtils.endsWith(token, MD5.hex(String.format("%s%s%s%s", msg_id, task_id, fault_time, jkbao_token)))) {
			try {
				String msg = URLDecoder.decode(content, "UTF-8");
				logger.info(msg);
				// TODO 模板渲染，推送到指定频道。
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			jkbao_msgIds.add(msg_id);
		}

		return;

	}

	private String jkbao_parseFormDataBodyParts(List<FormDataBodyPart> dataObjs) {
		if (dataObjs != null && !dataObjs.isEmpty()) {
			for (FormDataBodyPart dataObj : dataObjs) {
				String data = dataObj.getValueAs(String.class);
				logger.info(data);
				return data;
			}

		}
		return null;
	}

	@POST
	@Path("jsj")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String jsj(@Context HttpServletRequest req, Map<String, Object> jsonData) {
		// String api_key="tj_drkod2HhNtz69i7V40w";
		// String api_secret="uvnVDVbHlcMmtE6huIxy6Q";

		String content_type = req.getContentType();

		// {form=x8GO5U, entry={serial_number=1, field_1=大熊, field_7=一定能来,
		// field_2=13800000000, field_3=[日期1, 日期2, 日期3, 日期4], field_4=[菜类1],
		// field_6=带两个家属, creator_name=, created_at=2015-06-03T03:28:51Z,
		// updated_at=2015-06-03T03:28:51Z, info_remote_ip=1.80.205.127}}

		String form = (String) jsonData.get("form");
		String serial_number = String.valueOf(((Map) jsonData.get("entry")).get("serial_number"));

		logger.info(form);

		if (!jsj_serials.contains(serial_number)) {
			logger.info((String) ((Map) jsonData.get("entry")).get("DateTime"));
			// TODO generate link address and broadcast(通知有新数据，通过链接查看详情)
			String url = String.format("https://www.jinshuju.net/forms/%s/entries?utm_source=%s", form, "hiwork.cc");
			logger.info(url);

			jsj_serials.add(serial_number);
		}
		return content_type;
	}

	/**
	 * 创建webhook配置时检查url使用。此时的content-type is JSON
	 * 
	 * @param req
	 * @return
	 */
	@POST
	@Path("/fir")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String fir(@Context HttpServletRequest req, @FormParam("icon") String icon, @FormParam("msg") String msg,
			@FormParam("name") String name, @FormParam("changelog") String changelog,
			@FormParam("platform") String platform, @FormParam("release_type") String release_type,
			@FormParam("build") String build) {
		String content_type = req.getContentType();
		// TODO generate msg and broadcast

		return content_type;
	}

	/**
	 * 创建webhook配置时检查url使用。此时的content-type is JSON
	 * 
	 * @param req
	 * @return
	 */
	@POST
	@Path("/sendcloud")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String sendCloud(@Context HttpServletRequest req) {
		// String API_KEY="pzpnzMopG2SSCSOc";
		String content_type = req.getContentType();
		logger.info(content_type);
		return content_type;
	}

	/**
	 * 事件通知时使用。此时的content-type is FORM
	 * 
	 * @param req
	 * @param event
	 * @param message
	 * @param mail_list_task_id
	 * @param messageId
	 * @param category
	 * @param recipientArray
	 * @param emailIds
	 * @param labelId
	 * @param recipientSize
	 * @param timestamp
	 * @param token
	 * @param signature
	 * @return
	 */
	@POST
	@Path("/sendcloud")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String sendCloud(@Context HttpServletRequest req, @FormParam("event") String event,
			@FormParam("message") String message, @FormParam("mail_list_task_id") long mail_list_task_id,
			@FormParam("messageId") String messageId, @FormParam("category") String category,
			@FormParam("recipientArray") List<String> recipientArray, @FormParam("emailIds") List<String> emailIds,
			@FormParam("labelId") int labelId, @FormParam("recipientSize") int recipientSize,
			@FormParam("timestamp") long timestamp, @FormParam("token") String token,
			@FormParam("signature") String signature) {

		String content_type = req.getContentType();
		logger.info(content_type);
		// TODO generate msg and broadcast

		return content_type;
	}

	@POST
	@Path("/bitbucket/post")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void bitbucket_post(@Context HttpServletRequest req, @FormParam("payload") String payload) {
		logger.info(payload);
		// TODO generate msg use jsonData and broadcast
	}

	@POST
	@Path("/bitbucket/pull")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void bitbucket_pull(@Context HttpServletRequest req, Map<String, Object> jsonData) {
		logger.info(jsonData.toString());
		// TODO generate msg use jsonData and broadcast
	}

	@POST
	@Path("/gitcafe")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void gitcafe(@Context HttpServletRequest req, Map<String, Object> jsonData) {
		logger.info(jsonData.toString());
		// TODO generate msg use jsonData and broadcast
	}

	@POST
	@Path("/circleci/{token}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String circleci(@Context HttpServletRequest req, @PathParam("token") String token, Map<String, Object> jsonData) {
		String content_type = req.getContentType();
		logger.info(content_type);
		// TODO generate msg use jsonData and broadcast

		 return content_type;
	}

	@POST
	@Path("/bugsnag")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String bugsnag(@Context HttpServletRequest req, Map<String, Object> jsonData) {
		String content_type = req.getContentType();
		logger.info(content_type);
		// [help] https://bugsnag.com/docs/notifier-api#json-payload

		// TODO generate msg use jsonData and broadcast
		return content_type == null ? "hello" : content_type;
	}

	@POST
	@Path("/jira")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String jira(@Context HttpServletRequest req, Map<String, Object> jsonData) {
		String content_type = req.getContentType();
		logger.info(content_type);
		// [help]
		// https://developer.atlassian.com/jiradev/jira-architecture/webhooks

		// TODO generate msg use jsonData and broadcast
		return content_type == null ? "hello" : content_type;
	}

	@POST
	@Path("/teambition")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String teambition(@Context HttpServletRequest req, Map<String, Object> jsonData) {
		String content_type = req.getContentType();
		logger.info(content_type);
		// [help] https://docs.teambition.com/wiki/webhooks#webhooks-readme

		// TODO generate msg use jsonData and broadcast
		return content_type == null ? "hello" : content_type;
	}

	@POST
	@Path("/kf5/{token}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String kf5(@Context HttpServletRequest req, @PathParam("token") String token,
			@FormDataParam("payload") String payload) {
		String content_type = req.getContentType();
		logger.info(content_type);

		// TODO generate msg use jsonData and broadcast
		return content_type == null ? "hello" : content_type;
	}

	@POST
	@Path("/zendesk/{token}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String zendesk(@Context HttpServletRequest req, @PathParam("token") String token,
			@FormParam("source") String source, @FormParam("id") String ticket_id,
			@FormParam("status") String ticket_status, @FormParam("payload") String payload) {
		String content_type = req.getContentType();
		logger.info(content_type);

		// TODO generate msg use jsonData and broadcast
		return content_type == null ? "hello" : content_type;
	}

	@POST
	@Path("/vsonline/{token}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String vsOnline(@Context HttpServletRequest req, @PathParam("token") String token,
			Map<String, Object> jsonData) {
		String content_type = req.getContentType();
		logger.info(content_type);
		// [help] https://www.visualstudio.com/get-started/webhooks-and-vso-vs

		// TODO generate msg use jsonData and broadcast
		return content_type == null ? "hello" : content_type;
	}

	@POST
	@Path("/buildkite/{token}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String buildkite(@Context HttpServletRequest req, @PathParam("token") String token,
			Map<String, Object> jsonData) {
		String content_type = req.getContentType();
		logger.info(content_type);
		// [help] https://buildkite.com/docs/webhooks

		// TODO generate msg use jsonData and broadcast
		return content_type == null ? "hello" : content_type;
	}

	@POST
	@Path("/gogs/{token}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String gogs(@Context HttpServletRequest req, @PathParam("token") String token,
			Map<String, Object> jsonData) {
		String content_type = req.getContentType();
		logger.info(content_type);
		// [help]

		// TODO generate msg use jsonData and broadcast
		return content_type == null ? "hello" : content_type;
	}

	@POST
	@Path("/codeship/{token}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String codeship(@Context HttpServletRequest req, @PathParam("token") String token,
			Map<String, Object> jsonData) {
		String content_type = req.getContentType();
		logger.info(content_type);
		// [help] https://codeship.com/documentation/integrations/webhooks/

		// TODO generate msg use jsonData and broadcast
		return content_type == null ? "hello" : content_type;
	}
	
	@POST
	@Path("/travis/{token}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String travis(@Context HttpServletRequest req, @PathParam("token") String token,
			Map<String, Object> jsonData) {
		String content_type = req.getContentType();
		logger.info(content_type);
		// [help] https://codeship.com/documentation/integrations/webhooks/

		// TODO generate msg use jsonData and broadcast
		return content_type == null ? "hello" : content_type;
	}

	@POST
	@Path("/getsentry")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getsentry(@Context HttpServletRequest req, Map<String, Object> jsonData) {
		logger.info(jsonData.toString());
		// [help] https://github.com/getsentry/sentry-webhooks

		// TODO generate msg use jsonData and broadcast
	}

	@POST
	@Path("/relic")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void newrelic(@Context HttpServletRequest req, @FormParam("alert") String alert,
			@FormParam("deployment") String deployment) {
		logger.info(alert);
		logger.info(deployment);
		// 警告信息 和 部署发布通知两类信息

		// [set] servers -> INTEGRATIONS |alerting notifications | webhook
		// [help]
		// https://rpm.newrelic.com/accounts/1000941/integrations?page=alerting#tab-integrations=_webhook

		// TODO generate msg use jsonData and broadcast

	}

	@POST
	@Path("/worktile")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void worktile(@Context HttpServletRequest req, Map<String, Object> jsonData) {

		// **********以任务评论为例 列举数据模型*************************
		// 1:action
		// 2:data
		// 2.1:tid
		// 2.2:name
		// 2.3:entry_id
		// 2.4:entry_name
		// 2.5:create_date
		// 2.6:comment
		// 2.6.1:cid
		// 2.6.2:message
		// 2.6.3:create_date
		// 2.6.4:create_by
		// 2.6.4.1:uid
		// 2.6.4.2:name
		// 2.6.4.2:display_name
		// 2.7:project
		// 2.7.1:pid
		// 2.7.2:name

		// ********************************************
		logger.info(jsonData.toString());
		// TODO generate msg use jsonData and broadcast

	}

	@POST
	@Path("/tower")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void tower(@Context HttpServletRequest req, Map<String, Object> jsonData) {
		logger.info(jsonData.toString());
		// **********以讨论的评论为例 列举数据模型*************************
		// 1:action
		// 2:data
		// 2.1:project
		// 2.1.1:guid
		// 2.1.2:name
		// 2.2:topic
		// 2.2.1:guid
		// 2.2.2:title
		// 2.2.3:updated_at
		// 2.2.4:handler
		// 2.2.4.1:guid
		// 2.2.4.2:nickname
		// 2.3:comment
		// 2.3.1:guid
		// 2.3.2:content
		// ********************************************

		// TODO generate msg use jsonData and broadcast

	}

	/**
	 * 重定向到认证授权地址
	 * 
	 * @param resp
	 */
	@GET
	@Path("/trello/auth")
	public void trelloOauth(@Context HttpServletResponse resp) {
		try {
			resp.sendRedirect(TrelloUtils.getOauthUrl());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 授权回调服务。接收trello授权的认证token，以及验证信息。
	 * 
	 * @param req
	 * @param oauth_token
	 * @param oauth_verifier
	 * @return
	 */
	@GET
	@Path("/trello/auth/callback")
	@Produces(MediaType.APPLICATION_JSON)
	public String trelloOauthCallBack(@Context HttpServletRequest req, @QueryParam("oauth_token") String oauth_token,
			@QueryParam("oauth_verifier") String oauth_verifier) {
		logger.info("oauth_token : " + oauth_token);
		logger.info("oauth_verifier: " + oauth_verifier);
		String access_token = TrelloUtils.getAccessToken(oauth_token, oauth_verifier);
		trello_access_token = access_token;
		logger.info("access_token: " + access_token);
		return "is ok!";
	}

	/**
	 * board事件通知接收服务
	 * 
	 * @param req
	 * @param json_obj
	 * @return
	 */
	@POST
	@Path("/trello/board/callback")
	@Produces(MediaType.APPLICATION_JSON)
	public String trelloBoardCallBackPost(@Context HttpServletRequest req, Map<String, Object> json_obj) {
		String content_type = req.getContentType();
		try {
			logger.info("push data : " + new ObjectMapper().writeValueAsString(json_obj));
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content_type;
	}

	/**
	 * 创建board事件通知接收服务时的验证服务。
	 * 
	 * @param req
	 * @param resp
	 * @return
	 */
	@HEAD
	@Path("/trello/board/callback")
	@Produces(MediaType.APPLICATION_JSON)
	public String trelloBoardCallBackHead(@Context HttpServletRequest req, @Context HttpServletResponse resp) {
		return "is ok";
	}

	/**
	 * 微博粉丝互动服务
	 * 
	 * @param req
	 * @param text
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/weibo")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String weibo_json(@Context HttpServletRequest req, String text) throws IOException {
		String timestamp = req.getParameter("timestamp");
		String signature = req.getParameter("signature");
		String nonce = req.getParameter("nonce");
		String echostr = req.getParameter("echostr");
		logger.debug(String.format("[signature:%s] [timestamp:%s] [nonce:%s] [echostr:%s]", signature, timestamp, nonce,
				echostr));
		System.out.println(String.format("[signature:%s] [timestamp:%s] [nonce:%s] [echostr:%s]", signature, timestamp,
				nonce, echostr));
		if (validateSHA(signature, nonce, timestamp)) {
			if (org.apache.commons.lang.StringUtils.isNotBlank(echostr)) {
				return echostr;
			} else {
				// 正常推送消息时不会存在echoStr参数。
				// 接收post过来的消息数据
				// StringBuilder sb = new StringBuilder();
				// BufferedReader in = req.getReader();
				// String line;
				// while ((line = in.readLine()) != null) {
				// sb.append(line);
				// }

				// TODO 根据业务对消息进行处理。处理完成可以返回空串，也可以返回回复消息。
				System.out.println("received message : " + text);
				JSONObject jsonObj = JSONObject.fromObject(text);
				String msg = (String) jsonObj.get("text");
				String type = (String) jsonObj.get("type");

				String senderId = String.valueOf(jsonObj.get("sender_id"));// 回复消息的发送方id。即蓝v自己的uid
				String receiverId = String.valueOf(jsonObj.get("receiver_id")); // 回复消息的接收方id。蓝v粉丝的uid。这个字段需要在接收的推送消息中获取。
				String created_at = (String) jsonObj.get("created_at");

				// 需要回复消息时，修改returnContent为对应消息内容
				// String returnContent = generateReplyMsg(textMsg(), "text",
				// senderId, receiverId);//回复text类型消息
				// returnContent = generateReplyMsg(articleMsg(), "articles",
				// senderId, receiverId); //回复article类型消息
				// returnContent = generateReplyMsg(positionMsg(), "position",
				// senderId, receiverId);//回复position类型的消息
				// System.out.println("returnContent : " + text);
				return "";
			}
		} else {
			return "sign error!";
		}

	}

	/**
	 * 微博粉丝服务
	 * 
	 * @param req
	 * @return
	 * @throws IOException
	 */
	@GET
	@Path("/weibo")
	@Produces(MediaType.APPLICATION_JSON)
	public String weibo_get(@Context HttpServletRequest req) throws IOException {
		String timestamp = req.getParameter("timestamp");
		String signature = req.getParameter("signature");
		String nonce = req.getParameter("nonce");
		String echostr = req.getParameter("echostr");
		logger.debug(String.format("[signature:%s] [timestamp:%s] [nonce:%s] [echostr:%s]", signature, timestamp, nonce,
				echostr));

		if (validateSHA(signature, nonce, timestamp)) {
			if (org.apache.commons.lang.StringUtils.isNotBlank(echostr)) {
				return echostr;
			}
			return "request data error";
		} else {
			return "sign error!";
		}

	}

	/**
	 * 微博授权回调服务
	 * 
	 * @param auth_token
	 */
	@GET
	@Path("/weibo/auth")
	public void weibo_auth(@QueryParam("code") String auth_token) {
		System.out.println(auth_token);
		try {
			String access_token = WeiboProvide.getAccessToken(auth_token);
			WeiboProvide.friendsTimeLine(access_token, 0);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 微博授权页面
	 * 
	 * @param resp
	 * @throws IOException
	 */
	@GET
	@Path("/weibo/index.html")
	public void index(@Context HttpServletResponse resp) throws IOException {
		StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
		Configuration cfg = Configuration.defaultConfiguration();
		GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
		Template tmpl = gt.getTemplate("");
		tmpl.binding("", "");
		String htm = tmpl.render();
		resp.getOutputStream().write(htm.getBytes());
	}

	/**
	 * 微博取消授权回调服务
	 * 
	 * @param access_token
	 */
	@GET
	@Path("/weibo/unauth")
	public void weibo_unauth(@QueryParam("code") String access_token) {
		System.out.println(access_token);
	}

	private boolean validateSHA(String data, String signture) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(data.getBytes());
			return StringUtils.equals(signture, Hex.encodeHexString(md.digest()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 验证sha1签名，验证通过返回true，否则返回false
	 * 
	 * @param signature
	 * @param nonce
	 * @param timestamp
	 * @return
	 */
	private boolean validateSHA(String signature, String nonce, String timestamp) {
		if (signature == null || nonce == null || timestamp == null) {
			return false;
		}
		// String sign = sha1(getSignContent(nonce, timestamp, appSecret));
		// if (!signature.equals(sign)) {
		// return false;
		// }
		// return true;
		return validateSHA(getSignContent(nonce, timestamp, weibo_app_secret), signature);

	}

	/**
	 * 对非空参数按字典顺序升序构造签名串
	 * 
	 * @param params
	 * @return
	 */
	private static String getSignContent(String... params) {
		List<String> list = new ArrayList<String>(params.length);
		for (String temp : params) {
			if (StringUtils.isNotBlank(temp)) {
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
	 * 
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
	 * 
	 * @param data
	 *            消息的内容。
	 * @param type
	 *            消息的类型
	 * @param senderId
	 *            回复消息的发送方uid。蓝v用户自己
	 * @param receiverId
	 *            回复消息的接收方 蓝v用户的粉丝uid
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	private String generateReplyMsg(String data, String type, String senderId, String receiverId) {
		JSONObject jo = new JSONObject();
		jo.put("result", true);
		jo.put("sender_id", senderId);
		jo.put("receiver_id", receiverId);
		jo.put("type", type);
		try {
			jo.put("data", URLEncoder.encode(data, "utf-8")); // data字段的内容需要进行utf8的urlencode
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jo.toString();
	}

	/**
	 * 生成文本类型的消息data字段
	 * 
	 * @return
	 */
	private static String textMsg() {
		JSONObject jo = new JSONObject();
		jo.put("text", "中文消息");
		return jo.toString();
	}

	/**
	 * 生成文本类型的消息data字段
	 * 
	 * @return
	 */
	private static String articleMsg() {
		JSONObject jo = new JSONObject();
		JSONArray ja = new JSONArray();
		for (int i = 0; i < 1; i++) {
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
	 * 
	 * @return
	 */
	private static String positionMsg() {
		JSONObject jo = new JSONObject();
		jo.put("longitude", "344.3344");
		jo.put("latitude", "232.343434");
		return jo.toString();
	}

}
