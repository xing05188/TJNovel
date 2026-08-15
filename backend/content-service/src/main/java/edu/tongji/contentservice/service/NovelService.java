package edu.tongji.contentservice.service;

import edu.tongji.common.service.StorageService;
import edu.tongji.contentservice.dto.LatestPublishedChapterDto;
import edu.tongji.contentservice.dto.NovelCreateDto;
import edu.tongji.contentservice.dto.NovelEditDto;
import edu.tongji.contentservice.entity.Chapter;
import edu.tongji.contentservice.entity.Novel;
import edu.tongji.contentservice.repository.ChapterRepository;
import edu.tongji.contentservice.repository.NovelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class NovelService {

    private static final Logger logger = LoggerFactory.getLogger(NovelService.class);

    private final NovelRepository novelRepository;
    private final ChapterRepository chapterRepository;
    private final StorageService storageService;
    private final AdminServiceClient adminServiceClient;
    private final NotificationProducer notificationProducer;

    @Autowired(required = false)
    private DataSyncService dataSyncService;

    @Autowired
    public NovelService(NovelRepository novelRepository, ChapterRepository chapterRepository, StorageService storageService, AdminServiceClient adminServiceClient, NotificationProducer notificationProducer) {
        this.novelRepository = novelRepository;
        this.chapterRepository = chapterRepository;
        this.storageService = storageService;
        this.adminServiceClient = adminServiceClient;
        this.notificationProducer = notificationProducer;
    }

    /**
     * 小说数据变更后同步 ES 索引：仅"连载/完结"入库，其余状态移除索引
     */
    private void syncNovelIndex(Novel novel) {
        if (dataSyncService == null || novel == null || novel.getNovelId() == null) {
            return;
        }
        try {
            String status = novel.getStatus();
            if ("连载".equals(status) || "完结".equals(status)) {
                dataSyncService.syncNovel(novel.getNovelId());
            } else {
                dataSyncService.deleteNovel(novel.getNovelId());
            }
        } catch (Exception e) {
            logger.error("同步小说到 ES 失败: novelId={}, 原因: {}", novel.getNovelId(), e.getMessage());
        }
    }

    public List<Novel> getAllNovels() {
        return novelRepository.findAll();
    }

    @Cacheable(value = "novel", key = "#id", unless = "#result == null")
    public Optional<Novel> getNovelById(Long id) {
        return novelRepository.findById(id);
    }

    @CacheEvict(value = "publishedNovels", allEntries = true)
    public Novel createNovel(Novel novel) {
        novel.setCreateTime(new Date());
        return novelRepository.save(novel);
    }

    @CacheEvict(value = "novel", key = "#id")
    public Novel updateNovel(Long id, Novel novelDetails) {
        Optional<Novel> optionalNovel = novelRepository.findById(id);
        if (optionalNovel.isPresent()) {
            Novel novel = optionalNovel.get();
            
            if (novelDetails.getNovelName() != null) {
                novel.setNovelName(novelDetails.getNovelName());
            }
            if (novelDetails.getIntroduction() != null) {
                novel.setIntroduction(novelDetails.getIntroduction());
            }
            if (novelDetails.getCoverUrl() != null) {
                novel.setCoverUrl(novelDetails.getCoverUrl());
            }
            if (novelDetails.getScore() != null) {
                novel.setScore(novelDetails.getScore());
            }
            if (novelDetails.getTotalWordCount() != null) {
                novel.setTotalWordCount(novelDetails.getTotalWordCount());
            }
            if (novelDetails.getRecommendCount() != null) {
                novel.setRecommendCount(novelDetails.getRecommendCount());
            }
            if (novelDetails.getCollectedCount() != null) {
                novel.setCollectedCount(novelDetails.getCollectedCount());
            }
            if (novelDetails.getStatus() != null) {
                novel.setStatus(novelDetails.getStatus());
            }
            if (novelDetails.getTotalPrice() != null) {
                novel.setTotalPrice(novelDetails.getTotalPrice());
            }
            
            return novelRepository.save(novel);
        }
        return null;
    }

    @CacheEvict(value = "novel", key = "#id")
    public boolean deleteNovel(Long id) {
        if (novelRepository.existsById(id)) {
            novelRepository.deleteById(id);
            // 删除小说时同步移除 ES 索引
            if (dataSyncService != null) {
                try {
                    dataSyncService.deleteNovel(id);
                } catch (Exception e) {
                    logger.error("删除小说 ES 索引失败: novelId={}, 原因: {}", id, e.getMessage());
                }
            }
            return true;
        }
        return false;
    }

    public Novel createNovelFromDto(NovelCreateDto novelCreateDto) {
        Novel novel = new Novel();
        novel.setAuthorId(novelCreateDto.getAuthorId());
        novel.setNovelName(novelCreateDto.getNovelName());
        novel.setIntroduction(novelCreateDto.getIntroduction());
        novel.setCreateTime(new Date());
        novel.setStatus("待审核");
        novel.setScore(BigDecimal.ZERO);
        novel.setTotalWordCount(0L);
        novel.setRecommendCount(0);
        novel.setCollectedCount(0);
        novel.setTotalPrice(BigDecimal.ZERO);
        novel.setOriginalNovelId(-1L);
        
        return novelRepository.save(novel);
    }

    /**
     * 审核小说逻辑：
     * - 审核对象可能是原稿或副本（通过 originalNovelId 判断，-1 表示原稿）
     * - 审核通过（newStatus = "连载"）：
     *   - 副本：将内容同步到原稿，删除副本，原稿状态改为“连载”
     *   - 原稿：直接更新状态为“连载”
     * - 审核不通过（newStatus = "封禁"）：
     *   - 副本：删除副本，原稿不变
     *   - 原稿：直接更新为“封禁”
     * - 其他状态：默认直接写到当前记录
     * - 所有审核操作均记录管理日志（日志绑定到“原稿”小说上）
     */
    @Caching(evict = {
            @CacheEvict(value = "novel", key = "#id"),
            @CacheEvict(value = "publishedNovels", allEntries = true)
    })
    public boolean reviewNovel(Long id, String newStatus, Long managerId, String result) {
        Optional<Novel> optionalNovel = novelRepository.findById(id);
        if (optionalNovel.isEmpty()) {
            return false;
        }

        Novel target = optionalNovel.get(); // 可能是原稿，也可能是副本
        boolean isCopy = target.getOriginalNovelId() != null && target.getOriginalNovelId() != -1L;

        // 找到对应的“原稿”小说（日志与最终展示都绑定到它）
        Novel original = target;
        if (isCopy) {
            Optional<Novel> originalOpt = novelRepository.findById(target.getOriginalNovelId());
            if (originalOpt.isPresent()) {
                original = originalOpt.get();
            } else {
                // 找不到原稿时，降级处理：把当前当作原稿
                isCopy = false;
                target.setOriginalNovelId(-1L);
                original = target;
            }
        }

        // 审核状态分支
        if ("连载".equals(newStatus)) {
            if (isCopy) {
                // 副本审核通过：同步内容到原稿，删除副本，原稿状态“连载”
                original.setNovelName(target.getNovelName());
                original.setIntroduction(target.getIntroduction());
                original.setCoverUrl(target.getCoverUrl());
                if (target.getTotalPrice() != null) {
                    original.setTotalPrice(target.getTotalPrice());
                }
                original.setStatus("连载");
                novelRepository.save(original);
                novelRepository.delete(target);
            } else {
                // 原稿审核通过：直接更新状态
                original.setStatus("连载");
                novelRepository.save(original);
            }
        } else if ("封禁".equals(newStatus)) {
            if (isCopy) {
                // 副本审核不通过：删除副本，原稿不变
                novelRepository.delete(target);
            } else {
                // 原稿审核不通过：直接封禁
                original.setStatus("封禁");
                novelRepository.save(original);
            }
        } else {
            // 其他状态：保守起见，直接写到当前记录
            target.setStatus(newStatus);
            novelRepository.save(target);
        }

        // 记录管理操作到 admin-service（日志始终绑定到原稿 novelId）
        if (managerId != null) {
            Long logNovelId = (original != null && original.getNovelId() != null)
                    ? original.getNovelId()
                    : id;
            try {
                adminServiceClient.recordNovelManagement(logNovelId, managerId, result)
                        .subscribe(success -> {
                            if (success) {
                                logger.info("成功记录小说管理操作: novelId={}, managerId={}", logNovelId, managerId);
                            } else {
                                logger.warn("记录小说管理操作失败: novelId={}, managerId={}", logNovelId, managerId);
                            }
                        });
            } catch (Exception e) {
                logger.error("调用admin-service记录小说管理操作时发生异常: {}", e.getMessage(), e);
                // 不抛出异常，避免影响主要业务流程
            }
        }

        // 审核结果异步推送给作者（通过 RabbitMQ）
        try {
            boolean passed = "连载".equals(newStatus);
            Long authorId = (original != null && original.getAuthorId() != null)
                    ? original.getAuthorId()
                    : target.getAuthorId();
            String novelName = (original != null && original.getNovelName() != null)
                    ? original.getNovelName()
                    : target.getNovelName();
            notificationProducer.publishAuditResult(authorId, novelName, passed, result);
        } catch (Exception e) {
            logger.error("发布审核结果推送时发生异常: {}", e.getMessage(), e);
        }

        // 审核后同步 ES 索引（连载入库，封禁/其他状态移除索引）
        if (original != null && original.getNovelId() != null) {
            syncNovelIndex(original);
        }

        return true;
    }

    public String uploadNovelCover(Long novelId, MultipartFile coverFile) {
        Optional<Novel> optionalNovel = novelRepository.findById(novelId);
        if (optionalNovel.isPresent()) {
            Novel novel = optionalNovel.get();
            String coverUrl = storageService.uploadFile(coverFile, "covers");
            novel.setCoverUrl(coverUrl);
            novelRepository.save(novel);
            // 封面上传后同步 ES 索引（连载/完结入库）
            syncNovelIndex(novel);
            return coverUrl;
        }
        return null;
    }

    /**
     * 作者修改小说逻辑：
     * 1）修改“连载/完结”的已发布小说：
     *   - 仅修改状态（没有改名称/简介/封面/价格）→ 直接更新原稿状态；
     *   - 修改内容（名称/简介/封面/总价任意一项）→ 创建副本（status=待审核，originalNovelId=原稿ID），原稿保持正常展示；
     * 2）修改“待审核/封禁”小说 → 直接修改当前记录，状态强制为“待审核”。
     *
     * 返回值说明：
     * - 对于已发布小说的“内容修改”，返回原稿（保持原样），前端只收到“已提交审核”的提示；
     * - 其他情况返回被修改的那一条记录。
     */
    @CacheEvict(value = "novel", key = "#originalNovelId")
    public Novel editNovel(Long originalNovelId, NovelEditDto editedDto) {
        Optional<Novel> optionalNovel = novelRepository.findById(originalNovelId);
        if (optionalNovel.isEmpty()) {
            return null;
        }

        Novel novel = optionalNovel.get(); // 当前这条记录（可能是原稿，也可能是待审核/封禁稿）
        String currentStatus = novel.getStatus();

        // 判断本次是否有“内容修改”
        boolean changeName = editedDto.getNovelName() != null;
        boolean changeIntro = editedDto.getIntroduction() != null;
        boolean changeCover = editedDto.getCoverUrl() != null;
        boolean changePrice = editedDto.getTotalPrice() != null;
        boolean hasContentChange = changeName || changeIntro || changeCover || changePrice;

        boolean isPublished = "连载".equals(currentStatus) || "完结".equals(currentStatus);
        boolean isPendingOrBanned = "待审核".equals(currentStatus) || "封禁".equals(currentStatus);

        if (isPublished) {
            // 已发布小说
            if (!hasContentChange && editedDto.getStatus() != null) {
                // 仅修改状态 -> 直接更新原稿状态
                novel.setStatus(editedDto.getStatus());
                Novel saved = novelRepository.save(novel);
                // 状态变化（连载->完结等）后同步 ES 索引
                syncNovelIndex(saved);
                return saved;
            } else if (hasContentChange) {
                // 修改内容 -> 创建副本，副本进入“待审核”，原稿保持展示
                Novel copy = new Novel();
                copy.setAuthorId(novel.getAuthorId());
                copy.setNovelName(changeName ? editedDto.getNovelName() : novel.getNovelName());
                copy.setIntroduction(changeIntro ? editedDto.getIntroduction() : novel.getIntroduction());
                copy.setCoverUrl(changeCover ? editedDto.getCoverUrl() : novel.getCoverUrl());
                copy.setCreateTime(new Date());
                copy.setStatus("待审核");
                copy.setOriginalNovelId(novel.getNovelId());
                // 分数、统计等保持与原稿一致，避免影响前台展示逻辑
                copy.setScore(novel.getScore());
                copy.setTotalWordCount(novel.getTotalWordCount());
                copy.setRecommendCount(novel.getRecommendCount());
                copy.setCollectedCount(novel.getCollectedCount());
                if (changePrice) {
                    copy.setTotalPrice(BigDecimal.valueOf(editedDto.getTotalPrice()));
                } else {
                    copy.setTotalPrice(novel.getTotalPrice());
                }

                novelRepository.save(copy);
                // 返回原稿本身（前端继续展示原稿信息），由审核通过后再同步
                return novel;
            } else {
                // 既没改内容，也没改状态，直接返回原稿
                return novel;
            }
        } else if (isPendingOrBanned) {
            // 修改“待审核/封禁”小说：直接改当前记录，状态统一变为“待审核”
            if (changeName) {
                novel.setNovelName(editedDto.getNovelName());
            }
            if (changeIntro) {
                novel.setIntroduction(editedDto.getIntroduction());
            }
            if (changeCover) {
                novel.setCoverUrl(editedDto.getCoverUrl());
            }
            if (changePrice) {
                novel.setTotalPrice(BigDecimal.valueOf(editedDto.getTotalPrice()));
            }
            novel.setStatus("待审核");
            return novelRepository.save(novel);
        } else {
            // 其他状态（理论上不会很多），保持原有简单更新逻辑
            if (changeName) {
                novel.setNovelName(editedDto.getNovelName());
            }
            if (changeIntro) {
                novel.setIntroduction(editedDto.getIntroduction());
            }
            if (changeCover) {
                novel.setCoverUrl(editedDto.getCoverUrl());
            }
            if (editedDto.getStatus() != null) {
                novel.setStatus(editedDto.getStatus());
            }
            if (changePrice) {
                novel.setTotalPrice(BigDecimal.valueOf(editedDto.getTotalPrice()));
            }
            return novelRepository.save(novel);
        }
    }

    /**
     * 通过小说名模糊搜索小说
     * @param keyword 关键词
     * @return 匹配的小说列表
     */
    public List<Novel> searchNovelsByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        
        logger.info("通过关键词搜索小说: {}", keyword);
        return novelRepository.findByNovelNameContaining(keyword.trim());
    }
    
    /**
     * 获取小说总数
     * @return 小说总数
     */
    public Long getTotalNovelsCount() {
        return novelRepository.countTotalNovels();
    }
    
    /**
     * 获取待审核小说数量
     * @return 待审核小说数量
     */
    public Long getPendingNovelsCount() {
        return novelRepository.countPendingNovels();
    }
    
    /**
     * 根据作者ID获取小说数量
     * @param authorId 作者ID
     * @return 小说数量
     */
    public Long getNovelCountByAuthorId(Long authorId) {
        return novelRepository.countByAuthorId(authorId);
    }
    
    /**
     * 根据作者ID获取所有小说
     * @param authorId 作者ID
     * @return 小说列表
     */
    public List<Novel> getNovelsByAuthorId(Long authorId) {
        return novelRepository.findByAuthorId(authorId);
    }
    
    /**
     * 根据作者ID计算总字数
     * @param authorId 作者ID
     * @return 总字数
     */
    public Long getTotalWordCountByAuthorId(Long authorId) {
        return novelRepository.sumWordCountByAuthorId(authorId);
    }
    
    /**
     * 获取小说总字数
     * @param novelId 小说ID
     * @return 小说总字数
     */
    public Long getWordCountByNovelId(Long novelId) {
        return novelRepository.getWordCountByNovelId(novelId);
    }
    
    /**
     * 获取小说推荐数
     * @param novelId 小说ID
     * @return 小说推荐数
     */
    public Integer getRecommendCountByNovelId(Long novelId) {
        return novelRepository.getRecommendCountByNovelId(novelId);
    }
    
    /**
     * 获取小说收藏数
     * @param novelId 小说ID
     * @return 小说收藏数
     */
    public Integer getCollectCountByNovelId(Long novelId) {
        return novelRepository.getCollectCountByNovelId(novelId);
    }
    
    /**
     * 获取小说最新已发布章节信息
     * @param novelId 小说ID
     * @return 最新已发布章节信息
     */
    public LatestPublishedChapterDto getLatestPublishedChapter(Long novelId) {
        List<Chapter> chapters = chapterRepository.findLatestPublishedChapter(novelId);
        if (chapters.isEmpty()) {
            return null;
        }
        
        Chapter latestChapter = chapters.get(0);
        LatestPublishedChapterDto dto = new LatestPublishedChapterDto();
        dto.setChapterId(latestChapter.getChapterId());
        dto.setTitle(latestChapter.getTitle());
        dto.setPublishTime(latestChapter.getPublishTime());
        
        return dto;
    }
    
    /**
     * 获取所有已发布的小说
     * @return 已发布小说列表
     */
    @Cacheable(value = "publishedNovels")
    public List<Novel> getAllPublishedNovels() {
        return novelRepository.findAllPublishedNovels();
    }
    
    /**
     * 分页获取已发布小说，按novelID顺序
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @return 已发布小说分页结果
     */
    public Page<Novel> getPublishedNovelsOrderById(int page, int pageSize) {
        // 将从1开始的页码转换为从0开始的页码
        int zeroBasedPage = page > 0 ? page - 1 : 0;
        Pageable pageable = PageRequest.of(zeroBasedPage, pageSize);
        return novelRepository.findPublishedNovelsOrderById(pageable);
    }
    
    /**
     * 分页获取已发布小说，按NovelId顺序，支持条件筛选
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @param category 分类筛选
     * @param minWordCount 最小字数筛选
     * @param maxWordCount 最大字数筛选
     * @param isFinished 是否完结筛选
     * @return 已发布小说分页结果
     */
    public Page<Novel> getPublishedNovelsWithFilters(int page, int pageSize, String category, 
                                                     Long minWordCount, Long maxWordCount, Boolean isFinished) {
        // 将从1开始的页码转换为从0开始的页码
        int zeroBasedPage = page > 0 ? page - 1 : 0;
        Pageable pageable = PageRequest.of(zeroBasedPage, pageSize);
        return novelRepository.findPublishedNovelsWithFilters(category, minWordCount, maxWordCount, isFinished, pageable);
    }

    /**
     * 更新小说的总字数
     * @param novelId 小说ID
     */
    public void updateNovelTotalWordCount(Long novelId) {
        Long totalWordCount = chapterRepository.sumWordCountByNovelId(novelId);
        Optional<Novel> novelOpt = novelRepository.findById(novelId);
        if (novelOpt.isPresent()) {
            Novel novel = novelOpt.get();
            novel.setTotalWordCount(totalWordCount);
            novelRepository.save(novel);
            logger.info("更新小说总字数成功: novelId={}, totalWordCount={}", novelId, totalWordCount);
            // 字数变化后同步 ES 索引（连载/完结入库）
            syncNovelIndex(novel);
        }
    }
}