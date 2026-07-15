package edu.tongji.transactionservice.web;

import edu.tongji.common.dto.ApiResponse;
import edu.tongji.transactionservice.dto.RewardRequestDto;
import edu.tongji.transactionservice.service.RewardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 打赏API控制器
 */
@RestController
@RequestMapping("/rewards")
@Tag(name = "打赏API", description = "提供打赏相关的API接口")
public class RewardController {

    @Autowired
    private RewardService rewardService;

    /**
     * 用户打赏
     * @param rewardRequest 打赏请求
     * @return 打赏结果
     */
    // 同时支持 /rewards 和 /rewards/ 两种路径，避免网关重写后带尾斜杠导致 404
    @PostMapping({"", "/"})
    @Operation(summary = "用户打赏", description = "用户对小说进行打赏，包括检查余额、创建交易记录、扣除余额、创建打赏记录和作者收入记录")
    public ResponseEntity<ApiResponse<String>> reward(
            @Parameter(description = "打赏请求信息", required = true) 
            @Valid @RequestBody RewardRequestDto rewardRequest) {
        try {
            boolean success = rewardService.processReward(rewardRequest);
            if (success) {
                return ResponseEntity.ok(ApiResponse.okWithMessage("打赏成功", "打赏处理完成"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("transaction-service", "打赏失败，请检查余额是否充足"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("transaction-service", "打赏处理失败：" + e.getMessage()));
        }
    }

    /**
     * 健康检查接口
     * @return 健康状态
     */
    @GetMapping({"/ping"})
    @Operation(summary = "健康检查", description = "检查打赏服务是否正常运行")
    public ApiResponse<String> ping() {
        return ApiResponse.ok("reward-service", "hello");
    }
}