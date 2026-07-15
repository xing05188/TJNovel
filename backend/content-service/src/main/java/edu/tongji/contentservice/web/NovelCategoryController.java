package edu.tongji.contentservice.web;

import edu.tongji.contentservice.entity.Novel;
import edu.tongji.contentservice.entity.Category;
import edu.tongji.contentservice.dto.NovelCategoryDto;
import edu.tongji.contentservice.service.NovelCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/NovelCategory")
@Tag(name = "NovelCategory API", description = "小说分类关系管理API")
public class NovelCategoryController {
    
    @Autowired
    private NovelCategoryService novelCategoryService;
    
    // 添加小说与分类关系
    @Operation(summary = "添加小说与分类关系", description = "为指定小说添加分类关系")
    @PostMapping
    public ResponseEntity<Map<String, Object>> addNovelCategoryRelation(@RequestBody NovelCategoryDto request) {
        Long novelId = request.getNovelId();
        String categoryName = request.getCategoryName();
        
        if (!novelCategoryService.novelExists(novelId)) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "小说不存在");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (!novelCategoryService.categoryExists(categoryName)) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "分类不存在");
            return ResponseEntity.badRequest().body(response);
        }
        
        boolean success = novelCategoryService.addNovelCategoryRelation(novelId, categoryName);
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "添加成功" : "关系已存在");
        
        return ResponseEntity.ok(response);
    }
    
    // 删除小说与分类关系
    @Operation(summary = "删除小说与分类关系", description = "删除指定小说与分类的关系")
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteNovelCategoryRelation(@RequestBody NovelCategoryDto request) {
        Long novelId = request.getNovelId();
        String categoryName = request.getCategoryName();
        
        boolean success = novelCategoryService.deleteNovelCategoryRelation(novelId, categoryName);
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "删除成功" : "关系不存在");
        
        return ResponseEntity.ok(response);
    }
    
    // 获取所有小说与分类的关系
    @Operation(summary = "获取所有小说与分类的关系", description = "获取系统中所有小说与分类的关系列表")
    @GetMapping
    public List<Map<String, Object>> getAllNovelCategoryRelations() {
        List<Object[]> relations = novelCategoryService.getAllNovelCategoryRelations();
        return relations.stream().map(relation -> {
            Map<String, Object> map = new HashMap<>();
            map.put("novelId", relation[0]);
            map.put("categoryName", relation[1]);
            return map;
        }).collect(Collectors.toList());
    }
    
    // 获取某本小说的全部分类
    @Operation(summary = "获取某本小说的全部分类", description = "根据小说ID获取该小说所属的所有分类")
    @GetMapping("/novel/{novelId}")
    public ResponseEntity<?> getCategoriesByNovelId(@PathVariable Long novelId) {
        if (!novelCategoryService.novelExists(novelId)) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "小说不存在");
            return ResponseEntity.badRequest().body(response);
        }
        
        List<Category> categories = novelCategoryService.getCategoriesByNovelId(novelId);
        return ResponseEntity.ok(categories);
    }
    
    // 获取某个分类下的所有小说
    @Operation(summary = "获取某个分类下的所有小说", description = "根据分类名称获取该分类下的所有小说")
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<?> getNovelsByCategoryName(@PathVariable String categoryName) {
        if (!novelCategoryService.categoryExists(categoryName)) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "分类不存在");
            return ResponseEntity.badRequest().body(response);
        }
        
        List<Novel> novels = novelCategoryService.getNovelsByCategoryName(categoryName);
        return ResponseEntity.ok(novels);
    }
}