package edu.tongji.notificationservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通知 WebSocket 处理器：维护 userId -> 会话集合，支持定向推送与广播。
 * 连接地址示例：/ws/notifications?userId=123
 */
@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationWebSocketHandler.class);

    /** userId -> 该用户的所有会话（一个用户可能多端登录） */
    private final Map<Long, ConcurrentHashMap<String, WebSocketSession>> userSessions =
            new ConcurrentHashMap<>();
    /** 未携带 userId 的匿名会话（仅接收广播） */
    private final ConcurrentHashMap<String, WebSocketSession> anonymousSessions =
            new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = parseUserId(session);
        if (userId != null) {
            userSessions.computeIfAbsent(userId, k -> new ConcurrentHashMap<>())
                    .put(session.getId(), session);
            logger.info("通知WebSocket连接建立: userId={}, sessionId={}", userId, session.getId());
        } else {
            anonymousSessions.put(session.getId(), session);
            logger.info("通知WebSocket连接建立(匿名): sessionId={}", session.getId());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session,
                                      org.springframework.web.socket.CloseStatus status) {
        Long userId = parseUserId(session);
        if (userId != null) {
            ConcurrentHashMap<String, WebSocketSession> sessions = userSessions.get(userId);
            if (sessions != null) {
                sessions.remove(session.getId());
                if (sessions.isEmpty()) {
                    userSessions.remove(userId);
                }
            }
        } else {
            anonymousSessions.remove(session.getId());
        }
    }

    /** 定向推送给指定用户 */
    public void sendToUser(Long userId, String payload) {
        ConcurrentHashMap<String, WebSocketSession> sessions = userSessions.get(userId);
        if (sessions == null) {
            return;
        }
        for (WebSocketSession s : sessions.values()) {
            sendIfOpen(s, payload);
        }
    }

    /** 广播给所有在线会话（定向 + 匿名） */
    public void broadcast(String payload) {
        userSessions.values().forEach(sessions ->
                sessions.values().forEach(s -> sendIfOpen(s, payload)));
        anonymousSessions.values().forEach(s -> sendIfOpen(s, payload));
    }

    private void sendIfOpen(WebSocketSession session, String payload) {
        if (session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(payload));
            } catch (IOException e) {
                logger.warn("推送通知失败 sessionId={}: {}", session.getId(), e.getMessage());
            }
        }
    }

    private Long parseUserId(WebSocketSession session) {
        try {
            String query = session.getUri() != null ? session.getUri().getQuery() : null;
            if (query == null) {
                return null;
            }
            for (String pair : query.split("&")) {
                String[] kv = pair.split("=");
                if (kv.length == 2 && "userId".equals(kv[0])) {
                    return Long.parseLong(kv[1]);
                }
            }
        } catch (Exception e) {
            logger.warn("解析WebSocket userId失败: {}", e.getMessage());
        }
        return null;
    }
}
