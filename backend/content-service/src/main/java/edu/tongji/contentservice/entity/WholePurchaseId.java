package edu.tongji.contentservice.entity;

import java.io.Serializable;
import java.util.Objects;

public class WholePurchaseId implements Serializable {
    private Long readerId;
    private Long novelId;
    
    // 默认构造函数
    public WholePurchaseId() {}
    
    // 带参数的构造函数
    public WholePurchaseId(Long readerId, Long novelId) {
        this.readerId = readerId;
        this.novelId = novelId;
    }
    
    // Getters and Setters
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
    
    // 重写equals和hashCode方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WholePurchaseId that = (WholePurchaseId) o;
        return Objects.equals(readerId, that.readerId) &&
               Objects.equals(novelId, that.novelId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(readerId, novelId);
    }
}