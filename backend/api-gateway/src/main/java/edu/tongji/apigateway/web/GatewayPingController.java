package edu.tongji.apigateway.web;

import edu.tongji.common.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GatewayPingController {

    @GetMapping("/gateway/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.ok("api-gateway", "hello");
    }
}

