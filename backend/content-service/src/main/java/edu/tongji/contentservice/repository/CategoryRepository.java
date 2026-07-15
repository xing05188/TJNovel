package edu.tongji.contentservice.repository;

import edu.tongji.contentservice.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    
    // 根据分类名称查找分类
    Optional<Category> findByCategoryName(String categoryName);
    
    // 检查分类名称是否存在
    boolean existsByCategoryName(String categoryName);
}