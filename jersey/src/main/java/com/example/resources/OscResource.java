package com.example.resources;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.commons.OscUtils;

@Path("/webhook/osc")
public class OscResource {
    private static Logger logger = LoggerFactory.getLogger(OscResource.class);
    ObjectMapper mapper = new ObjectMapper();
    OscUtils oscUtil = new OscUtils();

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response index(@Context HttpServletResponse resp) throws IOException {
        String html = "welcome to <a href='http://hiwork.cc'>hiwork</a>.";
        return Response.ok(html).build();
    }

    @Path("/redirect")
    @GET
    public Response redirect(@QueryParam("source") String source) throws IOException {
        String default_url = "http://oschina.net";
        URI location = null;
        if (source != null) {
            try {
                location = new URI(source);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        if (location == null) {
            try {
                location = new URI(default_url);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return Response.temporaryRedirect(location).build();
    }

    @Path("/auth")
    @GET
    public void oauth2Auth(@Context HttpServletResponse resp) {
        try {
            resp.sendRedirect(oscUtil.getOauth2AuthUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Path("/authback")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String auth_back(@QueryParam("code") String code, @QueryParam("state") String state) {
        OscUtils.authCode = code;
        logger.info(String.format("auth code is : %s", code));
        logger.info(String.format("res state is : %s", state));

        // fetch access token
        oscUtil.fetchOauth2Token(code);
        return "";
    }

    @Path("/search")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String search(String catalog, String words) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("access_token", OscUtils.accessToken);
        data.put("catalog", catalog);
        data.put("q", words);
        data.put("dataType", "json");
        String req_data = "";
        try {
            req_data = new ObjectMapper().writeValueAsString(data);
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }

        if (StringUtils.isEmpty(req_data)) {
            logger.error("req_data is empty : return");
            return "";
        }
        logger.info(String.format("req_data is : %s", req_data));

        return oscUtil.search(catalog, words);

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Path("/find")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Map<String, Object> outgoingFind(@Context HttpServletRequest req, Map<String, Object> jsonData) {
        Map<String, Object> returnData = new HashMap<String, Object>();
        logger.debug("channel : " + (String) jsonData.get("channel"));
        String text = (String) jsonData.get("content");
        String triggerWord = (String) jsonData.get("trigger_word");

        if (text.trim().length() > triggerWord.length()) {
            String content = StringUtils.split(text, " ", 2)[1];
            String catalog = StringUtils.split(content, " ", 2)[0];
            String words = StringUtils.split(content, " ", 2)[1];
            String respData = "";
            if (catalog.equals("tweet")) {
                Long useid = null;
                if ("最新".equals(words)) {
                    useid = 0L;
                } else if ("热门".equals(words)) {
                    useid = -1L;
                }
                if (useid == null) {
                    respData = "你只能查看【热门|最新】动弹，禁止查看作者自己的动弹";
                } else {
                    String resq_content = oscUtil.list_tweet(useid);
                    List<Map> contents = null;
                    try {
                        contents = (List<Map>) ((Map) mapper.readValue(resq_content, Map.class)).get("tweetlist");
                    } catch (IOException e) {
                        logger.warn(e.getMessage(), e);
                    }
                    if (contents != null) {

                        if (contents.isEmpty()) {
                            respData = String.format("没有找到与【%s】相关的信息 in [%s] ", words, catalog);
                        } else {
                            respData = "";
                            for (Map m : contents) {
                                String author = (String) m.get("author");
                                String body = (String) m.get("body");
                                String pubdate = StringUtils.isEmpty((String) m.get("pubDate")) ? ""
                                        : (String) m.get("pubDate");
                                String portrait = (String) m.get("portrait");
                                respData += String.format(
                                        "</br><img src=\"%s\" class=\"img-circle weibo-avar\"/> <b>%s</b> : %s - %s",
                                        portrait, StringUtils.isEmpty(author) ? "" : author, body, pubdate);
                            }
                        }
                    } else {
                        respData = resq_content;
                    }
                }
            } else {

                Map<String, String> data = new HashMap<String, String>();
                data.put("access_token", OscUtils.accessToken);
                data.put("catalog", catalog);
                data.put("q", words);
                data.put("dataType", "json");
                String reqData = "";

                try {
                    reqData = new ObjectMapper().writeValueAsString(data);

                } catch (IOException e) {
                    respData += "," + e.getMessage();
                    logger.warn(e.getMessage(), e);
                }

                if (StringUtils.isEmpty(reqData)) {
                    logger.error("req_data is empty : return");
                } else {
                    logger.info(String.format("req_data is : %s", reqData));
                    String respContent = oscUtil.search(catalog, words);
                    List<Map> contents = null;
                    try {
                        contents = (List<Map>) ((Map) mapper.readValue(respContent, Map.class)).get("searchlist");
                    } catch (IOException e) {
                        logger.warn(e.getMessage(), e);
                    }
                    if (contents != null) {

                        if (contents.isEmpty()) {
                            respData = String.format("没有找到与【%s】相关的信息 in [%s] ", words, catalog);
                        } else {
                            respData = "";
                            for (Map m : contents) {
                                String author = (String) m.get("author");
                                String title = (String) m.get("title");
                                String pubdate = StringUtils.isEmpty((String) m.get("pubDate")) ? ""
                                        : (String) m.get("pubDate");
                                if (((String) m.get("type")).equals("project")) {
                                    title = (String) m.get("name");
                                }
                                String url = (String) m.get("url");
                                respData += String.format(
                                        "</br> <b>%s : </b> <a target=\"_blank\" href=\"%s\">%s </a> - %s",
                                        StringUtils.isEmpty(author) ? "" : author, url, title, pubdate);
                            }
                        }
                    } else {
                        respData = respContent;
                    }
                }
            }

            returnData.put("title", "osc[" + text + "]");
            returnData.put("text", respData);
        }
        return returnData;
    }

}
