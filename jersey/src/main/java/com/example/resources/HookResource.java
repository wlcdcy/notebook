package com.example.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
import javax.ws.rs.core.Response;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jivesoftware.smack.util.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.commons.CommonUtils;
import com.example.commons.NETUtils;
import com.example.commons.TrelloUtils;
import com.example.util.WeiboProvide;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Path("/webhook")
public class HookResource {
    private static final Logger logger = LoggerFactory.getLogger(HookResource.class);

    private static final String HELLOVALUE = "hello";
    private static final String CHARSETENCODE = "utf-8";

    // 微博全局变量声明
    private static final String WEIBODEBUGFORMAT = "[signature:%s] [timestamp:%s] [nonce:%s] [echostr:%s]";
    String weiboAccessToken = "0ebc90cad97041ac57615c0af924f729";
    String weiboAppSecret = "2b3626dc0a956bc98e5b05afd1dbb608";
    static String[] persons = { "佘明强", "张宏", "彭祥波", "刘刚", "刘剑", "李豆", "刘国艳", "符润祯", "梁培杰", "李工", "张青", "李总", "老王",
            "沈京华", "陈静", "焦明明" };
    static String[] personUrls = { "http://www.kaixin001.com/home/58140650.html", "张宏",
            "http://cn.linkedin.com/pub/%E7%A5%A5%E6%B3%A2-%E5%BD%AD/9a/709/217", "刘刚", "刘剑", "李豆", "刘国艳", "符润祯", "梁培杰",
            "李工", "张青", "李总", "老王", "沈京华", "陈静", "焦明明" };
    static String[] actors = { "土豪", "坏人", "懒人", "商人", "工人", "牛人", "超人", "乞丐", "好人", "神人" };
    static String[] works = { "脸很大", "腰很粗", "脸挺小", "腰蛮细", "嘴很甜", "脖子粗", "眼睛小", "胳膊长", "腿挺短", "手挺快" };
    
    public static final String TURING_URL="http://www.tuling123.com/openapi/api";
    static Map<String,String> robots;
    static {
        robots = new HashMap<String , String>();
        robots.put("xiaoai", "f3d7228474114e99aecc6c05fd03c176");
        robots.put("tuling", "c232f980ef2b261b6934506d67e8f0a8");
        };
    static Random random = new Random();
    ObjectMapper mapper = new ObjectMapper();

    // 监控宝全局变量声明

    /**
     * 推送的一个msgid的集合。防止重复接收。
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static final List<String> jkbaoMsgIds = new ArrayList();
    /**
     * 监控宝生成的，需要在hiwork配置时，设置这个token,做数据校验用，确保请求来自于监控宝 （类似签名的效果）
     */
    private static String jkbaoToken = "efc4f368e17fceb424074e52672e544d";

