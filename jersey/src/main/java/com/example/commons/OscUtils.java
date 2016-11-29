package com.example.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OscUtils {

    private static Logger logger = LoggerFactory.getLogger(OscUtils.class);
    private static final String HEADERUSERAGENT="Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803";
    
    private static String clientId = "rVvmjArEXwSioLakrx5M";
    private static String clientSecret = "ZgU8C2mUKeK6WHS0G4xIvDpclKt6JG2l";
    private static String baseUrl = "https://www.oschina.net";

    private static String redirectUri = "http://xianzhouhe.eicp.net/jersey/webhook/osc/authback";

    public static String authCode = "LDHcgP";
    public static String reqState = "xyz";
    public static String accessToken = "074ca0cb-8e78-4493-ab93-704dd67fc461";
    public static String refreshToken = "d6b67606-00e3-4d94-84a8-931de8110b35";
    
   

    private static final String CHARSET = "utf-8";

    /**
     * 获取osc认证地址
     * 
     * @return
     */
    @SuppressWarnings("deprecation")
    public String getOauth2AuthUrl() {
        String url = "/action/oauth2/authorize";
        String responseType = "code";

        String param = String.format("response_type=%s&client_id=%s&state=%s&redirect_uri=%s", responseType, clientId,
                reqState, URLEncoder.encode(redirectUri));
        return baseUrl + url + "?" + param;
    }

    /**
     * 获取osc认证后的访问token
     * 
     * @param code
     * @return
     */
    @SuppressWarnings("deprecation")
    public String fetchOauth2Token(String code) {
        String url = "/action/openapi/token";
        String param = String.format("dataType=%s&code=%s&grant_type=%s&client_id=%s&client_secret=%s&redirect_uri=%s",
                "json", code, "authorization_code", clientId, clientSecret, URLEncoder.encode(redirectUri));
        logger.info(String.format("req_data is : %s", param));
        String res_data = getRequest(String.format("%s%s?%s", baseUrl, url, param));
        logger.info(String.format("res_data is : %s", res_data));
        return res_data;
    }

    /**
     * osc搜索【news-新闻，blog-博客，project-开源软件，post-帖子、问答】
     * 
     * @param catalog
     *            enum【"news","blog","project","post","0"】
     * @param words
     * @return
     */
    public String search(String catalog, String words) {
        String url = "/action/openapi/search_list";
        String wordsEncode = words;
        try {
            wordsEncode = URLEncoder.encode(words, CHARSET);
        } catch (UnsupportedEncodingException e) {
            logger.warn(e.getMessage(), e);
        }
        String param = String.format("dataType=%s&access_token=%s&catalog=%s&q=%s", "json", accessToken, catalog,
                wordsEncode);
        return postRequest(url, param);

    }

    /**
     * osc发帖
     * 
     * @param isNoticeMe
     * @param catalog
     * @param title
     * @param content
     * @param askuser
     * @return
     */
    public String pub_posts(Integer isNoticeMe, Integer catalog, String title, String content, Long askuser) {
        String url = "/action/openapi/post_pub";
        // access_token true string oauth2_token获取的access_token
        // isNoticeMe false int 有回答是否邮件通知 2是邮件通知 0
        // catalog true int 类别ID 1-问答 2-分享 3-IT杂烩(综合) 4-站务 100-职业生涯 1
        // title true string 帖子标题
        // content true string 帖子内容
        // askuser false long 用户id（向某人提问）

        StringBuilder sb = new StringBuilder();
        try {
            sb.append("access_token=" + accessToken).append("&catalog=" + catalog)
                    .append("&title=" + URLEncoder.encode(title, CHARSET))
                    .append("&content=" + URLEncoder.encode(content, CHARSET));
        } catch (UnsupportedEncodingException e) {
            logger.warn(e.getMessage(), e);
        }
        if (isNoticeMe != null) {
            sb.append("&isNoticeMe=" + isNoticeMe);
        }
        if (askuser != null) {
            sb.append("&askuser=" + askuser);
        }
        String params = sb.toString();
        logger.info(String.format("req_data is : %s", params));
        String res_data = request(HttpPost.METHOD_NAME, url, params);
        logger.info(String.format("res_data is : %s", res_data));
        return res_data;
    }

    /**
     * 获取动弹信息
     * 
     * @param userid
     *            【0标示热门动弹，-1标示最新动弹，其它值标示自己的动弹】
     * @return
     */
    public String list_tweet(long userid) {
        String url = "/action/openapi/tweet_list";
        StringBuilder sb = new StringBuilder();
        sb.append("access_token=" + accessToken).append("&user=" + userid);
        String params = sb.toString();
        logger.info(String.format("req_data is : %s", params));

        String res_data = postRequest(url, params);
        logger.info(String.format("res_data is : %s", res_data));
        return res_data;
    }

    /**
     * 发布一条纯文本的动弹
     * 
     * @param msg
     *            消息内容
     * @return
     */
    @SuppressWarnings("deprecation")
    public String pub_tweet(String msg) {
        // access_token true string oauth2_token获取的access_token
        // msg true string 动弹内容
        // img false image 图片流

        String url = "/action/openapi/tweet_pub";
        StringBuilder sb = new StringBuilder();
        sb.append("access_token=" + accessToken).append("&msg=" + URLEncoder.encode(msg));
        String params = sb.toString();
        logger.info(String.format("req_data is : %s", params));
        String res_data = request(HttpPost.METHOD_NAME, url, params);
        logger.info(String.format("res_data is : %s", res_data));
        return res_data;
    }

    /**
     * 发布一条带图片的的动弹
     * 
     * @param msg
     *            消息内容
     * @param img
     *            img(object type)可以是File、InputStram、byte[]类型
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public String pubTweet(String msg, Object img) {
        if (img == null) {
            return pub_tweet(msg);
        }
        String url = "/action/openapi/tweet_pub";
        Map<String, Object> params = new HashMap();
        params.put("access_token", accessToken);
        params.put("msg", msg);
        params.put("img", img);
        logger.info(String.format("req_data is : %s", params));
        String reqData = request(HttpPost.METHOD_NAME, url, params);
        logger.info(String.format("res_data is : %s", reqData));
        return reqData;
    }

    /**
     * 获取软件列表 recommend-推荐|time-最新|view-热门|cn-国产
     * 
     * @param type
     * @return
     */
    public String projectList(String type) {
        String url = "/action/openapi/project_list";
        StringBuilder sb = new StringBuilder();
        sb.append("access_token=" + accessToken).append("&type=" + type);
        String params = sb.toString();
        logger.info(String.format("req_data is : %s", params));

        String respData = postRequest(url, params);
        logger.info(String.format("res_data is : %s", respData));
        return respData;
    }

    private String buildReqeustUrl(String relativeUrl, String params) {
        String url = String.format("%s%s", baseUrl, relativeUrl);
        if (StringUtils.isBlank(params)) {
            return url;
        } else {
            return url + "?" + params;
        }
    }

    private String buildReqeustUrlWithOutParam(String relativeUrl) {
        return String.format("%s%s", baseUrl, relativeUrl);
    }

    public String postRequest(String reqUrl, String reqData) {
        String reqUrlwithParam = buildReqeustUrl(reqUrl, reqData);

        boolean ssl = StringUtils.startsWith(reqUrlwithParam, "https") ? true : false;
        try (CloseableHttpClient httpclient = NETUtils.getHttpClient(ssl);) {

            HttpPost httpPost = new HttpPost(reqUrlwithParam);
            httpPost.addHeader(HttpHeaders.USER_AGENT,HEADERUSERAGENT);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            logger.info(response.toString());
            if (response.getStatusLine().getStatusCode() < 300) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (UnsupportedCharsetException | ParseException | IOException e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }

    private String getRequest(String reqUrl) {
        boolean ssl = StringUtils.startsWith(reqUrl, "https") ? true : false;
        try (CloseableHttpClient httpclient = NETUtils.getHttpClient(ssl)) {
            HttpGet httpget = new HttpGet(reqUrl);
            httpget.addHeader(HttpHeaders.USER_AGENT,HEADERUSERAGENT);
            CloseableHttpResponse response = httpclient.execute(httpget);
            logger.info(response.toString());
            if (response.getStatusLine().getStatusCode() < 300) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (UnsupportedCharsetException | ParseException | IOException e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }

    private String request(String method, String url, String params) {
        String reqUrl = buildReqeustUrlWithOutParam(url);
        boolean ssl = StringUtils.startsWith(reqUrl, "https") ? true : false;

        CloseableHttpResponse response = null;
        try (CloseableHttpClient httpclient = NETUtils.getHttpClient(ssl)) {
            if (StringUtils.equals(HttpGet.METHOD_NAME, method)) {
                response = httpclient.execute(getHttpGet(reqUrl));
            } else if (StringUtils.equals(HttpPost.METHOD_NAME, method)) {
                response = httpclient.execute(getHttpPost(reqUrl, params));
            } else {
                return null;
            }
            logger.info(response.toString());
            if (response.getStatusLine().getStatusCode() < 300) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (UnsupportedCharsetException | ParseException | IOException e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }

    private String request(String method, String url, Map<String, Object> params) {
        String reqUrl = buildReqeustUrlWithOutParam(url);
        boolean ssl = StringUtils.startsWith(reqUrl, "https") ? true : false;
        CloseableHttpResponse response = null;
        try (CloseableHttpClient httpclient = NETUtils.getHttpClient(ssl)) {
            if (StringUtils.equals(HttpGet.METHOD_NAME, method)) {
                response = httpclient.execute(getHttpGet(reqUrl));
            } else if (StringUtils.equals(HttpPost.METHOD_NAME, method)) {
                response = httpclient.execute(getHttpPost(reqUrl, params));
            } else {
                return null;
            }
            logger.info(response.toString());
            if (response.getStatusLine().getStatusCode() < 300) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (UnsupportedCharsetException | ParseException | IOException e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }

    private HttpGet getHttpGet(String reqUrl) {
        HttpGet httpRequest = new HttpGet(reqUrl);
        httpRequest.addHeader(HttpHeaders.USER_AGENT,HEADERUSERAGENT);
        return httpRequest;
    }

    private HttpPost getHttpPost(String reqUrl, String params) {
        HttpPost httpPost = new HttpPost(reqUrl);
        httpPost.addHeader(HttpHeaders.USER_AGENT,HEADERUSERAGENT);
        httpPost.addHeader(HttpHeaders.CONTENT_ENCODING, "utf-8");
        List<NameValuePair> formparams = URLEncodedUtils.parse(params, Consts.UTF_8, '&');
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        httpPost.setEntity(entity);
        return httpPost;
    }

    private HttpPost getHttpPost(String reqUrl, Map<String, Object> params) {
        HttpPost httpPost = new HttpPost(reqUrl);
        httpPost.addHeader(HttpHeaders.USER_AGENT,HEADERUSERAGENT);
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        Iterator<String> keys = params.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = params.get(key);
            if (value instanceof String) {
                // 使用part代替body解决乱码问题[entityBuilder.addTextBody(key, (String)
                // value,ContentType.TEXT_PLAIN);]
                ContentType contentType = ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), Consts.UTF_8);
                StringBody stringBody = new StringBody((String) value, contentType);
                entityBuilder.addPart(key, stringBody);
            }
            if (value instanceof File) {
                ContentType contentType = ContentType.create(ContentType.APPLICATION_OCTET_STREAM.getMimeType());
                FileBody fileBody = new FileBody((File) value, contentType);
                entityBuilder.addPart(key, fileBody);
            }
            if (value instanceof InputStream) {
                InputStreamBody inputBody = new InputStreamBody((InputStream) value, "img.png");
                entityBuilder.addPart(key, inputBody);
            }
            if (value instanceof byte[]) {
                ByteArrayBody byteBody = new ByteArrayBody((byte[]) value, "img.png");
                entityBuilder.addPart(key, byteBody);
            }
            logger.info(String.format("not found object type for %s param", key));
        }
        httpPost.setEntity(entityBuilder.build());
        return httpPost;
    }

    public static void main(String[] args) throws FileNotFoundException {
        OscUtils oscUtil = new OscUtils();
        File f = new File("d:/20150612113904.png");
        try (InputStream in = new FileInputStream(f); ImageInputStream imageIo = ImageIO.createImageInputStream(in)) {
            byte[] b = new byte[((Long)f.length()).intValue()];
            imageIo.read(b);
            oscUtil.pubTweet(
                    "@开源中国 @乔布斯 @小编辑【这不是恶搞，这真是一个问题】/action/openapi/tweet_pub中的【img false image 图片流】怎么使用?我用流传输了，发布也正常，文字可以显示出来，可是图片没显示出来，谁知道什么原因？",
                    b);
            oscUtil.pubTweet(
                    "@开源中国 @乔布斯 @小编辑【这不是恶搞，这真是一个问题】/action/openapi/tweet_pub中的【img false image 图片流】怎么使用?我用流传输了，发布也正常，文字可以显示出来，可是图片没显示出来，谁知道什么原因？",
                    f);
            oscUtil.pubTweet("@开源中国 @乔布斯 @小编辑【这不是恶搞，这是一次求助】请问一条沉寂了多年的帖子，怎样才能让再置顶呢？回帖有惊喜哦", in);
            oscUtil.list_tweet(-1);
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }
    }

}
