package edu.tongji.userservice.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * RecentReadings实体的复合主键类
 */
public class RecentReadingsId implements Serializable {
    
    private Long readerId;
    private Long novelId;
    
    public RecentReadingsId() {
    }
    
    public RecentReadingsId(Long readerId, Long novelId) {
        this.readerId = readerId;
        this.novelId = novelId;
    }
    
    public Long getReaderId() {
        return readerId;
    }
    
    public void setReaderId(Long readerId) {
        this.readerId = readerId;
    }
    
    public Long getNovelId() {
        return novelId;
    }
    
    public void setNovelId(Long novelId) {
        this.novelId = novelId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecentReadingsId that = (RecentReadingsId) o;
        return Objects.equals(readerId, that.readerId) &&
               Objects.equals(novelId, that.novelId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(readerId, novelId);
    }
}

