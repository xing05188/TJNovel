package edu.tongji.contentservice.service;

import edu.tongji.contentservice.entity.Category;
import edu.tongji.contentservice.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    // 获取所有分类
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    // 根据ID获取分类
    public Optional<Category> getCategoryById(String id) {
        return categoryRepository.findById(id);
    }
    
    // 添加分类
    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }
    
    // 更新分类
    public Optional<Category> updateCategory(String id, Category category) {
        return categoryRepository.findById(id)
            .map(existingCategory -> {
                existingCategory.setCategoryName(category.getCategoryName());
                return categoryRepository.save(existingCategory);
            });
    }
    
    // 删除分类
    public boolean deleteCategory(String id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // 重命名分类
    public Optional<Category> renameCategory(String oldName, String newName) {
        return categoryRepository.findByCategoryName(oldName)
            .map(category -> {
                category.setCategoryName(newName);
                return categoryRepository.save(category);
            });
    }
    
    // 检查分类是否存在
    public boolean categoryExists(String id) {
        return categoryRepository.existsById(id);
    }
    
    // 检查分类名称是否存在
    public boolean categoryNameExists(String categoryName) {
        return categoryRepository.existsByCategoryName(categoryName);
    }
}