package edu.tongji.contentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 创建评论请求DTO
 */
@Schema(description = "创建评论请求")
public class CommentCreateRequest {
    
    @Schema(description = "读者ID", example = "1", required = true)
    private Long readerId;
    
    @Schema(description = "小说ID", example = "1", required = true)
    private Long novelId;
    
    @Schema(description = "章节ID", example = "1", required = true)
    private Long chapterId;
    
    @Schema(description = "评论标题", example = "很好的章节", required = true)
    private String title;
    
    @Schema(description = "评论内容", example = "这个章节写得很好，情节紧凑", required = true)
    private String content;
    
    @Schema(description = "状态", example = "通过")
    private String status;
    
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
    
    public Long getChapterId() {
        return chapterId;
    }
    
    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}