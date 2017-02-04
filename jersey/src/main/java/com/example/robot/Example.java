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
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection.FromMode;
import org.jivesoftware.smack.XMPPException;
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

public class Example {

    private static final Logger LOG = LoggerFactory.getLogger(Example.class);

    private static final String HOST = "192.168.1.101";
    private static final int PORT = 5222;
    private static String robotResource = "robot";

    private static String noReply = "我不知道怎么回答";
    static Map<String, String> turingRobots;
    static {
        turingRobots = new HashMap<String, String>();
        turingRobots.put("xiaoai", "f3d7228474114e99aecc6c05fd03c176");
        turingRobots.put("tuling", "c232f980ef2b261b6934506d67e8f0a8");
    };

    XMPPTCPConnection connection = null;
    ChatManager chatMamager = null;
    Map<String, Chat> chats = new HashMap<>();

    public Example() {

        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder().setHost(HOST)
                .setServiceName("bar").setPort(PORT).setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setResource(robotResource).build();
        connection = new XMPPTCPConnection(config);
        connection.setFromMode(FromMode.USER);
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
            chat.close();
        }
    }

    public void start() {
        login("1000", "111111");
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
                        LOG.debug(String.format("%s %s %s %s %s", bareJid, domain, localpart, resource));
                        String body = message.getBody();
                        String robotId = XmppStringUtils.parseLocalpart(message.getTo());
                        String talkerId = XmppStringUtils.parseLocalpart(message.getFrom());
                        if (message.getSubject() == null) {
                            String replyText = robotReply(robotId, talkerId, body);
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

    public void sendPacket(String jid, String text) {
        jid = "1001@bar";
        text = "你好， 播播洒";
        Chat chat = chatMamager.createChat(jid);
        try {
            chat.sendMessage(text);
        } catch (NotConnectedException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            chat.close();
        }
    }

    private String robotReply(String robotId, String talkerId, String content) {
        robotId = "xiaoai";
        String apiKey = turingRobots.get(robotId);
        if (StringUtils.isEmpty(apiKey)) {
            return null;
        }
        HttpURLConnection connection = null;
        try {
            String info = URLEncoder.encode(content, "utf-8");
            String getURL = "http://www.tuling123.com/openapi/api?key=" + apiKey + "&info=" + info + "&userid="
                    + talkerId;
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
            LOG.warn(e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception e) {
                    LOG.warn(e.getMessage(), e);
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
            LOG.warn(e.getMessage(), e);
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

    public static void main(String[] args) {
        Example robot = new Example();
        robot.start();
        try {
            Thread.sleep(3000);
            String text = "你嗷嗷啊";
            robot.sendPacket("1001", text);
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            robot.shutdown();
        }
    }
}
