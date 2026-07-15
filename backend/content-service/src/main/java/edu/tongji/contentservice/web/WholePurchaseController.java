package edu.tongji.contentservice.web;

import edu.tongji.common.dto.ApiResponse;
import edu.tongji.contentservice.dto.WholePurchaseDto;
import edu.tongji.contentservice.entity.WholePurchase;
import edu.tongji.contentservice.service.WholePurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wholepurchase")
@Tag(name = "WholePurchaseAPI", description = "整本购买相关API")
public class WholePurchaseController {
    
    @Autowired
    private WholePurchaseService wholePurchaseService;
    
    @PostMapping
    @Operation(summary = "整本小说买断", description = "读者整本购买小说")
    public ResponseEntity<ApiResponse<WholePurchase>> purchaseNovel(
            @Parameter(description = "购买请求信息") @RequestBody WholePurchaseDto wholePurchaseDto) {
        try {
            WholePurchase purchase = wholePurchaseService.purchaseNovel(wholePurchaseDto);
            return ResponseEntity.ok(ApiResponse.ok("content-service", purchase));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("content-service", e.getMessage()));
        }
    }
    
    @GetMapping("/status")
    @Operation(summary = "查询购买状态", description = "查询读者是否已整本买断某小说")
    public ResponseEntity<ApiResponse<Boolean>> checkPurchaseStatus(
            @Parameter(description = "读者ID") @RequestParam Long readerId,
            @Parameter(description = "小说ID") @RequestParam Long novelId) {
        try {
            boolean isPurchased = wholePurchaseService.checkPurchaseStatus(readerId, novelId);
            return ResponseEntity.ok(ApiResponse.ok("content-service", isPurchased));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("content-service", "查询购买状态失败：" + e.getMessage()));
        }
    }
}