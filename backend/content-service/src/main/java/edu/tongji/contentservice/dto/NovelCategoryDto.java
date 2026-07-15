package edu.tongji.contentservice.dto;

public class NovelCategoryDto {
    private Long novelId;
    private String categoryName;
    
    public NovelCategoryDto() {
    }
    
    public NovelCategoryDto(Long novelId, String categoryName) {
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
}