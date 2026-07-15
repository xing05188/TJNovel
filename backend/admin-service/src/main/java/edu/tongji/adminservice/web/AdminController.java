package edu.tongji.adminservice.web;

import edu.tongji.common.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.ok("admin-service", "hello");
    }
}

