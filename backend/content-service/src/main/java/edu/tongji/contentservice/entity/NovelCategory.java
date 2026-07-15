package edu.tongji.contentservice.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "NOVEL_CATEGORY")
@IdClass(NovelCategoryId.class)
public class NovelCategory implements Serializable {
    
    @Id
    @Column(name = "NOVEL_ID")
    private Long novelId;
    
    @Id
    @Column(name = "CATEGORY_NAME", length = 20)
    private String categoryName;
    

    
    // 默认构造函数
    public NovelCategory() {
    }
    
    // 带参数的构造函数
    public NovelCategory(Long novelId, String categoryName) {
        this.novelId = novelId;
        this.categoryName = categoryName;
    }
    
    // Getters and Setters
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
        
        NovelCategory that = (NovelCategory) o;
        return novelId != null ? novelId.equals(that.novelId) : that.novelId == null &&
               categoryName != null ? categoryName.equals(that.categoryName) : that.categoryName == null;
    }
    
    @Override
    public int hashCode() {
        int result = novelId != null ? novelId.hashCode() : 0;
        result = 31 * result + (categoryName != null ? categoryName.hashCode() : 0);
        return result;
    }
}