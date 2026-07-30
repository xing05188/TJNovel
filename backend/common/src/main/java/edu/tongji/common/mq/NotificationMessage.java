package edu.tongji.common.mq;

import java.io.Serializable;
import java.util.Map;

/**
 * 异步通知消息载体（通过 RabbitMQ 在微服务间传递）。
 *
 * <p>targetUserId 为 null 时表示广播（推送给所有在线客户端），
 * 例如"小说更新通知"；非 null 时表示定向推送给指定用户，例如"订单回调""审核结果"。</p>
 */
public class NotificationMessage implements Serializable {

    /** 消息类型：NOVEL_UPDATE / ORDER_EVENT / AUDIT_RESULT / SYSTEM */
    private String type;

    /** 目标用户 ID，null 表示广播 */
    private Long targetUserId;

    /** 通知标题 */
    private String title;

    /** 通知正文 */
    private String content;

    /** 附加业务数据（如 novelId、orderId、amount 等） */
    private Map<String, Object> data;

    /** 消息产生时间戳（毫秒） */
    private Long timestamp;

    public NotificationMessage() {
    }

    public NotificationMessage(String type, Long targetUserId, String title, String content,
                               Map<String, Object> data, Long timestamp) {
        this.type = type;
        this.targetUserId = targetUserId;
        this.title = title;
        this.content = content;
        this.data = data;
        this.timestamp = timestamp;
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
}
