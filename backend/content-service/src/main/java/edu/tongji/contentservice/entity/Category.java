package edu.tongji.contentservice.entity;

import jakarta.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "CATEGORY")
public class Category implements Serializable {
    
    @Id
    @Column(name = "CATEGORY_NAME", length = 20)
    private String categoryName;
    

    
    // 默认构造函数
    public Category() {
    }
    
    // 带参数的构造函数
    public Category(String categoryName) {
        this.categoryName = categoryName;
    }
    
    // Getters and Setters
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
        
        Category category = (Category) o;
        return categoryName != null ? categoryName.equals(category.categoryName) : category.categoryName == null;
    }
    
    @Override
    public int hashCode() {
        return categoryName != null ? categoryName.hashCode() : 0;
    }
}