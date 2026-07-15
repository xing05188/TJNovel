package edu.tongji.contentservice.repository;

import edu.tongji.contentservice.entity.NovelCategory;
import edu.tongji.contentservice.entity.NovelCategoryId;
import edu.tongji.contentservice.entity.Novel;
import edu.tongji.contentservice.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NovelCategoryRepository extends JpaRepository<NovelCategory, NovelCategoryId> {
    
    // 根据小说ID和分类名称查找关系
    Optional<NovelCategory> findByNovelIdAndCategoryName(Long novelId, String categoryName);
    
    // 根据小说ID查找所有关系
    List<NovelCategory> findByNovelId(Long novelId);
    
    // 根据分类名称查找所有关系
    List<NovelCategory> findByCategoryName(String categoryName);
    
    // 获取某本小说的全部分类
    @Query("SELECT c FROM Category c WHERE c.categoryName IN " +
           "(SELECT nc.categoryName FROM NovelCategory nc WHERE nc.novelId = :novelId)")
    List<Category> findCategoriesByNovelId(@Param("novelId") Long novelId);
    
    // 获取某个分类下的所有小说
    @Query("SELECT n FROM Novel n WHERE n.novelId IN " +
           "(SELECT nc.novelId FROM NovelCategory nc WHERE nc.categoryName = :categoryName)")
    List<Novel> findNovelsByCategoryName(@Param("categoryName") String categoryName);
    
    // 获取所有小说与分类的关系
    @Query("SELECT nc.novelId, nc.categoryName FROM NovelCategory nc")
    List<Object[]> findAllNovelCategoryRelations();
    
    // 检查关系是否存在
    boolean existsByNovelIdAndCategoryName(Long novelId, String categoryName);
}