package edu.tongji.contentservice.web;

import edu.tongji.contentservice.entity.Category;

import edu.tongji.contentservice.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/Category")
@Tag(name = "Category API", description = "分类管理API")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    // 处理 CORS 预检请求
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> handleOptions() {
        return ResponseEntity.ok().build();
    }
    
    // 获取所有分类
    @Operation(summary = "获取所有分类", description = "获取系统中所有分类的列表")
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }
    
    // 添加分类
    @Operation(summary = "添加新分类", description = "创建一个新的分类并保存到系统中")
    @PostMapping
    public ResponseEntity<Category> addCategory(@RequestBody Category category) {
        Category savedCategory = categoryService.addCategory(category);
        return ResponseEntity.ok(savedCategory);
    }
    
    // 根据ID获取分类
    @Operation(summary = "根据ID获取分类", description = "通过分类ID获取指定分类的详细信息")
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable String id) {
        Optional<Category> category = categoryService.getCategoryById(id);
        return category.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 更新分类（支持重命名）
    @Operation(summary = "更新分类信息", description = "根据ID更新指定分类的信息，支持修改分类名称等属性")
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable String id, 
            @RequestBody Category category) {
        Optional<Category> updatedCategory = categoryService.updateCategory(id, category);
        return updatedCategory.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 删除分类
    @Operation(summary = "删除分类", description = "根据ID删除指定的分类")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        boolean deleted = categoryService.deleteCategory(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 单独的改名接口
    @Operation(summary = "重命名分类", description = "通过旧名称和新名称重命名现有分类")
    @PostMapping("/rename")
    public ResponseEntity<Category> renameCategory(@RequestBody Map<String, String> renameRequest) {
        String oldName = renameRequest.get("oldName");
        String newName = renameRequest.get("newName");
        
        if (oldName == null || newName == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Optional<Category> renamedCategory = categoryService.renameCategory(oldName, newName);
        return renamedCategory.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}