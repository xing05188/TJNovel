package edu.tongji.notificationservice.web;

import edu.tongji.common.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    @GetMapping("/notify/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.ok("notification-service", "hello");
    }
}

