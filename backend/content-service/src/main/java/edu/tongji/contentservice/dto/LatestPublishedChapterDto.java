package edu.tongji.contentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 最新已发布章节信息DTO
 */
@Data
@Schema(description = "最新已发布章节信息")
public class LatestPublishedChapterDto {
    
    @Schema(description = "章节ID")
    private Long chapterId;
    
    @Schema(description = "章节标题")
    private String title;
    
    @Schema(description = "发布时间")
    private Date publishTime;
}