package edu.tongji.userservice.web;

import edu.tongji.common.dto.ApiResponse;
import edu.tongji.userservice.dto.LoginResponseDto;
import edu.tongji.userservice.dto.ManagerLoginDto;
import edu.tongji.userservice.dto.ManagerRegisterDto;
import edu.tongji.userservice.dto.ManagerResetPasswordDto;
import edu.tongji.userservice.entity.Manager;
import edu.tongji.userservice.service.ManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 * 提供管理员登录等接口
 */
@RestController
@RequestMapping("/managers")
@Tag(name = "管理员管理", description = "管理员登录等接口")
public class ManagerController {
    
    @Autowired
    private ManagerService managerService;
    
    /**
     * 管理员登录接口
     * @param dto 登录信息（管理员名、密码）
     * @return 登录响应（包含JWT Token）
     */
    @PostMapping("/login-manager")
    @Operation(summary = "管理员登录", description = "管理员登录，验证管理员名和密码，返回JWT Token")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@RequestBody ManagerLoginDto dto) {
        try {
            LoginResponseDto response = managerService.login(
                dto.getManagerName(),
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
     * 管理员注册接口
     * @param dto 注册信息（管理员名、密码）
     * @return 注册结果
     */
    @PostMapping("/register-manager")
    @Operation(summary = "管理员注册", description = "注册新管理员，需要提供管理员名和密码")
    public ResponseEntity<ApiResponse<Manager>> register(@RequestBody ManagerRegisterDto dto) {
        try {
            Manager manager = managerService.register(
                dto.getManagerName(),
                dto.getPassword()
            );
            
            return ResponseEntity.ok(ApiResponse.okWithMessage("注册成功", manager));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "注册失败：" + e.getMessage()));
        }
    }
    
    /**
     * 重置管理员密码接口
     * @param dto 重置密码信息（管理员名、新密码）
     * @return 重置结果
     */
    @PostMapping("/reset-manager-password")
    @Operation(summary = "重置管理员密码", description = "根据管理员名重置密码")
    public ResponseEntity<ApiResponse<Manager>> resetPassword(@RequestBody ManagerResetPasswordDto dto) {
        try {
            Manager manager = managerService.resetPassword(
                dto.getManagerName(),
                dto.getNewPassword()
            );
            
            return ResponseEntity.ok(ApiResponse.okWithMessage("密码重置成功", manager));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "密码重置失败：" + e.getMessage()));
        }
    }
    
    /**
     * 管理员登出接口
     * @return 登出结果
     */
    @PostMapping("/logout")
    @Operation(summary = "管理员登出", description = "管理员登出")
    public ResponseEntity<ApiResponse<String>> logout() {
        // 在实际应用中，这里可以将token加入黑名单或执行其他登出逻辑
        // 目前只返回成功消息
        return ResponseEntity.ok(ApiResponse.okWithMessage("登出成功", "success"));
    }
    
    /**
     * 获取所有管理员接口
     * @return 所有管理员列表
     */
    @GetMapping
    @Operation(summary = "获取所有管理员", description = "获取系统中所有管理员的信息")
    public ResponseEntity<ApiResponse<java.util.List<Manager>>> getAllManagers() {
        try {
            java.util.List<Manager> managers = managerService.getAllManagers();
            return ResponseEntity.ok(ApiResponse.ok("user-service", managers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "获取管理员列表失败：" + e.getMessage()));
        }
    }
    
    /**
     * 根据ID获取管理员接口
     * @param managerId 管理员ID
     * @return 管理员详情
     */
    @GetMapping("/{managerId}")
    @Operation(summary = "根据ID获取管理员", description = "根据ID获取管理员详情")
    public ResponseEntity<ApiResponse<Manager>> getManagerById(@PathVariable Long managerId) {
        try {
            Manager manager = managerService.findManagerById(managerId);
            return ResponseEntity.ok(ApiResponse.ok("user-service", manager));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "获取管理员详情失败：" + e.getMessage()));
        }
    }
    
    /**
     * 创建新管理员
     * @param manager 管理员信息
     * @return 创建结果
     */
    @PostMapping
    @Operation(summary = "创建新管理员", description = "创建新的管理员")
    public ResponseEntity<ApiResponse<Manager>> createManager(@RequestBody Manager manager) {
        try {
            Manager createdManager = managerService.createManager(manager);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.okWithMessage("创建成功", createdManager));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "创建管理员失败：" + e.getMessage()));
        }
    }
    
    /**
     * 更新管理员信息
     * @param managerId 管理员ID
     * @param manager 更新的管理员信息
     * @return 更新结果
     */
    @PutMapping("/{managerId}")
    @Operation(summary = "更新管理员信息", description = "根据ID更新管理员信息")
    public ResponseEntity<ApiResponse<Manager>> updateManager(@PathVariable Long managerId, @RequestBody Manager manager) {
        try {
            Manager updatedManager = managerService.updateManager(managerId, manager);
            return ResponseEntity.ok(ApiResponse.okWithMessage("更新成功", updatedManager));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "更新管理员失败：" + e.getMessage()));
        }
    }
    
    /**
     * 删除管理员
     * @param managerId 管理员ID
     * @return 删除结果
     */
    @DeleteMapping("/{managerId}")
    @Operation(summary = "删除管理员", description = "根据ID删除管理员")
    public ResponseEntity<ApiResponse<String>> deleteManager(@PathVariable Long managerId) {
        try {
            managerService.deleteManager(managerId);
            return ResponseEntity.ok(ApiResponse.okWithMessage("删除成功", "success"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "删除管理员失败：" + e.getMessage()));
        }
    }
}
