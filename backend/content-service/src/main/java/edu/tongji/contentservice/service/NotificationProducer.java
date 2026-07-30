package edu.tongji.contentservice.service;

import edu.tongji.common.mq.MqConstants;
import edu.tongji.common.mq.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * content-service 异步通知生产者：通过 RabbitMQ 发布"小说更新通知"和"审核结果推送"。
 * 发送失败仅记录日志，不影响主业务流程（异步解耦）。
 */
@Service
public class NotificationProducer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationProducer.class);

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public NotificationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 发布小说更新通知（广播给所有在线读者）。
     *
     * @param novelId      小说 ID
     * @param novelTitle   小说名
     * @param chapterTitle 新章节标题
     * @param authorId     作者 ID
     */
    public void publishNovelUpdate(Long novelId, String novelTitle, String chapterTitle, Long authorId) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("novelId", novelId);
            data.put("novelTitle", novelTitle);
            data.put("chapterTitle", chapterTitle);
            data.put("authorId", authorId);

            NotificationMessage message = new NotificationMessage(
                    MqConstants.TYPE_NOVEL_UPDATE,
                    null, // 广播
                    "《" + novelTitle + "》更新啦",
                    "新章节：" + chapterTitle,
                    data,
                    System.currentTimeMillis());

            rabbitTemplate.convertAndSend(
                    MqConstants.EXCHANGE,
                    MqConstants.NOVEL_UPDATE_ROUTING_KEY,
                    message);
            logger.info("小说更新通知已发布: novelId={}, chapter={}", novelId, chapterTitle);
        } catch (Exception e) {
            logger.error("发布小说更新通知失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 发布审核结果推送（定向推送给作者）。
     *
     * @param authorId 作者（目标用户）ID
     * @param title    小说名
     * @param passed   true=通过（连载），false=被封禁/驳回
     * @param reason   说明
     */
    public void publishAuditResult(Long authorId, String title, boolean passed, String reason) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("novelTitle", title);
            data.put("passed", passed);

            String content = passed
                    ? "您的小说《" + title + "》已通过审核，开始连载。"
                    : "您的小说《" + title + "》未通过审核。" + (reason == null ? "" : "原因：" + reason);

            NotificationMessage message = new NotificationMessage(
                    MqConstants.TYPE_AUDIT_RESULT,
                    authorId,
                    "审核结果通知",
                    content,
                    data,
                    System.currentTimeMillis());

            rabbitTemplate.convertAndSend(
                    MqConstants.EXCHANGE,
                    MqConstants.NOTIFICATION_ROUTING_KEY,
                    message);
            logger.info("审核结果推送已发布: authorId={}, title={}, passed={}", authorId, title, passed);
        } catch (Exception e) {
            logger.error("发布审核结果推送失败: {}", e.getMessage(), e);
        }
    }
}
