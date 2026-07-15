package edu.tongji.contentservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "CHAPTER")
@IdClass(ChapterId.class)
public class Chapter implements Serializable {
    @Id
    @Column(name = "NOVEL_ID")
    private Long novelId;

    @Id
    @Column(name = "CHAPTER_ID")
    private Long chapterId;

    @Column(name = "TITLE", length = 40, nullable = false)
    private String title;

    @Column(name = "CONTENT", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "WORD_COUNT", nullable = false)
    private Long wordCount;

    @Column(name = "PRICE_PER_KILO", precision = 10, scale = 2)
    private BigDecimal pricePerKilo = BigDecimal.valueOf(0.50);

    @Transient
    private BigDecimal calculatedPrice;

    @Column(name = "IS_CHARGED", length = 2)
    private String isCharged = "否";

    @Column(name = "PUBLISH_TIME")
    private Date publishTime;

    @Column(name = "STATUS", length = 10)
    private String status = "草稿";

    // Getter for calculatedPrice
    public BigDecimal getCalculatedPrice() {
        if (wordCount == null || pricePerKilo == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(wordCount / 1000.0)
                .multiply(pricePerKilo)
                .setScale(0, BigDecimal.ROUND_HALF_UP);
    }

    // Setter for calculatedPrice (needed for JSON serialization)
    public void setCalculatedPrice(BigDecimal calculatedPrice) {
        this.calculatedPrice = calculatedPrice;
    }
}