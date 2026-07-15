package edu.tongji.contentservice.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "rate")
@IdClass(RateId.class)
public class Rate implements Serializable {
    @Id
    @Column(name = "novel_id", nullable = false)
    private Long novelId;
    
    @Id
    @Column(name = "reader_id", nullable = false)
    private Long readerId;
    
    @Column(name = "score", nullable = false, precision = 3, scale = 2)
    private BigDecimal rate;
    
    @Column(name = "rating_time")
    private LocalDateTime rateTime;
    
    // 默认构造函数
    public Rate() {}
    
    // 带参数的构造函数
    public Rate(Long novelId, Long readerId, BigDecimal rate, LocalDateTime rateTime) {
        this.novelId = novelId;
        this.readerId = readerId;
        this.rate = rate;
        this.rateTime = rateTime;
    }
}