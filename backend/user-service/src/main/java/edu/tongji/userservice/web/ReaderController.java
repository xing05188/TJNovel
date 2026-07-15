package edu.tongji.userservice.web;

import edu.tongji.common.dto.ApiResponse;
import edu.tongji.userservice.dto.LoginResponseDto;
import edu.tongji.userservice.dto.ReaderDetailDto;
import edu.tongji.userservice.dto.ReaderLoginDto;
import edu.tongji.userservice.dto.ReaderRegisterDto;
import edu.tongji.userservice.dto.ReaderResetPasswordDto;
import edu.tongji.userservice.entity.Reader;
import edu.tongji.userservice.service.ReaderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.List;
import java.util.Optional;

/**
 * 读者控制器
 * 提供读者注册、登录等接口
 */
@RestController
@RequestMapping("/readers")
@Tag(name = "读者管理", description = "读者注册、登录等接口")
public class ReaderController {
    
    @Autowired
    private ReaderService readerService;
    
    /**
     * 读者注册接口
     * @param dto 注册信息（用户名、密码、手机号）
     * @return 注册结果
     */
    @PostMapping("/register-reader")
    @Operation(summary = "读者注册", description = "注册新读者，需要提供用户名、密码和手机号")
    public ResponseEntity<ApiResponse<Reader>> register(@RequestBody ReaderRegisterDto dto) {
        try {
            Reader reader = readerService.register(
                dto.getReaderName(),
                dto.getPassword(),
                dto.getPhone()
            );
            
            return ResponseEntity.ok(ApiResponse.okWithMessage("注册成功", reader));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "注册失败：" + e.getMessage()));
        }
    }
    
    /**
     * 读者登录接口
     * @param dto 登录信息（用户名、密码）
     * @return 登录响应（包含JWT Token）
     */
    @PostMapping("/login-reader")
    @Operation(summary = "读者登录", description = "读者登录，验证用户名和密码，返回JWT Token")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@RequestBody ReaderLoginDto dto) {
        try {
            LoginResponseDto response = readerService.login(
                dto.getReaderName(),
                dto.getPassword()
            );
            
            return ResponseEntity.ok(ApiResponse.okWithMessage("登录成功", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "登录失败：" + e.getMessage()));
        }
    }
    
    /**
     * 重置密码接口
     * @param dto 重置密码信息（读者名、手机号、新密码）
     * @return 重置结果
     */
    @PostMapping("/reset-reader-password")
    @Operation(summary = "重置密码", description = "根据用户名和手机号重置密码")
    public ResponseEntity<ApiResponse<Reader>> resetPassword(@RequestBody ReaderResetPasswordDto dto) {
        try {
            Reader reader = readerService.resetPassword(
                dto.getReaderName(),
                dto.getPhone(),
                dto.getNewPassword()
            );
            return ResponseEntity.ok(ApiResponse.okWithMessage("密码重置成功", reader));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "密码重置失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取所有读者接口
     * @return 所有读者列表
     */
    @GetMapping
    @Operation(summary = "获取所有读者", description = "获取系统中所有读者的信息")
    public ResponseEntity<ApiResponse<List<Reader>>> getAllReaders() {
        try {
            List<Reader> readers = readerService.getAllReaders();
            return ResponseEntity.ok(ApiResponse.ok("user-service", readers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "获取读者列表失败：" + e.getMessage()));
        }
    }
    
    /**
     * 创建新读者
     * @param reader 读者信息
     * @return 创建结果
     */
    @PostMapping
    @Operation(summary = "创建新读者", description = "创建新的读者")
    public ResponseEntity<ApiResponse<Reader>> createReader(@RequestBody Reader reader) {
        try {
            Reader createdReader = readerService.createReader(reader);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.okWithMessage("创建成功", createdReader));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "创建读者失败：" + e.getMessage()));
        }
    }
    
    /**
     * 更新读者信息
     * @param readerId 读者ID
     * @param reader 更新的读者信息
     * @return 更新结果
     */
    @PutMapping("/{readerId}")
    @Operation(summary = "更新读者信息", description = "根据ID更新读者信息")
    public ResponseEntity<ApiResponse<Reader>> updateReader(@PathVariable Long readerId, @RequestBody Reader reader) {
        try {
            Reader updatedReader = readerService.updateReader(readerId, reader);
            return ResponseEntity.ok(ApiResponse.okWithMessage("更新成功", updatedReader));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "更新读者失败：" + e.getMessage()));
        }
    }
    
    /**
     * 删除读者
     * @param readerId 读者ID
     * @return 删除结果
     */
    @DeleteMapping("/{readerId}")
    @Operation(summary = "删除读者", description = "根据ID删除读者")
    public ResponseEntity<ApiResponse<String>> deleteReader(@PathVariable Long readerId) {
        try {
            readerService.deleteReader(readerId);
            return ResponseEntity.ok(ApiResponse.okWithMessage("删除成功", "success"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "删除读者失败：" + e.getMessage()));
        }
    }
    
    /**
     * 上传读者头像
     * @param readerId 读者ID
     * @param file 头像文件
     * @return 上传结果
     */
    @PostMapping("/{readerId}/UploadAvatar")
    @Operation(summary = "上传读者头像", description = "为指定读者上传头像")
    public ResponseEntity<ApiResponse<String>> uploadAvatar(@PathVariable Long readerId, @RequestParam("file") MultipartFile file) {
        try {
            String avatarUrl = readerService.uploadAvatar(readerId, file);
            return ResponseEntity.ok(ApiResponse.okWithMessage("头像上传成功", avatarUrl));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "头像上传失败：" + e.getMessage()));
        }
    }
    
    /**
     * 上传读者背景
     * @param readerId 读者ID
     * @param file 背景文件
     * @return 上传结果
     */
    @PostMapping("/{readerId}/UploadBackGround")
    @Operation(summary = "上传读者背景", description = "为指定读者上传背景图片")
    public ResponseEntity<ApiResponse<String>> uploadBackground(@PathVariable Long readerId, @RequestParam("file") MultipartFile file) {
        try {
            String backgroundUrl = readerService.uploadBackground(readerId, file);
            return ResponseEntity.ok(ApiResponse.okWithMessage("背景上传成功", backgroundUrl));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "背景上传失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取读者余额接口
     * @param readerId 读者ID
     * @return 读者余额
     */
    @GetMapping("/{readerId}/balance")
    @Operation(summary = "获取读者余额", description = "根据读者ID获取读者的余额信息")
    public ResponseEntity<ApiResponse<BigDecimal>> getBalance(
            @Parameter(description = "读者ID", required = true, example = "1")
            @PathVariable("readerId") Long readerId) {
        try {
            BigDecimal balance = readerService.getBalanceById(readerId);
            return ResponseEntity.ok(ApiResponse.ok("user-service", balance));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "获取读者余额失败：" + e.getMessage()));
        }
    }
    
    /**
     * 扣除读者余额接口
     * @param readerId 读者ID
     * @param requestBody 包含扣除金额的请求体
     * @return 操作结果
     */
    @PostMapping("/{readerId}/deduct-balance")
    @Operation(summary = "扣除读者余额", description = "根据读者ID扣除指定金额的余额")
    public ResponseEntity<ApiResponse<Boolean>> deductBalance(
            @Parameter(description = "读者ID", required = true, example = "1")
            @PathVariable("readerId") Long readerId,
            @RequestBody Map<String, Object> requestBody) {
        try {
            BigDecimal amount = new BigDecimal(requestBody.get("amount").toString());
            boolean success = readerService.deductBalance(readerId, amount);
            return ResponseEntity.ok(ApiResponse.ok("user-service", success));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "扣除读者余额失败：" + e.getMessage()));
        }
    }
    
    /**
     * 增加读者余额接口
     * @param readerId 读者ID
     * @param requestBody 包含增加金额的请求体
     * @return 操作结果
     */
    @PostMapping("/{readerId}/add-balance")
    @Operation(summary = "增加读者余额", description = "根据读者ID增加指定金额的余额")
    public ResponseEntity<ApiResponse<Boolean>> addBalance(
            @Parameter(description = "读者ID", required = true, example = "1")
            @PathVariable("readerId") Long readerId,
            @RequestBody Map<String, Object> requestBody) {
        try {
            BigDecimal amount = new BigDecimal(requestBody.get("amount").toString());
            boolean success = readerService.addBalance(readerId, amount);
            return ResponseEntity.ok(ApiResponse.ok("user-service", success));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "增加读者余额失败：" + e.getMessage()));
        }
    }
    
    /**
     * 根据ID获取读者详情
     * @param readerId 读者ID
     * @return 读者信息
     */
    @GetMapping("/{readerId}")
    @Operation(summary = "获取读者详情", description = "根据读者ID获取读者详细信息")
    public ResponseEntity<ApiResponse<ReaderDetailDto>> getReader(
            @Parameter(description = "读者ID", required = true, example = "1")
            @PathVariable("readerId") Long readerId) {
        try {
            Optional<Reader> readerOpt = readerService.findById(readerId);
            if (readerOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("user-service", "读者不存在"));
            }
            
            Reader reader = readerOpt.get();
            // 转换为DTO，不包含密码等敏感信息
            ReaderDetailDto dto = new ReaderDetailDto(
                reader.getReaderId(),
                reader.getReaderName(),
                reader.getPhone(),
                reader.getGender(),
                reader.getBalance(),
                reader.getAvatarUrl(),
                reader.getBackgroundUrl(),
                reader.getIsCollectVisible(),
                reader.getIsRecommendVisible(),
                reader.getCreateTime()
            );
            
            return ResponseEntity.ok(ApiResponse.ok("user-service", dto));
        } catch (Exception e) {
            // 打印详细错误信息到控制台
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "获取读者信息失败：" + e.getMessage() + " | " + e.getClass().getName()));
        }
    }
    
    /**
     * 健康检查接口
     */
    @GetMapping("/ping")
    @Operation(summary = "健康检查", description = "检查服务是否正常运行")
    public ResponseEntity<ApiResponse<String>> ping() {
        return ResponseEntity.ok(ApiResponse.ok("user-service", "hello"));
    }
    
    /**
     * 读者登出接口
     * @return 登出结果
     */
    @PostMapping("/logout")
    @Operation(summary = "读者登出", description = "读者登出")
    public ResponseEntity<ApiResponse<String>> logout() {
        // 在实际应用中，这里可以将token加入黑名单或执行其他登出逻辑
        // 目前只返回成功消息
        return ResponseEntity.ok(ApiResponse.okWithMessage("登出成功", "success"));
    }
    
    /**
     * 搜索读者接口
     * @param keyword 搜索关键词
     * @return 搜索结果
     */
    @GetMapping("/search")
    @Operation(summary = "搜索读者", description = "通过读者名模糊搜索读者")
    public ResponseEntity<ApiResponse<List<Reader>>> searchReaders(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        try {
            List<Reader> readers = readerService.searchReadersByName(keyword);
            return ResponseEntity.ok(ApiResponse.ok("user-service", readers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "搜索读者失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取读者总数接口
     * @return 读者总数
     */
    @GetMapping("/statistics/total-readers")
    @Operation(summary = "获取读者总数", description = "获取系统中读者的总数")
    public ResponseEntity<ApiResponse<Long>> getTotalReadersCount() {
        try {
            Long count = readerService.getTotalReadersCount();
            return ResponseEntity.ok(ApiResponse.ok("user-service", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "获取读者总数失败：" + e.getMessage()));
        }
    }
}

