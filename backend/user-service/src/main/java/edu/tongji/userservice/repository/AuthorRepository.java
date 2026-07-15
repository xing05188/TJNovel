package edu.tongji.userservice.repository;

import edu.tongji.userservice.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByAuthorName(String authorName);
    boolean existsByAuthorName(String authorName);
    
    /**
     * 通过作者名模糊搜索作者
     * @param keyword 关键词
     * @return 匹配的作者列表
     */
    @Query("SELECT a FROM Author a WHERE a.authorName LIKE %:keyword%")
    List<Author> findByAuthorNameContaining(@Param("keyword") String keyword);
    
    /**
     * 获取作者总数
     * @return 作者总数
     */
    @Query("SELECT COUNT(a) FROM Author a")
    Long countTotalAuthors();
}
