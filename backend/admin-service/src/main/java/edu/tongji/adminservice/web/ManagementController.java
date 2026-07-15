package edu.tongji.adminservice.web;

import edu.tongji.adminservice.entity.Management;
import edu.tongji.adminservice.service.ManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 管理API控制器
 */
@RestController
@RequestMapping("/api/Management")
@Tag(name = "ManagementAPI", description = "管理记录相关API")
public class ManagementController {
    
    private final ManagementService managementService;
    
    public ManagementController(ManagementService managementService) {
        this.managementService = managementService;
    }
    
    /**
     * 处理 CORS 预检请求
     * @return 响应
     */
    @RequestMapping(method = RequestMethod.OPTIONS)
    @Operation(summary = "处理 CORS 预检请求", description = "处理跨域资源共享预检请求")
    public ResponseEntity<Void> handleOptions() {
        return ResponseEntity.ok().build();
    }
    
    /**
     * 获取所有管理记录
     * @return 所有管理记录列表
     */
    @GetMapping
    @Operation(summary = "获取所有管理记录", description = "获取系统中的所有管理记录")
    public ResponseEntity<List<Management>> getAllManagementRecords() {
        List<Management> records = managementService.getAllManagementRecords();
        return ResponseEntity.ok(records);
    }
    
    /**
     * 根据ID获取管理记录
     * @param id 管理记录ID
     * @return 管理记录
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取管理记录", description = "根据ID获取指定的管理记录")
    public ResponseEntity<Management> getManagementById(
            @Parameter(description = "管理记录ID", required = true) @PathVariable Long id) {
        
        Optional<Management> management = managementService.getManagementById(id);
        return management.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 创建管理记录
     * @param management 管理记录
     * @return 创建的管理记录
     */
    @PostMapping
    @Operation(summary = "创建管理记录", description = "创建新的管理记录")
    public ResponseEntity<Management> createManagement(
            @Parameter(description = "管理记录", required = true) @RequestBody Management management) {
        
        Management createdManagement = managementService.createManagement(management);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdManagement);
    }
    
    /**
     * 更新管理记录
     * @param id 管理记录ID
     * @param management 更新的管理记录
     * @return 更新后的管理记录
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新管理记录", description = "更新指定的管理记录")
    public ResponseEntity<Management> updateManagement(
            @Parameter(description = "管理记录ID", required = true) @PathVariable Long id,
            @Parameter(description = "管理记录", required = true) @RequestBody Management management) {
        
        Optional<Management> updatedManagement = managementService.updateManagement(id, management);
        return updatedManagement.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 删除管理记录
     * @param id 管理记录ID
     * @return 是否删除成功
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除管理记录", description = "删除指定的管理记录")
    public ResponseEntity<Void> deleteManagement(
            @Parameter(description = "管理记录ID", required = true) @PathVariable Long id) {
        
        boolean deleted = managementService.deleteManagement(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    /**
     * 根据管理员ID筛选管理记录
     * @param managerId 管理员ID
     * @return 管理记录列表
     */
    @GetMapping("/by-manager/{managerId}")
    @Operation(summary = "根据管理员ID筛选管理记录", description = "根据管理员ID获取相关的管理记录")
    public ResponseEntity<List<Management>> getManagementByManagerId(
            @Parameter(description = "管理员ID", required = true) @PathVariable Long managerId) {
        
        List<Management> records = managementService.getManagementByManagerId(managerId);
        return ResponseEntity.ok(records);
    }
}