package edu.tongji.contentservice.service;

import edu.tongji.contentservice.entity.Chapter;
import edu.tongji.contentservice.repository.ChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final AdminServiceClient adminServiceClient;
    private final NovelService novelService;
    private final NotificationProducer notificationProducer;

    @Autowired(required = false)
    private DataSyncService dataSyncService;

    @Autowired
    public ChapterService(ChapterRepository chapterRepository, 
                          AdminServiceClient adminServiceClient,
                          NovelService novelService,
                          NotificationProducer notificationProducer) {
        this.chapterRepository = chapterRepository;
        this.adminServiceClient = adminServiceClient;
        this.novelService = novelService;
        this.notificationProducer = notificationProducer;
    }

    /**
     * 章节状态变更后同步 ES 索引：仅"已发布"入库，其余状态移除索引
     */
    private void syncChapterIndex(Chapter chapter) {
        if (dataSyncService == null) {
            return; // ES 未启用时跳过
        }
        try {
            if (chapter != null && "已发布".equals(chapter.getStatus())) {
                dataSyncService.syncChapter(chapter.getNovelId(), chapter.getChapterId());
            } else if (chapter != null) {
                dataSyncService.deleteChapter(chapter.getNovelId(), chapter.getChapterId());
            }
        } catch (Exception e) {
            System.err.println("同步章节到 ES 失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有状态为"首次审核"或"审核中"的章节
     * @return 章节列表
     */
    public List<Chapter> getChaptersInReviewStatus() {
        return chapterRepository.findChaptersInReviewStatus();
    }

    /**
     * 获取指定小说下的所有章节
     * @param novelId 小说ID
     * @return 章节列表
     */
    public List<Chapter> getChaptersByNovelId(Long novelId) {
        return chapterRepository.findByNovelIdOrderByChapterId(novelId);
    }

    /**
     * 获取指定小说的所有章节（与上一个方法相同，但API路径不同）
     * @param novelId 小说ID
     * @return 章节列表
     */
    public List<Chapter> getAllChaptersByNovelId(Long novelId) {
        return chapterRepository.findByNovelIdOrderByChapterId(novelId);
    }

    /**
     * 获取指定章节
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @return 章节对象
     */
    public Optional<Chapter> getChapterById(Long novelId, Long chapterId) {
        return Optional.ofNullable(chapterRepository.findByNovelIdAndChapterId(novelId, chapterId));
    }

    /**
     * 添加章节
     * @param chapter 章节对象
     * @return 保存后的章节对象
     */
    public Chapter addChapter(Chapter chapter) {
        // 验证必填字段
        if (chapter.getNovelId() == null) {
            throw new IllegalArgumentException("小说ID不能为空");
        }
        if (chapter.getChapterId() == null) {
            throw new IllegalArgumentException("章节ID不能为空");
        }
        if (chapter.getTitle() == null || chapter.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("章节标题不能为空");
        }
        if (chapter.getWordCount() == null) {
            chapter.setWordCount(0L);
        }
        
        // 检查章节是否已存在
        Chapter existingChapter = chapterRepository.findByNovelIdAndChapterId(
            chapter.getNovelId(), 
            chapter.getChapterId()
        );
        if (existingChapter != null) {
            throw new IllegalArgumentException("章节已存在，小说ID: " + chapter.getNovelId() + ", 章节ID: " + chapter.getChapterId());
        }
        
        // 设置默认值
        if (chapter.getContent() == null) {
            chapter.setContent("");
        }
        if (chapter.getPricePerKilo() == null) {
            chapter.setPricePerKilo(java.math.BigDecimal.valueOf(0.50));
        }
        if (chapter.getIsCharged() == null || chapter.getIsCharged().isEmpty()) {
            chapter.setIsCharged("否");
        }
        if (chapter.getStatus() == null || chapter.getStatus().isEmpty()) {
            chapter.setStatus("草稿");
        }
        
        // 设置发布时间
        if (chapter.getPublishTime() == null) {
            chapter.setPublishTime(new Date());
        }
        
        Chapter saved = chapterRepository.save(chapter);
        
        // 如果是已发布状态，更新小说总字数
        if ("已发布".equals(saved.getStatus())) {
            novelService.updateNovelTotalWordCount(saved.getNovelId());
            notifyNovelUpdate(saved.getNovelId(), saved.getTitle());
        }
        
        // 增量同步章节到 ES（已发布入库，非发布状态移除索引）
        syncChapterIndex(saved);
        
        return saved;
    }

    /**
     * 更新章节
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @param chapter 更新的章节对象
     * @return 更新后的章节对象
     */
    public Optional<Chapter> updateChapter(Long novelId, Long chapterId, Chapter chapter) {
        Optional<Chapter> existingChapter = getChapterById(novelId, chapterId);
        if (existingChapter.isPresent()) {
            Chapter updatedChapter = existingChapter.get();
            
            // 更新字段
            if (chapter.getTitle() != null) {
                updatedChapter.setTitle(chapter.getTitle());
            }
            if (chapter.getContent() != null) {
                updatedChapter.setContent(chapter.getContent());
                // 更新字数
                updatedChapter.setWordCount((long) chapter.getContent().length());
            }
            if (chapter.getWordCount() != null) {
                updatedChapter.setWordCount(chapter.getWordCount());
            }
            if (chapter.getPricePerKilo() != null) {
                updatedChapter.setPricePerKilo(chapter.getPricePerKilo());
            }
            if (chapter.getIsCharged() != null) {
                updatedChapter.setIsCharged(chapter.getIsCharged());
            }
            if (chapter.getStatus() != null) {
                updatedChapter.setStatus(chapter.getStatus());
            }
            
            Chapter saved = chapterRepository.save(updatedChapter);
            
            // 如果是已发布状态，更新小说总字数
            if ("已发布".equals(saved.getStatus())) {
                novelService.updateNovelTotalWordCount(saved.getNovelId());
                notifyNovelUpdate(saved.getNovelId(), saved.getTitle());
            }
            
            // 增量同步章节到 ES（已发布入库，非发布状态移除索引）
            syncChapterIndex(saved);
            
            return Optional.of(saved);
        }
        return Optional.empty();
    }

    /**
     * 删除章节
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @return 是否删除成功
     */
    public boolean deleteChapter(Long novelId, Long chapterId) {
        Optional<Chapter> chapterOpt = getChapterById(novelId, chapterId);
        if (chapterOpt.isPresent()) {
            Chapter chapter = chapterOpt.get();
            chapterRepository.deleteByNovelIdAndChapterId(novelId, chapterId);
            
            // 如果删除的是已发布章节，更新小说总字数
            if ("已发布".equals(chapter.getStatus())) {
                novelService.updateNovelTotalWordCount(novelId);
            }
            
            // 删除章节后同步移除 ES 索引
            if (dataSyncService != null) {
                try {
                    dataSyncService.deleteChapter(novelId, chapterId);
                } catch (Exception e) {
                    System.err.println("删除章节 ES 索引失败: " + e.getMessage());
                }
            }
            return true;
        }
        return false;
    }
    
    /**
     * 获取待审核章节数量
     * @return 待审核章节数量
     */
    public Long getPendingChaptersCount() {
        return chapterRepository.countPendingChapters();
    }
    
    /**
     * 审核章节
     * @param novelId 小说ID
     * @param chapterId 章节ID
     * @param newStatus 新状态（如"已发布"、"封禁"等）
     * @param managerId 管理员ID（可选，为空则不记录管理日志）
     * @param result 审核结果说明（可选）
     * @return 更新后的章节对象
     */
    public Optional<Chapter> reviewChapter(Long novelId, Long chapterId, String newStatus,
                                           Long managerId, String result) {
        Optional<Chapter> chapterOpt = getChapterById(novelId, chapterId);
        if (chapterOpt.isPresent()) {
            Chapter chapter = chapterOpt.get();
            chapter.setStatus(newStatus);
            Chapter saved = chapterRepository.save(chapter);

            // 如果审核状态变为已发布，更新小说总字数
            if ("已发布".equals(newStatus)) {
                novelService.updateNovelTotalWordCount(novelId);
            }
            
            // 审核状态变更后同步 ES 索引（已发布入库，封禁/下架移除索引）
            syncChapterIndex(saved);

            // 如果提供了管理员信息，则记录章节管理操作到 admin-service
            if (managerId != null) {
                try {
                    adminServiceClient.recordChapterManagement(novelId, chapterId, managerId, result)
                            .subscribe(success -> {
                                if (success) {
                                    // 这里简单打个 debug 日志即可，如需要可在 AdminServiceClient 中观察
                                }
                            });
                } catch (Exception e) {
                    // 记录日志但不影响主流程
                    // 使用 System.err 避免引入 Logger，这个类本身已经有日志的话可以改为 logger
                    System.err.println("调用 admin-service 记录章节管理操作时发生异常: " + e.getMessage());
                }
            }

            return Optional.of(saved);
        }
        return Optional.empty();
    }

    /**
     * 章节发布时，通过 RabbitMQ 异步广播"小说更新通知"。
     * 仅取小说标题与作者 ID 用于消息体，失败不影响主流程。
     */
    private void notifyNovelUpdate(Long novelId, String chapterTitle) {
        try {
            novelService.getNovelById(novelId).ifPresent(novel ->
                    notificationProducer.publishNovelUpdate(
                            novelId, novel.getNovelName(), chapterTitle, novel.getAuthorId()));
        } catch (Exception e) {
            // 通知为异步增强能力，异常不应影响章节发布主流程
            System.err.println("发布小说更新通知时发生异常: " + e.getMessage());
        }
    }
}