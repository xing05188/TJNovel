package edu.tongji.userservice.web;

import edu.tongji.common.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.ok("user-service", "hello");
    }
}

