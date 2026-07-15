package edu.tongji.contentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "小说编辑DTO")
public class NovelEditDto {
    
    @Schema(description = "小说名称", example = "示例小说")
    private String novelName;
    
    @Schema(description = "小说简介", example = "这是一个示例小说的简介")
    private String introduction;
    
    @Schema(description = "封面URL", example = "https://example.com/cover.jpg")
    private String coverUrl;
    
    @Schema(description = "小说状态", example = "连载")
    private String status;
    
    @Schema(description = "总价格", example = "19.99")
    private Double totalPrice;
    
    public String getNovelName() {
        return novelName;
    }
    
    public void setNovelName(String novelName) {
        this.novelName = novelName;
    }
    
    public String getIntroduction() {
        return introduction;
    }
    
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
    
    public String getCoverUrl() {
        return coverUrl;
    }
    
    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Double getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}