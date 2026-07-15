package edu.tongji.contentservice.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Collect实体的复合主键类
 */
public class CollectId implements Serializable {
    
    private Long novelId;
    private Long readerId;
    
    public CollectId() {
    }
    
    public CollectId(Long novelId, Long readerId) {
        this.novelId = novelId;
        this.readerId = readerId;
    }
    
    public Long getNovelId() {
        return novelId;
    }
    
    public void setNovelId(Long novelId) {
        this.novelId = novelId;
    }
    
    public Long getReaderId() {
        return readerId;
    }
    
    public void setReaderId(Long readerId) {
        this.readerId = readerId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectId collectId = (CollectId) o;
        return Objects.equals(novelId, collectId.novelId) &&
               Objects.equals(readerId, collectId.readerId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(novelId, readerId);
    }
}

