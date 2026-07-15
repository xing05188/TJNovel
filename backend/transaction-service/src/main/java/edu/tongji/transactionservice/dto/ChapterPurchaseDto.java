package edu.tongji.transactionservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 章节购买DTO
 */
@Data
public class ChapterPurchaseDto {
    
    @NotNull(message = "读者ID不能为空")
    private Long readerId;
    
    @NotNull(message = "小说ID不能为空")
    private Long novelId;
    
    @NotNull(message = "章节ID不能为空")
    private Long chapterId;
}