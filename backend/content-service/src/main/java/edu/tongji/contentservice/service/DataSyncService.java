package edu.tongji.contentservice.service;

import edu.tongji.contentservice.document.ChapterDocument;
import edu.tongji.contentservice.document.NovelDocument;
import edu.tongji.contentservice.entity.Category;
import edu.tongji.contentservice.entity.Chapter;
import edu.tongji.contentservice.entity.Novel;
import edu.tongji.contentservice.entity.NovelCategory;
import edu.tongji.contentservice.repository.ChapterSearchRepository;
import edu.tongji.contentservice.repository.ChapterRepository;
import edu.tongji.contentservice.repository.NovelCategoryRepository;
import edu.tongji.contentservice.repository.NovelRepository;
import edu.tongji.contentservice.repository.NovelSearchRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MySQL → Elasticsearch 数据同步服务
 * 使用异步消息方式（推荐方案），通过 RabbitMQ 触发同步
 */
@Service
@ConditionalOnProperty(prefix = "spring.elasticsearch", name = "uris")
public class DataSyncService {

    private static final Logger logger = LoggerFactory.getLogger(DataSyncService.class);

    private final NovelRepository novelRepository;
    private final ChapterRepository chapterRepository;
    private final NovelCategoryRepository novelCategoryRepository;
    private final NovelSearchRepository novelSearchRepository;
    private final ChapterSearchRepository chapterSearchRepository;

    @Autowired
    public DataSyncService(NovelRepository novelRepository,
                           ChapterRepository chapterRepository,
                           NovelCategoryRepository novelCategoryRepository,
                           NovelSearchRepository novelSearchRepository,
                           ChapterSearchRepository chapterSearchRepository) {
        this.novelRepository = novelRepository;
        this.chapterRepository = chapterRepository;
        this.novelCategoryRepository = novelCategoryRepository;
        this.novelSearchRepository = novelSearchRepository;
        this.chapterSearchRepository = chapterSearchRepository;
    }

    /**
     * 启动时全量同步（可选，生产环境建议通过消息队列触发）
     */
    @PostConstruct
    public void initSync() {
        logger.info("开始全量同步 MySQL 数据到 Elasticsearch...");
        try {
            syncAllNovels();
            logger.info("全量同步完成");
        } catch (Exception e) {
            logger.warn("全量同步失败（ES 可能未就绪）: {}", e.getMessage());
        }
    }

    /**
     * 全量同步所有小说
     */
    public void syncAllNovels() {
        List<Novel> novels = novelRepository.findAllPublishedNovels();
        List<NovelDocument> documents = novels.stream().map(this::convertToDocument).collect(Collectors.toList());
        novelSearchRepository.saveAll(documents);
        logger.info("同步 {} 部小说到 ES", documents.size());
    }

    /**
     * 同步单部小说
     */
    public void syncNovel(Long novelId) {
        Optional<Novel> novelOpt = novelRepository.findById(novelId);
        if (novelOpt.isPresent()) {
            NovelDocument document = convertToDocument(novelOpt.get());
            novelSearchRepository.save(document);
            logger.info("同步小说 {} 到 ES", novelId);
        }
    }

    /**
     * 删除小说索引
     */
    public void deleteNovel(Long novelId) {
        novelSearchRepository.deleteById(novelId);
        logger.info("从 ES 删除小说 {}", novelId);
    }

    /**
     * 同步章节
     */
    public void syncChapter(Long novelId, Long chapterId) {
        Chapter chapter = chapterRepository.findByNovelIdAndChapterId(novelId, chapterId);
        if (chapter != null) {
            ChapterDocument document = new ChapterDocument();
            document.setId(novelId + "_" + chapterId);
            document.setNovelId(novelId);
            document.setChapterId(chapterId);
            document.setTitle(chapter.getTitle());
            document.setContent(chapter.getContent());
            document.setWordCount(chapter.getWordCount());
            document.setStatus(chapter.getStatus());
            document.setPublishTime(chapter.getPublishTime());
            chapterSearchRepository.save(document);
            logger.info("同步章节 {}:{} 到 ES", novelId, chapterId);
        }
    }

    /**
     * 删除章节索引
     */
    public void deleteChapter(Long novelId, Long chapterId) {
        chapterSearchRepository.deleteById(novelId + "_" + chapterId);
    }

    /**
     * 将 Novel 实体转换为 NovelDocument
     */
    private NovelDocument convertToDocument(Novel novel) {
        NovelDocument doc = new NovelDocument();
        doc.setNovelId(novel.getNovelId());
        doc.setAuthorId(novel.getAuthorId());
        doc.setNovelName(novel.getNovelName());
        doc.setIntroduction(novel.getIntroduction());
        doc.setCoverUrl(novel.getCoverUrl());
        doc.setStatus(novel.getStatus());
        doc.setScore(novel.getScore());
        doc.setTotalWordCount(novel.getTotalWordCount());
        doc.setRecommendCount(novel.getRecommendCount());
        doc.setCollectedCount(novel.getCollectedCount());
        doc.setCreateTime(novel.getCreateTime());

        // 获取分类名称
        try {
            List<NovelCategory> categories = novelCategoryRepository.findByNovelId(novel.getNovelId());
            if (!categories.isEmpty()) {
                doc.setCategoryName(categories.get(0).getCategoryName());
            }
        } catch (Exception e) {
            logger.warn("获取小说 {} 分类失败: {}", novel.getNovelId(), e.getMessage());
        }

        return doc;
    }
}