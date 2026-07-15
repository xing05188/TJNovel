package edu.tongji.contentservice.service;

import edu.tongji.contentservice.dto.CommentManagementRequest;
import edu.tongji.contentservice.dto.NovelManagementRequest;
import edu.tongji.contentservice.dto.ReportManagementRequest;
import edu.tongji.contentservice.dto.ChapterManagementRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Admin服务调用类
 */
@Service
public class AdminServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminServiceClient.class);
    
    private final WebClient adminServiceWebClient;
    
    @Autowired
    public AdminServiceClient(WebClient adminServiceWebClient) {
        this.adminServiceWebClient = adminServiceWebClient;
    }
    
    /**
     * 记录评论管理操作
     * @param commentId 评论ID
     * @param managerId 管理员ID
     * @param result 操作结果
     * @return 是否成功
     */
    public Mono<Boolean> recordCommentManagement(Long commentId, Long managerId, String result) {
        CommentManagementRequest request = new CommentManagementRequest();
        request.setCommentId(commentId);
        request.setManagerId(managerId);
        request.setResult(result);
        
        logger.info("记录评论管理操作: commentId={}, managerId={}, result={}", 
                   commentId, managerId, result);
        
        return adminServiceWebClient.post()
                .uri("/CommentManagement") // 基础URL已经包含了/api前缀
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .onErrorResume(throwable -> {
                    logger.error("记录评论管理操作失败: {}", throwable.getMessage(), throwable);
                    return Mono.just(false);
                });
    }
    
    /**
     * 记录小说管理操作
     * @param novelId 小说ID
     * @param managerId 管理员ID
     * @param result 操作结果
     * @return 是否成功
     */
    public Mono<Boolean> recordNovelManagement(Long novelId, Long managerId, String result) {
        NovelManagementRequest request = new NovelManagementRequest();
        request.setNovelId(novelId);
        request.setManagerId(managerId);
        request.setResult(result);
        
        logger.info("记录小说管理操作: novelId={}, managerId={}, result={}", 
                   novelId, managerId, result);
        
        return adminServiceWebClient.post()
                .uri("/NovelManagement") // 基础URL已经包含了/api前缀
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .onErrorResume(throwable -> {
                    logger.error("记录小说管理操作失败: {}", throwable.getMessage(), throwable);
                    return Mono.just(false);
                });
    }
    
    /**
     * 记录举报管理操作
     * @param reportId 举报ID
     * @param managerId 管理员ID
     * @param result 操作结果
     * @return 是否成功
     */
    public Mono<Boolean> recordReportManagement(Long reportId, Long managerId, String result) {
        ReportManagementRequest request = new ReportManagementRequest();
        request.setReportId(reportId);
        request.setManagerId(managerId);
        request.setResult(result);
        
        logger.info("记录举报管理操作: reportId={}, managerId={}, result={}", 
                   reportId, managerId, result);
        
        return adminServiceWebClient.post()
                .uri("/ReportManagement") // 基础URL已经包含了/api前缀
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .onErrorResume(throwable -> {
                    logger.error("记录举报管理操作失败: {}", throwable.getMessage(), throwable);
                    return Mono.just(false);
                });
    }

    /**
     * 记录章节管理操作
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @param managerId 管理员ID
     * @param result 操作结果
     * @return 是否成功
     */
    public Mono<Boolean> recordChapterManagement(Long novelId, Long chapterId, Long managerId, String result) {
        ChapterManagementRequest request = new ChapterManagementRequest();
        request.setNovelId(novelId);
        request.setChapterId(chapterId);
        request.setManagerId(managerId);
        request.setResult(result);

        logger.info("记录章节管理操作: novelId={}, chapterId={}, managerId={}, result={}",
                novelId, chapterId, managerId, result);

        return adminServiceWebClient.post()
                .uri("/ChapterManagement") // 基础URL已经包含了/api前缀
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .onErrorResume(throwable -> {
                    logger.error("记录章节管理操作失败: {}", throwable.getMessage(), throwable);
                    return Mono.just(false);
                });
    }
}