package edu.tongji.notificationservice.service;

import edu.tongji.notificationservice.entity.Notification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 通知内存存储。notification-service 当前无独立数据库，使用内存保存近期通知，
 * 以便前端通过 REST 拉取历史；实时推送由 WebSocket 完成。
 */
@Service
public class NotificationStore {

    /** 每个用户的通知列表（定向通知） */
    private final ConcurrentHashMap<Long, List<Notification>> userNotifications = new ConcurrentHashMap<>();
    /** 广播通知（如小说更新）的最近列表，全局共享 */
    private final List<Notification> globalFeed = Collections.synchronizedList(new ArrayList<>());
    /** 广播列表容量上限 */
    private static final int GLOBAL_LIMIT = 100;
    /** 自增 ID */
    private final AtomicLong idSequence = new AtomicLong(1);

    public Notification save(Long targetUserId, String type, String title, String content,
                             Map<String, Object> data, Long timestamp) {
        Notification n = new Notification(
                idSequence.getAndIncrement(), type, targetUserId, title, content, data, timestamp, false);
        if (targetUserId != null) {
            userNotifications.computeIfAbsent(targetUserId, k -> Collections.synchronizedList(new ArrayList<>()))
                    .add(n);
        } else {
            globalFeed.add(n);
            if (globalFeed.size() > GLOBAL_LIMIT) {
                globalFeed.remove(0);
            }
        }
        return n;
    }

    /**
     * 获取某用户可见的通知：个人定向通知 + 全局广播，按时间倒序。
     */
    public List<Notification> getHistory(Long userId) {
        List<Notification> result = new ArrayList<>(globalFeed);
        if (userId != null) {
            List<Notification> personal = userNotifications.get(userId);
            if (personal != null) {
                result.addAll(personal);
            }
        }
        result.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
        return result;
    }

    public void markRead(Long id) {
        for (List<Notification> list : userNotifications.values()) {
            for (Notification n : list) {
                if (n.getId() != null && n.getId().equals(id)) {
                    n.setRead(true);
                    return;
                }
            }
        }
        for (Notification n : globalFeed) {
            if (n.getId() != null && n.getId().equals(id)) {
                n.setRead(true);
                return;
            }
        }
    }
}
