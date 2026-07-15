package edu.tongji.contentservice.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 小说实体类
 */
@Entity
@Table(name = "NOVEL")
public class Novel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOVEL_ID")
    private Long novelId;

    @Column(name = "AUTHOR_ID", nullable = false)
    private Long authorId;

    @Column(name = "NOVEL_NAME", nullable = false, length = 40)
    private String novelName;

    @Column(name = "INTRODUCTION", columnDefinition = "TEXT")
    private String introduction;

    @Column(name = "CREATE_TIME", updatable = false)
    private Date createTime;

    @Column(name = "COVER_URL", length = 255)
    private String coverUrl;

    @Column(name = "SCORE", precision = 3, scale = 1)
    private BigDecimal score = BigDecimal.ZERO;

    @Column(name = "TOTAL_WORD_COUNT")
    private Long totalWordCount = 0L;

    @Column(name = "RECOMMEND_COUNT")
    private Integer recommendCount = 0;

    @Column(name = "COLLECTED_COUNT")
    private Integer collectedCount = 0;

    @Column(name = "STATUS", length = 10)
    private String status = "待审核"; // "待审核"/"连载"/"完结"/"封禁"

    @Column(name = "ORIGINAL_NOVEL_ID", nullable = false)
    private Long originalNovelId = -1L;

    @Column(name = "TOTAL_PRICE", precision = 10, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO;



    // Getters and Setters
    public Long getNovelId() {
        return novelId;
    }

    public void setNovelId(Long novelId) {
        this.novelId = novelId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getNovelName() {
        return novelName;
    }

    public void setNovelName(String novelName) {
        this.novelName = novelName;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public Long getTotalWordCount() {
        return totalWordCount;
    }

    public void setTotalWordCount(Long totalWordCount) {
        this.totalWordCount = totalWordCount;
    }

    public Integer getRecommendCount() {
        return recommendCount;
    }

    public void setRecommendCount(Integer recommendCount) {
        this.recommendCount = recommendCount;
    }

    public Integer getCollectedCount() {
        return collectedCount;
    }

    public void setCollectedCount(Integer collectedCount) {
        this.collectedCount = collectedCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOriginalNovelId() {
        return originalNovelId;
    }

    public void setOriginalNovelId(Long originalNovelId) {
        this.originalNovelId = originalNovelId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

}
