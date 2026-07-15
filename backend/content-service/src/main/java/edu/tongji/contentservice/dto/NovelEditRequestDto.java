package edu.tongji.contentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "小说修改请求DTO")
public class NovelEditRequestDto {
    
    @Schema(description = "原始小说ID", example = "1")
    private Long originalNovelId;
    
    @Schema(description = "编辑后的小说信息")
    private NovelEditDto editedDto;
    
    public Long getOriginalNovelId() {
        return originalNovelId;
    }
    
    public void setOriginalNovelId(Long originalNovelId) {
        this.originalNovelId = originalNovelId;
    }
    
    public NovelEditDto getEditedDto() {
        return editedDto;
    }
    
    public void setEditedDto(NovelEditDto editedDto) {
        this.editedDto = editedDto;
    }
}