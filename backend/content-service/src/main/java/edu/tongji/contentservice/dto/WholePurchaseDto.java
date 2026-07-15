package edu.tongji.contentservice.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class WholePurchaseDto {
    
    @NotNull(message = "读者ID不能为空")
    private Long readerId;
    
    @NotNull(message = "小说ID不能为空")
    private Long novelId;
}