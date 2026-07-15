package edu.tongji.contentservice.entity;

import java.io.Serializable;
import java.util.Objects;

public class RateId implements Serializable {
    private Long novelId;
    private Long readerId;
    
    // 默认构造函数
    public RateId() {}
    
    // 带参数的构造函数
    public RateId(Long novelId, Long readerId) {
        this.novelId = novelId;
        this.readerId = readerId;
    }
    
    // Getters and Setters
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
    
    // 重写equals和hashCode方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RateId rateId = (RateId) o;
        return Objects.equals(novelId, rateId.novelId) &&
               Objects.equals(readerId, rateId.readerId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(novelId, readerId);
    }
}