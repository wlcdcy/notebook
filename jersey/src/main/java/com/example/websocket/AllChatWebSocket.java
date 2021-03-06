package com.example.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.mina.util.CopyOnWriteMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.license.DESUtil;

@ServerEndpoint(value = "/websocket/chat/{chatType}")
public class AllChatWebSocket {

    private static final Logger log = LoggerFactory.getLogger(AllChatWebSocket.class);
    private static final String GUEST_PREFIX = "Guest";
    private static final AtomicInteger connectionIds = new AtomicInteger(0);
    private static final Set<AllChatWebSocket> connections = new CopyOnWriteArraySet<>();
    private static final Map<String,AllChatWebSocket> conns = new CopyOnWriteMap<>();

    private String nickname;
    private Session session;

    private static final String KEY = "p2p|room";

    public AllChatWebSocket() {
        // nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
        connectionIds.getAndIncrement();
    }

    @OnOpen
    public void start(Session session) {
        this.session = session;
        log.info("sessionId: " + session.getId());
        // log.info("UserPrincipal: " + session.getUserPrincipal().getName());
        nickname = session.getUserPrincipal() != null ? session.getUserPrincipal().getName()
                : GUEST_PREFIX + connectionIds.getAndIncrement();
        connections.add(this);
        conns.put(nickname, this);
        String message = String.format("* %s %s", nickname, "has joined.");
        broadcast(message);
    }

    @OnClose
    public void end() {
        connections.remove(this);
        conns.remove(nickname);
        connectionIds.getAndDecrement();
        String message = String.format("* %s %s", nickname, "has disconnected.");
        log.info(message);
        broadcast(message);
    }

    @OnMessage
    public void incoming(String message) {
        message = decryptMessage(message);
        // Never trust the client
        String filteredMessage = String.format("%s: %s", nickname,
                // HTMLFilter.filter(message.toString())
                message);
        broadcast(filteredMessage);
    }

    @OnError
    public void onError(Throwable t) throws Throwable {
        log.error("Chat Error: " + t.toString(), t);
    }

    private static void broadcast(String msg) {
        msg = encryptMessage(msg);
        for (AllChatWebSocket client : connections) {
            try {
                synchronized (client) {
                    client.session.getBasicRemote().sendText(msg);
                }
            } catch (IOException e) {
                log.debug("Chat Error: Failed to send message to client", e);
                connections.remove(client);
                try {
                    client.session.close();
                } catch (IOException e1) {
                    // Ignore
                }
                String message = String.format("* %s %s", client.nickname, "has been disconnected.");
                broadcast(message);
            }
        }
    }

    private static String encryptMessage(String message) {
        try {
            return DESUtil.encryptBase64(message, KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String decryptMessage(String message) {
        try {
            return DESUtil.decryptBase64(message, KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
