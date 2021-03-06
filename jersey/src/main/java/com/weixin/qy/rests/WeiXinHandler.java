package com.weixin.qy.rests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.commons.CommonUtils;
import com.weixin.qy.entity.MaterialQuery;
import com.weixin.qy.entity.TextContent;
import com.weixin.qy.entity.TextMessage;
import com.weixin.qy.entity.VoiceContent;
import com.weixin.qy.entity.WeixinMessage;
import com.weixin.qy.rests.WeixinConfig.WeixinAccess;

public class WeiXinHandler implements Runnable {

    static Logger logger = LoggerFactory.getLogger(WeiXinHandler.class);
    private String xmlStr;

    WeiXinHandler(String str) {
        this.xmlStr = str;
    }

    @Override
    public void run() {

        WeixinMessage wxm = CommonUtils.xml2Object(xmlStr, WeixinMessage.class);
        String replyMsg = null;
        TextMessage _tm = new TextMessage();
        _tm.setTouser(wxm.getFromUserName());
        _tm.setAgentid(wxm.getAgentID());
        _tm.setToparty("1");
        // _tm.setTotag(totag);

        if (StringUtils.equals(wxm.getMsgType(), "text")) {
            replyMsg = turing(wxm.getContent());
            TextContent tmc = new TextContent();
            tmc.setContent(replyMsg);
            _tm.setText(tmc);
        } else if (StringUtils.equals(wxm.getMsgType(), "voice")) {
            MaterialQuery param = new MaterialQuery();
            param.setAgentid(0);
            param.setType("voice");
            param.setOffset(0);
            param.setCount(10);
            Map<?, ?> result = getMaterialList(WeixinConfig.getInstance().buildWeixinAccess(), param);

            int index = RandomUtils.nextInt(0, 4);
            @SuppressWarnings("rawtypes")
            String media_id = (String) ((Map) ((List) result.get("itemlist")).get(index)).get("media_id");
            _tm.setMsgtype("voice");
            VoiceContent voice = new VoiceContent();
            voice.setMedia_id(media_id);
            _tm.setVoice(voice);
        }

        String msg = CommonUtils.object2Json(_tm);
        logger.info("response: " + msg);
        sendMessage(WeixinConfig.getInstance().buildWeixinAccess(), msg);
    }

    private Map<?, ?> getMaterialList(WeixinAccess access, MaterialQuery param) {
        String jsonString = WeiXinAPIUtil.materialList(access.getToken(), param);
        Map<?, ?> result = CommonUtils.jsonToObject(Map.class, jsonString);
        if ((Integer) result.get("errcode") == WeixinConfig.access_token_expired) {
            logger.info((String) result.get("errmsg"));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            WeixinAccess access_ = WeixinConfig.getInstance().rebuildWeixinAccess(access);
            return getMaterialList(access_, param);
        } else {
            return result;
        }
    }

    private void sendMessage(WeixinAccess access, String msg) {
        String jsonString = null;
        try {
            jsonString = WeiXinAPIUtil.sendMessage(access.getToken(), msg);
        } catch (WeixinAPIEx e) {
            e.printStackTrace();
            sendMessage(access, msg);
        }

        if (StringUtils.isNotEmpty(jsonString)) {
            Map<?, ?> result = CommonUtils.jsonToObject(Map.class, jsonString);
            if ((Integer) result.get("errcode") == WeixinConfig.access_token_expired) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                WeixinAccess access_ = WeixinConfig.getInstance().rebuildWeixinAccess(access);
                sendMessage(access_, msg);
            }
        }
    }

    public static void main(String[] args) {
        String xml = "";
        WeiXinHandler handler = new WeiXinHandler(xml);
        handler.run();
    }

    public static String turing(String content) {
        StringBuffer sb = null;
        BufferedReader reader = null;
        HttpURLConnection connection = null;
        try {
            String APIKEY = "c232f980ef2b261b6934506d67e8f0a8";
            String INFO = URLEncoder.encode(content, "utf-8");
            String getURL = "http://www.tuling123.com/openapi/api?key=" + APIKEY + "&info=" + INFO;
            URL getUrl = new URL(getURL);
            connection = (HttpURLConnection) getUrl.openConnection();
            connection.connect();

            // 取得输入流，并使用Reader读取
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
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
