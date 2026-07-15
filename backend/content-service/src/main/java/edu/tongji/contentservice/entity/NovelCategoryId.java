package edu.tongji.contentservice.entity;

import java.io.Serializable;
import java.util.Objects;

public class NovelCategoryId implements Serializable {
    
    private Long novelId;
    private String categoryName;
    
    public NovelCategoryId() {
    }
    
    public NovelCategoryId(Long novelId, String categoryName) {
        this.novelId = novelId;
        this.categoryName = categoryName;
    }
    
    public Long getNovelId() {
        return novelId;
    }
    
    public void setNovelId(Long novelId) {
        this.novelId = novelId;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        NovelCategoryId that = (NovelCategoryId) o;
        return Objects.equals(novelId, that.novelId) &&
               Objects.equals(categoryName, that.categoryName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(novelId, categoryName);
    }
}