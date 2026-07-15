package edu.tongji.contentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "小说创建DTO")
public class NovelCreateDto {
    
    @Schema(description = "作者ID", example = "1")
    private Long authorId;
    
    @Schema(description = "小说名称", example = "示例小说")
    private String novelName;
    
    @Schema(description = "小说简介", example = "这是一个示例小说的简介")
    private String introduction;
    
    public Long getAuthorId() {
        return authorId;
    }
    
    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }
    
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
}