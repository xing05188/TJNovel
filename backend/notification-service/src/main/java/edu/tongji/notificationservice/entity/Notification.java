package edu.tongji.notificationservice.entity;

import java.io.Serializable;
import java.util.Map;

/**
 * 站内通知实体（内存存储，不落库）。
 */
public class Notification implements Serializable {

    private Long id;
    private String type;
    private Long targetUserId; // null 表示广播
    private String title;
    private String content;
    private Map<String, Object> data;
    private Long timestamp;
    private boolean read;

    public Notification() {
    }

    public Notification(Long id, String type, Long targetUserId, String title,
                        String content, Map<String, Object> data, Long timestamp, boolean read) {
        this.id = id;
        this.type = type;
        this.targetUserId = targetUserId;
        this.title = title;
        this.content = content;
        this.data = data;
        this.timestamp = timestamp;
        this.read = read;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
