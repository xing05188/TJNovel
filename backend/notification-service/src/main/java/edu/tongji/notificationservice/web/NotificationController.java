package edu.tongji.notificationservice.web;

import edu.tongji.common.dto.ApiResponse;
import edu.tongji.notificationservice.entity.Notification;
import edu.tongji.notificationservice.service.NotificationStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 通知 REST 接口：
 * - GET /notify/ping  健康检查
 * - GET /notify/history?userId=  拉取该用户可见的通知（定向 + 广播）历史
 */
@RestController
@RequestMapping("/notify")
public class NotificationController {

    private final NotificationStore notificationStore;

    @Autowired
    public NotificationController(NotificationStore notificationStore) {
        this.notificationStore = notificationStore;
    }

    @GetMapping("/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.ok("notification-service", "notification-service is running");
    }

    @GetMapping("/history")
    public ApiResponse<List<Notification>> getHistory(@RequestParam(required = false) Long userId) {
        List<Notification> history = notificationStore.getHistory(userId);
        return ApiResponse.ok("notification-service", history);
    }
}
