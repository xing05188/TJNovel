package edu.tongji.contentservice.service;

import edu.tongji.contentservice.entity.Novel;
import edu.tongji.contentservice.entity.Category;
import edu.tongji.contentservice.entity.NovelCategory;
import edu.tongji.contentservice.repository.NovelCategoryRepository;
import edu.tongji.contentservice.repository.NovelRepository;
import edu.tongji.contentservice.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NovelCategoryService {
    
    @Autowired
    private NovelCategoryRepository novelCategoryRepository;
    
    @Autowired
    private NovelRepository novelRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    // 添加小说与分类关系
    @Transactional
    public boolean addNovelCategoryRelation(Long novelId, String categoryName) {
        // 检查小说和分类是否存在
        if (!novelRepository.existsById(novelId) || !categoryRepository.existsByCategoryName(categoryName)) {
            return false;
        }
        
        // 检查关系是否已存在
        if (novelCategoryRepository.existsByNovelIdAndCategoryName(novelId, categoryName)) {
            return false; // 关系已存在
        }
        
        // 创建新关系
        NovelCategory novelCategory = new NovelCategory(novelId, categoryName);
        novelCategoryRepository.save(novelCategory);
        return true;
    }
    
    // 删除小说与分类关系
    @Transactional
    public boolean deleteNovelCategoryRelation(Long novelId, String categoryName) {
        // 查找关系
        Optional<NovelCategory> novelCategoryOpt = novelCategoryRepository.findByNovelIdAndCategoryName(novelId, categoryName);
        
        if (novelCategoryOpt.isPresent()) {
            NovelCategory novelCategory = novelCategoryOpt.get();
            novelCategoryRepository.delete(novelCategory);
            return true;
        }
        
        return false; // 关系不存在
    }
    
    // 获取所有小说与分类的关系
    public List<Object[]> getAllNovelCategoryRelations() {
        return novelCategoryRepository.findAllNovelCategoryRelations();
    }
    
    // 获取某本小说的全部分类
    public List<Category> getCategoriesByNovelId(Long novelId) {
        return novelCategoryRepository.findCategoriesByNovelId(novelId);
    }
    
    // 获取某个分类下的所有小说
    public List<Novel> getNovelsByCategoryName(String categoryName) {
        return novelCategoryRepository.findNovelsByCategoryName(categoryName);
    }
    
    // 检查小说是否存在
    public boolean novelExists(Long novelId) {
        return novelRepository.existsById(novelId);
    }
    
    // 检查分类是否存在
    public boolean categoryExists(String categoryName) {
        return categoryRepository.existsByCategoryName(categoryName);
    }
}