    // 金数据全局变量声明
    /**
     * 推送表单的serial_number的集合。防止重复接收。
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static final List<String> JSJSERIAL = new ArrayList();

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

    @Path("/test")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String testForm(@Context HttpServletRequest req, @FormParam("post_content") String postContent,
            @FormParam("post_content_filtered") String postContentFiltered, @FormParam("post_title") String postTitle,
            @FormParam("post_url") String postUrl,

            @FormParam("comment_post") String commentPost, @FormParam("comment_author") String commentAuthor,
            @FormParam("comment_content") String commentContent,
            @FormParam("comment_author_url") String commentAuthorUrl) {
        // post_content, post_content_filtered, post_title, post_url
        // 彭祥波(10887272) 11:40:33
        // comment_post ： comment_author，comment_content和comment_author_url

        String contentType = req.getContentType();
        if (logger.isDebugEnabled()) {
            logger.debug(postContent);
            logger.debug(postContentFiltered);
            logger.debug(postTitle);
            logger.debug(postUrl);
            logger.debug(commentPost);
            logger.debug(commentAuthor);
            logger.debug(commentContent);
            logger.debug(commentAuthorUrl);

            logger.debug(contentType);
        }

        return contentType;
    }

    /**
     * github webhook回调服务：request data use json format
     * 
     * @param req
     * @param token
     * @param jsonData
     * @return
     */
    @Path("/test")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String testJson(@Context HttpServletRequest req, @PathParam("token") String token,
            Map<String, Object> jsonData) {
        String contentType = req.getContentType();
        if (logger.isDebugEnabled()) {
            logger.debug(token);
            logger.debug(contentType);
        }

        String jsonStr = null;
        try {
            jsonStr = mapper.writeValueAsString(jsonData);
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }
        logger.debug(jsonStr);
        return contentType;
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
    public void githubForm(@Context HttpServletRequest req, @FormParam("Payload") String formData) {
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
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Path("/github/{token}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Map<String, Object> githubJson(@Context HttpServletRequest req, @PathParam("token") String token,
            Map<String, Object> jsonData) {
        String eventName = req.getHeader("X-Github-Event");
        String signature = req.getHeader("X-Hub-Signature");
        String deliverId = req.getHeader("X-Github-Delivery");
        if (logger.isDebugEnabled()) {
            logger.debug(token);
            logger.debug(String.format("[event:%s] [signature:%s] [deliverId:%s]", eventName, signature, deliverId));
            logger.debug(jsonData.toString());
        }
        Map<String, Object> result = new HashMap();
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
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Path("/coding")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Map<String, Object> codeJson(Map<String, Object> jsonData) {
        logger.debug(jsonData.toString());
        Map<String, Object> result = new HashMap();
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
    public void gitlabJson(Map<String, Object> jsonData) {
        ObjectMapper omap = new ObjectMapper();
        try {
            logger.debug(omap.writeValueAsString(jsonData));
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
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
    public String gitoscJson(@FormParam("hook") String data) {
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
    public String jkbaoGet(@QueryParam("msg_id") String msgId, @QueryParam("task_id") String taskId,
            @QueryParam("task_type") String taskType, @QueryParam("fault_time") String faultTime,
            @QueryParam("task_status") String taskStatus, @QueryParam("task_summary") String taskSummary,
            @QueryParam("content") String content, @QueryParam("token") String token) {

        // 检查msg_id是否已经接收过，接收过的可以忽略，不重复接收
        if (logger.isDebugEnabled()) {
            logger.debug(taskType);
            logger.debug(taskStatus);
            logger.debug(taskSummary);
        }

        if (jkbaoMsgIds.contains(msgId)) {
            return "";
        }

        if (StringUtils.endsWith(token, MD5.hex(String.format("%s%s%s%s", msgId, taskId, faultTime, jkbaoToken)))) {
            try {
                String msg = URLDecoder.decode(content, CHARSETENCODE);
                logger.debug(msg);
                // TODO 模板渲染，推送到指定频道。
            } catch (UnsupportedEncodingException e) {
                logger.warn(e.getMessage(), e);
            }
            jkbaoMsgIds.add(msgId);
        }

        return "";
    }

    @Path("/jkbao")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void jkbaoPost(@FormDataParam("msg_id") List<FormDataBodyPart> msgIdObjs,
            @FormDataParam("task_id") List<FormDataBodyPart> taskIdObjs,
            @FormDataParam("task_type") List<FormDataBodyPart> taskTypeObjs,
            @FormDataParam("fault_time") List<FormDataBodyPart> faultTimeObjs,
            @FormDataParam("message_type") List<FormDataBodyPart> messageTypeObjs,
            @FormDataParam("message_status") List<FormDataBodyPart> messageStatusObjs,
            @FormDataParam("task_summary") List<FormDataBodyPart> taskSummaryObjs,
            @FormDataParam("content") List<FormDataBodyPart> contentObjs,
            @FormDataParam("token") List<FormDataBodyPart> tokenObjs,
            @FormDataParam("message_detail") List<FormDataBodyPart> messageDetailObjs) {

        String msgId = jkbaoParseFormDataBodyParts(msgIdObjs);
        String taskId = jkbaoParseFormDataBodyParts(taskIdObjs);
        String taskType = jkbaoParseFormDataBodyParts(taskTypeObjs);
        String faultTime = jkbaoParseFormDataBodyParts(faultTimeObjs);
        String messageType = jkbaoParseFormDataBodyParts(messageTypeObjs);
        String messageStatus = jkbaoParseFormDataBodyParts(messageStatusObjs);
        String taskSummary = jkbaoParseFormDataBodyParts(taskSummaryObjs);
        String content = jkbaoParseFormDataBodyParts(contentObjs);
        String token = jkbaoParseFormDataBodyParts(tokenObjs);
        String messageDetail = jkbaoParseFormDataBodyParts(messageDetailObjs);

        if (logger.isDebugEnabled()) {
            logger.debug(taskType);
            logger.debug(messageType);
            logger.debug(messageStatus);
            logger.debug(taskSummary);
            logger.debug(messageDetail);
        }

        // 检查msg_id是否已经接收过，接收过的可以忽略，不重复接收
        if (jkbaoMsgIds.contains(msgId)) {
            return;
        }

        if (StringUtils.endsWith(token, MD5.hex(String.format("%s%s%s%s", msgId, taskId, faultTime, jkbaoToken)))) {
            try {
                String msg = URLDecoder.decode(content, CHARSETENCODE);
                logger.info(msg);
                // TODO 模板渲染，推送到指定频道。
            } catch (UnsupportedEncodingException e) {
                logger.warn(e.getMessage(), e);
            }
            jkbaoMsgIds.add(msgId);
        }

        return;

    }

    private String jkbaoParseFormDataBodyParts(List<FormDataBodyPart> dataObjs) {
        if (dataObjs != null && !dataObjs.isEmpty()) {
            for (FormDataBodyPart dataObj : dataObjs) {
                String data = dataObj.getValueAs(String.class);
                logger.info(data);
                return data;
            }
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    @POST
    @Path("jsj")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String jsj(@Context HttpServletRequest req, Map<String, Object> jsonData) {

        String apiKey = "tj_drkod2HhNtz69i7V40w";
        String apiSecret = "uvnVDVbHlcMmtE6huIxy6Q";
        if (logger.isDebugEnabled()) {
            logger.debug(apiKey);
            logger.debug(apiSecret);
        }
        String contentType = req.getContentType();
        String form = (String) jsonData.get("form");
        String serialNumber = String.valueOf(((Map) jsonData.get("entry")).get("serial_number"));
        logger.info(form);

        if (!JSJSERIAL.contains(serialNumber)) {
            logger.info((String) ((Map) jsonData.get("entry")).get("DateTime"));
            // TODO generate link address and broadcast(通知有新数据，通过链接查看详情)
            String url = String.format("https://www.jinshuju.net/forms/%s/entries?utm_source=%s", form, "hiwork.cc");
            logger.info(url);

            JSJSERIAL.add(serialNumber);
        }
        return contentType;
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
            @FormParam("platform") String platform, @FormParam("release_type") String releaseType,
            @FormParam("build") String build) {
        if (logger.isDebugEnabled()) {
            logger.debug(icon);
            logger.debug(msg);
            logger.debug(name);
            logger.debug(changelog);
            logger.debug(platform);
            logger.debug(releaseType);
            logger.debug(build);
        }
        // TODO generate msg and broadcast

        return req.getContentType();
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
        String contentType = req.getContentType();
        logger.info(contentType);
        return contentType;
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
            @FormParam("message") String message, @FormParam("mail_list_task_id") long mailListTaskId,
            @FormParam("messageId") String messageId, @FormParam("category") String category,
            @FormParam("recipientArray") List<String> recipientArray, @FormParam("emailIds") List<String> emailIds,
            @FormParam("labelId") int labelId, @FormParam("recipientSize") int recipientSize,
            @FormParam("timestamp") long timestamp, @FormParam("token") String token,
            @FormParam("signature") String signature) {

        String contentType = req.getContentType();
        if (logger.isDebugEnabled()) {
            logger.info(event);
            logger.info(message);
            logger.info(String.valueOf(mailListTaskId));
            logger.info(messageId);
            logger.info(StringUtils.join(recipientArray, ","));
            logger.info(category);
            logger.info(StringUtils.join(emailIds, ","));
            logger.info(String.valueOf(labelId));
            logger.info(String.valueOf(recipientSize));
            logger.info(String.valueOf(timestamp));
            logger.info(token);
            logger.info(signature);

            logger.info(contentType);
        }

        // TODO generate msg and broadcast

        return contentType;
    }

    @POST
    @Path("/bitbucket/post")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void bitbucketPost(@FormParam("payload") String payload) {
        logger.info(payload);
        // TODO generate msg use jsonData and broadcast
    }

    @POST
    @Path("/bitbucket/pull")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void bitbucketPull(Map<String, Object> jsonData) {
        logger.info(jsonData.toString());
        // TODO generate msg use jsonData and broadcast
    }

    @POST
    @Path("/swathub/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String swathubJson(@Context HttpServletRequest req, @PathParam("token") String token,
            Map<String, Object> jsonData) {
        String contentType = req.getContentType();
        if (logger.isDebugEnabled()) {
            logger.debug(token);
            logger.debug(contentType);
        }
        String jsonStr = null;
        try {
            jsonStr = mapper.writeValueAsString(jsonData);
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }
        logger.info(jsonStr);
        // TODO generate msg use jsonData and broadcast
        return "is ok";
    }

    @POST
    @Path("/swathub/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String swathubForm(@Context HttpServletRequest req, @PathParam("token") String token, String payload) {
        String contentType = req.getContentType();
        if (logger.isDebugEnabled()) {
            logger.debug(token);
            logger.debug(payload);
            logger.debug(contentType);
        }
        // TODO generate msg use jsonData and broadcast
        return contentType;
    }

    @POST
    @Path("/gitcafe")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void gitcafe(Map<String, Object> jsonData) {
        logger.info(jsonData.toString());
        // TODO generate msg use jsonData and broadcast
    }

    @POST
    @Path("/circleci/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String circleci(@Context HttpServletRequest req, @PathParam("token") String token,
            Map<String, Object> jsonData) {
        String contentType = req.getContentType();
        if (logger.isDebugEnabled()) {
            logger.debug(token);
            logger.debug(CommonUtils.object2Json(jsonData));
            logger.debug(contentType);
        }
        // [help] :https://circleci.com/docs/configuration

        // TODO generate msg use jsonData and broadcast

        return contentType;
    }

    @POST
    @Path("/magnum/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String magnum(@Context HttpServletRequest req, @PathParam("token") String token,
            @FormParam("payload") String payload) {
        String contentType = req.getContentType();
        if (logger.isDebugEnabled()) {
            logger.debug(token);
            logger.debug(payload);
        }

        // [help] :https://circleci.com/docs/configuration

        // TODO generate msg use jsonData and broadcast

        return contentType;
    }

    @Path("/outgoing")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String outgoingForm(@FormParam("token") String token, @FormParam("team") String team,
            @FormParam("domain") String domain, @FormParam("channel") String channel,
            @FormParam("timestamp") long timestamp, @FormParam("user") String userName, @FormParam("text") String text,
            @FormParam("trigger_word") String triggerWord, @FormParam("Payload") String formData) {

        if (logger.isDebugEnabled()) {
            logger.debug(token);
            logger.debug(team);
            logger.debug(domain);
            logger.debug(channel);
            logger.debug(String.valueOf(timestamp));
            logger.debug(userName);
            logger.debug(formData);
        }
        if (StringUtils.contains(text, triggerWord)) {
            return text;
        }
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Path("/outgoing")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Map<String, Object> outgoingJson(Map<String, Object> jsonData) {
        Map<String, Object> returnData = new HashMap();
        logger.debug("channel : " + (String) jsonData.get("channel"));
        String text = (String) jsonData.get("content");
        String triggerWord = (String) jsonData.get("trigger_word");

        if (text.trim().length() > triggerWord.length() && StringUtils.contains(text.trim(), triggerWord)) {
            String returntext = text;
            int i = 0;
            for (String person : persons) {
                i++;
                if (text.contains(person)) {
                    returntext = String.format("%s是%s，他%s", person, actors[random.nextInt(actors.length)],
                            works[random.nextInt(works.length)]);
                    break;
                }

            }
            returnData.put("title", "outgoing[" + text + "]");
            returnData.put("text", returntext);
            if (i == 3) {
                returnData.put("url", personUrls[i - 1]);
            } else {
                returnData.put("url", "#");
            }
        }

        return returnData;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Path("/turing")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Map<String, Object> outgoingTuring(Map<String, Object> jsonData) {
        Map<String, Object> returnData = new HashMap();
        logger.debug("channel : " + (String) jsonData.get("channel"));
        String text = (String) jsonData.get("content");
        String triggerWord = (String) jsonData.get("trigger_word");

        if (text.trim().length() > triggerWord.length()) {
            String content = StringUtils.split(text)[1];
            HttpURLConnection connection = null;
            try {
                String apiKey = "c232f980ef2b261b6934506d67e8f0a8";
                String info = URLEncoder.encode(content, CHARSETENCODE);
                String getURL = "http://www.tuling123.com/openapi/api?key=" + apiKey + "&info=" + info;
                URL getUrl = new URL(getURL);
                connection = (HttpURLConnection) getUrl.openConnection();
                connection.connect();

                // 取得输入流，并使用Reader读取
                try (InputStreamReader inr = new InputStreamReader(connection.getInputStream(), CHARSETENCODE);
                        BufferedReader reader = new BufferedReader(inr)) {
                    String readText = readerToString(reader);
                    returnData.put("title", "turing[" + text + "]");
                    returnData.put("text", readText);
                    returnData.put("url", "#");
                    return returnData;
                }
            } catch (IOException e) {
                logger.warn(e.getMessage(), e);
            } finally {
                // 断开连接
                if (connection != null) {
                    try {
                        connection.disconnect();
                    } catch (Exception e) {
                        logger.warn(e.getMessage(), e);
                    }
                }
            }

        }
        return returnData;
    }
    
    @Path("/robot/{robotId}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String robot(@PathParam("robotId")String robotId,Map<String, Object> reqData) {
        String apiKey = robots.get(robotId);
        if(StringUtils.isEmpty(apiKey)){
            return null;
        }
        String content = (String) reqData.get("info");
        String userid = (String)reqData.get("userid");
        return robotGet(apiKey,content,userid);
    }
    
    @SuppressWarnings({ "unchecked", "unused" })
    private String robotPost(String apiKey,String content,String userid){
        logger.debug(String.format("[info : %s]  [userid : %s]", content,userid));
        if (StringUtils.isNotBlank(content) && StringUtils.isNotBlank(userid)){
            Map<String,String> reqData = new HashMap<>();
            reqData.put("key", apiKey);
            reqData.put("info", content);
            reqData.put("userid", userid);
            String reqString = CommonUtils.object2Json(reqData);
        
            String respJson = NETUtils.httpPostWithJson(TURING_URL, reqString);
            Map<String,String> respData= CommonUtils.jsonToObject(Map.class, respJson);
            String text = respData.get("text");
            logger.debug(text);
            return text;
        }
        return null;
    }
    
    private String robotGet(String apiKey,String content,String userid){
        HttpURLConnection connection = null;
        try {
            String info = URLEncoder.encode(content, CHARSETENCODE);
            String getURL =TURING_URL+"?key=" + apiKey + "&info=" + info +"&userid="+userid;
            URL getUrl = new URL(getURL);
            connection = (HttpURLConnection) getUrl.openConnection();
            connection.connect();
            // 取得输入流，并使用Reader读取
            try (InputStreamReader inr = new InputStreamReader(connection.getInputStream(), CHARSETENCODE);
                    BufferedReader reader = new BufferedReader(inr)) {
                String readText = readerToString(reader);
                return readText;
            }
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
        return null;
    }
    
    private String readerToString(BufferedReader reader){
        StringBuilder sb = new StringBuilder();
        String line = "";
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            logger.warn(e.getMessage(),e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(sb.toString());
        }
        return sb.toString();
    }

    @POST
    @Path("/bugsnag")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String bugsnag(@Context HttpServletRequest req, Map<String, Object> jsonData) {
        String contentType = req.getContentType();
        if (logger.isDebugEnabled()) {
            logger.debug(CommonUtils.object2Json(jsonData));
            logger.debug(contentType);
        }

        // [help] https://bugsnag.com/docs/notifier-api#json-payload

        // TODO generate msg use jsonData and broadcast
        return contentType == null ? HELLOVALUE : contentType;
    }

    @POST
    @Path("/jira")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String jira(@Context HttpServletRequest req, Map<String, Object> jsonData) {
        String contentType = req.getContentType();
        logger.info(contentType);
        try {
            String json = new ObjectMapper().writeValueAsString(jsonData);
            if (logger.isDebugEnabled()) {
                logger.debug(json);
            }
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }

        // [help]
        // https://developer.atlassian.com/jiradev/jira-architecture/webhooks

        // TODO generate msg use jsonData and broadcast
        return contentType == null ? HELLOVALUE : contentType;
    }

    @POST
    @Path("/teambition")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String teambition(@Context HttpServletRequest req, Map<String, Object> jsonData) {
        String contentType = req.getContentType();
        try {
            String json = new ObjectMapper().writeValueAsString(jsonData);
            if (logger.isDebugEnabled()) {
                logger.debug(json);
            }
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }
        // [help] https://docs.teambition.com/wiki/webhooks#webhooks-readme

        // TODO generate msg use jsonData and broadcast
        return contentType == null ? HELLOVALUE : contentType;
    }

    @POST
    @Path("/kf5/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String kf5(@Context HttpServletRequest req, @PathParam("token") String token,
            @FormDataParam("payload") String payload) {
        String contentType = req.getContentType();
        if (logger.isDebugEnabled()) {
            logger.debug(token);
            logger.debug(payload);

            logger.debug(contentType);
        }

        // TODO generate msg use jsonData and broadcast
        return contentType == null ? HELLOVALUE : contentType;
    }

    @POST
    @Path("/zendesk/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String zendesk(@Context HttpServletRequest req, @PathParam("token") String token,
            @FormParam("source") String source, @FormParam("id") String ticketId,
            @FormParam("status") String ticketStatus, @FormParam("payload") String payload) {
        String contentType = req.getContentType();
        if (logger.isDebugEnabled()) {
            logger.debug(token);
            logger.debug(source);
            logger.debug(ticketId);
            logger.debug(ticketStatus);
            logger.debug(payload);
            logger.debug(contentType);
        }

        // TODO generate msg use jsonData and broadcast
        return contentType == null ? HELLOVALUE : contentType;
    }

    @POST
    @Path("/qingyun/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public String qingyunPost(@Context HttpServletRequest req, @PathParam("token") String token) {
        String contentType = req.getContentType();
        if (logger.isDebugEnabled()) {
            logger.debug(token);
            logger.debug(contentType);
        }
        // TODO generate msg use jsonData and broadcast
        return "5a6026";
    }

    @GET
    @Path("/qingyun/{token}")
    public Response qingyunGet(@Context HttpServletRequest req, @PathParam("token") String token) {
        String contentType = req.getContentType();
        if (logger.isDebugEnabled()) {
            logger.debug(token);
            logger.debug(contentType);
        }
        return Response.ok("5a6026").build();
    }

    @POST
    @Path("/vsonline/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String vsOnline(@Context HttpServletRequest req, @PathParam("token") String token,
            Map<String, Object> jsonData) {
        String contentType = req.getContentType();
        if (logger.isDebugEnabled()) {
            logger.debug(token);
            logger.debug(CommonUtils.object2Json(jsonData));
            logger.debug(contentType);
        }
        // [help] https://www.visualstudio.com/get-started/webhooks-and-vso-vs

        // TODO generate msg use jsonData and broadcast
        return contentType == null ? HELLOVALUE : contentType;
    }

    @POST
    @Path("/buildkite/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String buildkite(@Context HttpServletRequest req, @PathParam("token") String token,
            Map<String, Object> jsonData) {
        String contentType = req.getContentType();
        if (logger.isDebugEnabled()) {
            logger.debug(token);
            logger.debug(CommonUtils.object2Json(jsonData));
            logger.debug(contentType);
        }
        // [help] https://buildkite.com/docs/webhooks

        // TODO generate msg use jsonData and broadcast
        return contentType == null ? HELLOVALUE : contentType;
    }

    @POST
    @Path("/gogs/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String gogs(@Context HttpServletRequest req, @PathParam("token") String token,
            Map<String, Object> jsonData) {
        String contentType = req.getContentType();
        if (logger.isDebugEnabled()) {
            logger.debug(token);
            logger.debug(CommonUtils.object2Json(jsonData));
            logger.debug(contentType);
        }
        // [help]

        // TODO generate msg use jsonData and broadcast
        return contentType == null ? HELLOVALUE : contentType;
    }

    @POST
    @Path("/codeship/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String codeship(@Context HttpServletRequest req, @PathParam("token") String token,
            Map<String, Object> jsonData) {
        String contentType = req.getContentType();
        if (logger.isDebugEnabled()) {
            logger.debug(token);
            logger.debug(CommonUtils.object2Json(jsonData));
            logger.debug(contentType);
        }

        // [help] https://codeship.com/documentation/integrations/webhooks/

        // TODO generate msg use jsonData and broadcast
        return contentType == null ? HELLOVALUE : contentType;
    }

    @POST
    @Path("/travis/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String travis(@Context HttpServletRequest req, @PathParam("token") String token,
            Map<String, Object> jsonData) {
        String contentType = req.getContentType();
        if (logger.isDebugEnabled()) {
            logger.debug(token);
            logger.debug(CommonUtils.object2Json(jsonData));
            logger.debug(contentType);
        }

        // [help] https://codeship.com/documentation/integrations/webhooks/

        // TODO generate msg use jsonData and broadcast
        return contentType == null ? HELLOVALUE : contentType;
    }

    @POST
    @Path("/runscope/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String runscope(@Context HttpServletRequest req, @PathParam("token") String token,
            Map<String, Object> jsonData) {
        String contentType = req.getContentType();
        if (logger.isDebugEnabled()) {
            logger.debug(token);
            logger.debug(CommonUtils.object2Json(jsonData));
            logger.debug(contentType);
        }

        // [help]
        // https://www.runscope.com/docs/api-testing/notifications#webhook

        // TODO generate msg use jsonData and broadcast
        return contentType == null ? HELLOVALUE : contentType;
    }

    @POST
    @Path("/getsentry")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void getsentry(Map<String, Object> jsonData) {
        logger.info(jsonData.toString());
        // [help] https://github.com/getsentry/sentry-webhooks

        // TODO generate msg use jsonData and broadcast
    }

    @POST
    @Path("/relic")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void newrelic(@FormParam("alert") String alert, @FormParam("deployment") String deployment) {
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
    public void worktile(Map<String, Object> jsonData) {

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
    public void tower(Map<String, Object> jsonData) {
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
            logger.warn(e.getMessage(), e);
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
    public String trelloOauthCallBack(@QueryParam("oauth_token") String oauthToken,
            @QueryParam("oauth_verifier") String oauthVerifier) {
        logger.info("oauth_token : " + oauthToken);
        logger.info("oauth_verifier: " + oauthVerifier);
        String accessToken = TrelloUtils.getAccessToken(oauthToken, oauthVerifier);
        logger.info("trello_access_token: " + accessToken);
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
    public String trelloBoardCallBackPost(@Context HttpServletRequest req, Map<String, Object> jsonObj) {
        String contentType = req.getContentType();
        try {
            logger.info("push data : " + new ObjectMapper().writeValueAsString(jsonObj));
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }
        return contentType;
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
        resp.getContentType();
        return req.getContentType();
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
    public String weiboJson(@Context HttpServletRequest req, String text) throws IOException {
        String timestamp = req.getParameter("timestamp");
        String signature = req.getParameter("signature");
        String nonce = req.getParameter("nonce");
        String echostr = req.getParameter("echostr");

        if (logger.isDebugEnabled()) {
            logger.debug(String.format(WEIBODEBUGFORMAT, signature, timestamp, nonce, echostr));
            logger.debug(String.format(WEIBODEBUGFORMAT, signature, timestamp, nonce, echostr));
            logger.debug("received message : " + text);
        }
        if (validateSHA(signature, nonce, timestamp)) {
            if (org.apache.commons.lang.StringUtils.isNotBlank(echostr)) {
                return echostr;
            } else {
                // 正常推送消息时不会存在echoStr参数。
                // 接收post过来的消息数据
                StringBuilder sb = new StringBuilder();
                BufferedReader in = req.getReader();
                String line;
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                // TODO 根据业务对消息进行处理。处理完成可以返回空串，也可以返回回复消息。

                JSONObject jsonObj = JSONObject.fromObject(text);
                String msg = (String) jsonObj.get("text");
                String type = (String) jsonObj.get("type");
                /* 回复消息的发送方id */
                String senderId = String.valueOf(jsonObj.get("sender_id"));
                /* 回复消息的接收方id。这个字段需要在接收的推送消息中获取。 */
                String receiverId = String.valueOf(jsonObj.get("receiver_id"));
                String createdAt = (String) jsonObj.get("created_at");

                /* 需要回复消息时，修改returnContent为对应消息内容;回复text类型消息 */
                String returnText = generateReplyMsg(textMsg(), "text", senderId, receiverId);
                /* 回复article类型消息 */
                String returnArticle = generateReplyMsg(articleMsg(), "articles", senderId, receiverId);
                /* 回复position类型的消息 */
                String returnPosition = generateReplyMsg(positionMsg(), "position", senderId, receiverId);

                if (logger.isDebugEnabled()) {
                    logger.debug("Text : " + returnText);
                    logger.debug("Article : " + returnArticle);
                    logger.debug("Position : " + returnPosition);

                    logger.debug(msg);
                    logger.debug(type);
                    logger.debug(senderId);
                    logger.debug(receiverId);
                    logger.debug(createdAt);
                }
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
    public String weiboGet(@Context HttpServletRequest req) throws IOException {
        String timestamp = req.getParameter("timestamp");
        String signature = req.getParameter("signature");
        String nonce = req.getParameter("nonce");
        String echostr = req.getParameter("echostr");
        logger.debug(String.format(WEIBODEBUGFORMAT, signature, timestamp, nonce, echostr));

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
    public void weiboAuth(@QueryParam("code") String authToken) {
        if (logger.isDebugEnabled()) {
            logger.debug(authToken);
        }
        try {
            String accessToken = WeiboProvide.getAccessToken(authToken);
            WeiboProvide.friendsTimeLine(accessToken, 0);
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
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
    public void weiboUnauth(@QueryParam("code") String accessToken) {
        if (logger.isDebugEnabled()) {
            logger.debug(accessToken);
        }
    }

    private boolean validateSHA(String data, String signture) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(data.getBytes());
            return StringUtils.equals(signture, Hex.encodeHexString(md.digest()));
        } catch (NoSuchAlgorithmException e) {
            logger.warn(e.getMessage(), e);
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
        return validateSHA(getSignContent(nonce, timestamp, weiboAppSecret), signature);

    }

    /**
     * 对非空参数按字典顺序升序构造签名串
     * 
     * @param params
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static String getSignContent(String... params) {
        List<String> list = new ArrayList(params.length);
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
    public static String sha1(String strSrc) {
        MessageDigest md = null;
        String strDes = null;

        byte[] bt = strSrc.getBytes();
        try {
            md = MessageDigest.getInstance("SHA-1");
            md.update(bt);
            strDes = bytes2Hex(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            logger.warn(e.getMessage(), e);
        }
        return strDes;
    }

    private static String bytes2Hex(byte[] bts) {
        String des = "";
        for (int i = 0; i < bts.length; i++) {
            String tmp = Integer.toHexString(bts[i] & 0xFF);
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
    public String generateReplyMsg(String data, String type, String senderId, String receiverId) {
        JSONObject jo = new JSONObject();
        jo.put("result", true);
        jo.put("sender_id", senderId);
        jo.put("receiver_id", receiverId);
        jo.put("type", type);
        try {
            jo.put("data", URLEncoder.encode(data, CHARSETENCODE)); // data字段的内容需要进行utf8的urlencode
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return jo.toString();
    }

    /**
     * 生成文本类型的消息data字段
     * 
     * @return
     */
    public static String textMsg() {
        JSONObject jo = new JSONObject();
        jo.put("text", "中文消息");
        return jo.toString();
    }

    /**
     * 生成文本类型的消息data字段
     * 
     * @return
     */
    public static String articleMsg() {
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
    public static String positionMsg() {
        JSONObject jo = new JSONObject();
        jo.put("longitude", "344.3344");
        jo.put("latitude", "232.343434");
        return jo.toString();
    }

}
