package edu.tongji.userservice.repository;

import edu.tongji.userservice.entity.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, Long> {
    Optional<Reader> findByReaderName(String readerName);
    boolean existsByReaderName(String readerName);
    
    /**
     * 通过读者名模糊搜索读者
     * @param keyword 关键词
     * @return 匹配的读者列表
     */
    @Query("SELECT r FROM Reader r WHERE r.readerName LIKE %:keyword%")
    List<Reader> findByReaderNameContaining(@Param("keyword") String keyword);
    
    /**
     * 获取读者总数
     * @return 读者总数
     */
    @Query("SELECT COUNT(r) FROM Reader r")
    Long countTotalReaders();
}

