package com.example.robot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.util.XmppStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.commons.CommonUtils;

public class XMPPRobot extends XMPPServer {

    private static final Logger LOG = LoggerFactory.getLogger(Example.class);
    //private static String robotResource = "robot";

    private static String noReply = "我不知道怎么回答";

    private String username = "1000";
    private String password = "111111";
    XMPPTCPConnection connection = null;
    ChatManager chatMamager = null;
    Map<String, Chat> chats = new HashMap<>();

    public XMPPRobot(String uname, String paswd) {

        this.username = uname;
        this.password = paswd;
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder().setHost(getHost())
                .setServiceName(getDomain()).setPort(getPort())
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled).build();
        connection = new XMPPTCPConnection(config);
    }

    private void login(String username, String password) {
        try {
            if (!connection.isConnected()) {
                connection.connect();
            }
            connection.login(username, password);
        } catch (XMPPException | SmackException | IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void sendMessage(Chat chat, String text) {
        try {
            chat.sendMessage(text);
        } catch (NotConnectedException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            //chat.close();
        }
    }

    public void start() {
        login(username, password);
        chatMamager = ChatManager.getInstanceFor(connection);
        chatMamager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                chat.addMessageListener(new ChatMessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {
                        LOG.debug("receive message......    " + message.toXML().toString());
                        String bareJid = XmppStringUtils.parseBareJid(message.getTo());
                        String domain = XmppStringUtils.parseDomain(message.getTo());
                        String localpart = XmppStringUtils.parseLocalpart(message.getTo());
                        String resource = XmppStringUtils.parseResource(message.getTo());
                        LOG.debug(String.format("%s %s %s %s", bareJid, domain, localpart, resource));
                        String body = message.getBody();
                        String robotname = XmppStringUtils.parseLocalpart(message.getTo());
                        String talkername = XmppStringUtils.parseLocalpart(message.getFrom());
                        if (message.getSubject() == null) {
                            String replyText = robotReply(robotname, talkername, body);
                            sendMessage(chat, replyText);
                        }
                    }
                });
            }
        });
    }

    public void shutdown() {
        if (connection != null) {
            connection.disconnect();
        }
    }

    public void sendPacket(String robotjid, String text) {
        Chat chat = chatMamager.createChat(robotjid);
        sendMessage(chat, text);
    }

    private String getFullJid(String uname) {
        return XmppStringUtils.completeJidFrom(uname, getDomain());
    }

    private String robotReply(String robotId, String talkerId, String content) {
        String apiKey = getRobots().get(robotId).getTuringKey();
        if (StringUtils.isEmpty(apiKey)) {
            return null;
        }
        HttpURLConnection connection = null;
        try {
            String info = URLEncoder.encode(content, "utf-8");
            String getURL = TURINGURL + "?key=" + apiKey + "&info=" + info + "&userid=" + talkerId;
            URL getUrl = new URL(getURL);
            connection = (HttpURLConnection) getUrl.openConnection();
            connection.connect();
            // 取得输入流，并使用Reader读取
            try (InputStreamReader inr = new InputStreamReader(connection.getInputStream(), "utf-8");
                    BufferedReader reader = new BufferedReader(inr)) {
                String readText = readerToString(reader);
                return parseTuringData(readText);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
        return noReply;
    }

    public String readerToString(BufferedReader reader) {
        StringBuilder sb = new StringBuilder();
        String line = "";
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(sb.toString());
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private String parseTuringData(String turingData) {
        Map<String, ?> jsonStr = CommonUtils.jsonToObject(Map.class, turingData);
        String code = String.valueOf(jsonStr.get("code"));
        if (StringUtils.equals(code, "100000")) {
            return (String) jsonStr.get("text");
        }
        return noReply;
    }

    public static void main(String[] args){
        String uname="1000";
        String paswd="111111";
        XMPPRobot robot = new XMPPRobot(uname,paswd);
        robot.start();
        
        String toName="1001";
        String tojid = robot.getFullJid(toName);
        String text="你嗷嗷啊";
        robot.sendPacket(tojid, text);

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally{
            robot.shutdown();
        }
    }
}
