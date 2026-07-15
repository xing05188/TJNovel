package edu.tongji.userservice.web;

import edu.tongji.common.dto.ApiResponse;
import edu.tongji.userservice.dto.AuthorChangePasswordDto;
import edu.tongji.userservice.dto.AuthorLoginDto;
import edu.tongji.userservice.dto.AuthorRegisterDto;
import edu.tongji.userservice.dto.AuthorResetPasswordDto;
import edu.tongji.userservice.dto.LoginResponseDto;
import edu.tongji.userservice.entity.Author;
import edu.tongji.userservice.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

/**
 * 作者控制器
 * 提供作者注册、登录等接口
 */
@RestController
@RequestMapping("/authors")
@Tag(name = "作者管理", description = "作者注册、登录等接口")
public class AuthorController {
    
    @Autowired
    private AuthorService authorService;
    
    /**
     * 获取所有作者
     * @return 所有作者列表
     */
    @GetMapping
    @Operation(summary = "获取所有作者", description = "获取系统中所有作者的信息")
    public ResponseEntity<ApiResponse<List<Author>>> getAllAuthors() {
        try {
            List<Author> authors = authorService.findAllAuthors();
            return ResponseEntity.ok(ApiResponse.okWithMessage("获取成功", authors));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "获取作者列表失败：" + e.getMessage()));
        }
    }
    
    /**
     * 根据ID获取作者
     * @param id 作者ID
     * @return 作者信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取作者", description = "根据作者ID获取作者详细信息")
    public ResponseEntity<ApiResponse<Author>> getAuthorById(@PathVariable Long id) {
        try {
            return authorService.findAuthorById(id)
                .map(author -> ResponseEntity.ok(ApiResponse.okWithMessage("获取成功", author)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("user-service", "作者不存在")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "获取作者信息失败：" + e.getMessage()));
        }
    }
    
    /**
     * 创建新作者
     * @param author 作者信息
     * @return 创建结果
     */
    @PostMapping
    @Operation(summary = "创建新作者", description = "创建新的作者")
    public ResponseEntity<ApiResponse<Author>> createAuthor(@RequestBody Author author) {
        try {
            Author createdAuthor = authorService.createAuthor(author);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.okWithMessage("创建成功", createdAuthor));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "创建作者失败：" + e.getMessage()));
        }
    }
    
    /**
     * 更新作者信息
     * @param id 作者ID
     * @param author 更新的作者信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新作者信息", description = "根据ID更新作者信息")
    public ResponseEntity<ApiResponse<Author>> updateAuthor(@PathVariable Long id, @RequestBody Author author) {
        try {
            Author updatedAuthor = authorService.updateAuthor(id, author);
            return ResponseEntity.ok(ApiResponse.okWithMessage("更新成功", updatedAuthor));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "更新作者失败：" + e.getMessage()));
        }
    }
    
    /**
     * 删除作者
     * @param id 作者ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除作者", description = "根据ID删除作者")
    public ResponseEntity<ApiResponse<String>> deleteAuthor(@PathVariable Long id) {
        try {
            authorService.deleteAuthor(id);
            return ResponseEntity.ok(ApiResponse.okWithMessage("删除成功", "success"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "删除作者失败：" + e.getMessage()));
        }
    }
    
    /**
     * 上传作者头像
     * @param id 作者ID
     * @param file 头像文件
     * @return 上传结果
     */
    @PostMapping("/{id}/UploadAvatar")
    @Operation(summary = "上传作者头像", description = "为指定作者上传头像")
    public ResponseEntity<ApiResponse<String>> uploadAvatar(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            String avatarUrl = authorService.uploadAvatar(id, file);
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
     * 作者注册接口
     * @param dto 注册信息（作者名、密码、手机号）
     * @return 注册结果
     */
    @PostMapping("/register-author")
    @Operation(summary = "作者注册", description = "注册新作者，需要提供作者名、密码和手机号")
    public ResponseEntity<ApiResponse<Author>> register(@RequestBody AuthorRegisterDto dto) {
        try {
            Author author = authorService.register(
                dto.getAuthorName(),
                dto.getPassword(),
                dto.getPhone()
            );
            
            return ResponseEntity.ok(ApiResponse.okWithMessage("注册成功", author));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "注册失败：" + e.getMessage()));
        }
    }
    
    /**
     * 作者登录接口
     * @param dto 登录信息（作者名、密码）
     * @return 登录响应（包含JWT Token）
     */
    @PostMapping("/login-author")
    @Operation(summary = "作者登录", description = "作者登录，验证作者名和密码，返回JWT Token")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@RequestBody AuthorLoginDto dto) {
        try {
            LoginResponseDto response = authorService.login(
                dto.getAuthorName(),
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
     * 重置作者密码接口
     * @param dto 重置密码信息（作者名、手机号、新密码）
     * @return 重置结果
     */
    @PostMapping("/reset-author-password")
    @Operation(summary = "重置作者密码", description = "根据作者名和手机号重置密码")
    public ResponseEntity<ApiResponse<Author>> resetPassword(@RequestBody AuthorResetPasswordDto dto) {
        try {
            Author author = authorService.resetPassword(
                dto.getAuthorName(),
                dto.getPhone(),
                dto.getNewPassword()
            );
            
            return ResponseEntity.ok(ApiResponse.okWithMessage("密码重置成功", author));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "密码重置失败：" + e.getMessage()));
        }
    }
    
    /**
     * 修改作者密码接口
     * @param dto 修改密码信息（作者名、旧密码、新密码）
     * @return 修改结果
     */
    @PostMapping("/change-author-password")
    @Operation(summary = "修改作者密码", description = "验证旧密码后修改为新密码")
    public ResponseEntity<ApiResponse<Author>> changePassword(@RequestBody AuthorChangePasswordDto dto) {
        try {
            Author author = authorService.changePassword(
                dto.getAuthorName(),
                dto.getOldPassword(),
                dto.getNewPassword()
            );
            
            return ResponseEntity.ok(ApiResponse.okWithMessage("密码修改成功", author));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "密码修改失败：" + e.getMessage()));
        }
    }
    
    /**
     * 作者登出接口
     * @return 登出结果
     */
    @PostMapping("/logout")
    @Operation(summary = "作者登出", description = "作者登出")
    public ResponseEntity<ApiResponse<String>> logout() {
        // 在实际应用中，这里可以将token加入黑名单或执行其他登出逻辑
        // 目前只返回成功消息
        return ResponseEntity.ok(ApiResponse.okWithMessage("登出成功", "success"));
    }
    
    /**
     * 获取作者注册天数
     * @param authorId 作者ID
     * @return 注册天数
     */
    @GetMapping("/{authorId}/register-days")
    @Operation(summary = "获取作者注册天数", description = "根据作者ID计算并返回注册天数")
    public ResponseEntity<ApiResponse<Long>> getAuthorRegisterDays(@PathVariable Long authorId) {
        try {
            long days = authorService.getAuthorRegisterDays(authorId);
            return ResponseEntity.ok(ApiResponse.okWithMessage("获取成功", days));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "获取作者注册天数失败：" + e.getMessage()));
        }
    }
    
    /**
     * 搜索作者接口
     * @param keyword 搜索关键词
     * @return 搜索结果
     */
    @GetMapping("/search")
    @Operation(summary = "搜索作者", description = "通过作者名模糊搜索作者")
    public ResponseEntity<ApiResponse<List<Author>>> searchAuthors(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        try {
            List<Author> authors = authorService.searchAuthorsByName(keyword);
            return ResponseEntity.ok(ApiResponse.ok("user-service", authors));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "搜索作者失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取作者总数接口
     * @return 作者总数
     */
    @GetMapping("/statistics/total-authors")
    @Operation(summary = "获取作者总数", description = "获取系统中作者的总数")
    public ResponseEntity<ApiResponse<Long>> getTotalAuthorsCount() {
        try {
            Long count = authorService.getTotalAuthorsCount();
            return ResponseEntity.ok(ApiResponse.ok("user-service", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "获取作者总数失败：" + e.getMessage()));
        }
    }
    
    /**
     * 增加作者收入接口
     * @param authorId 作者ID
     * @param requestBody 包含增加金额的请求体
     * @return 操作结果
     */
    @PostMapping("/{authorId}/add-earning")
    @Operation(summary = "增加作者收入", description = "根据作者ID增加指定金额的收入")
    public ResponseEntity<ApiResponse<Boolean>> addEarning(
            @Parameter(description = "作者ID", required = true, example = "1")
            @PathVariable("authorId") Long authorId,
            @RequestBody Map<String, Object> requestBody) {
        try {
            BigDecimal amount = new BigDecimal(requestBody.get("amount").toString());
            boolean success = authorService.addEarning(authorId, amount);
            return ResponseEntity.ok(ApiResponse.ok("user-service", success));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("user-service", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("user-service", "增加作者收入失败：" + e.getMessage()));
        }
    }
}
