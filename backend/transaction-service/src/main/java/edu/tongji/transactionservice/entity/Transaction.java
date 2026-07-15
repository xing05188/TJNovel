package edu.tongji.transactionservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "TRANSACTION")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRANSACTION_ID")
    private Long transactionId;
    
    @NotNull
    @Column(name = "READER_ID", nullable = false)
    private Long readerId;
    
    @NotNull
    @Size(max = 10)
    @Column(name = "TRANS_TYPE", nullable = false, length = 10)
    private String transType;
    
    @NotNull
    @Column(name = "AMOUNT", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "TIME", updatable = false)
    private LocalDateTime time;
    
    // 默认构造函数
    public Transaction() {
        this.time = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
    }
    
    // 带参数的构造函数
    public Transaction(Long readerId, String transType, BigDecimal amount) {
        this.readerId = readerId;
        this.transType = transType;
        this.amount = amount;
        this.time = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
    }
    
    // 在保存前自动设置时间（如果未设置）
    @PrePersist
    protected void onCreate() {
        if (this.time == null) {
            this.time = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        }
    }
    
    // Getters and Setters
    public Long getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
    
    public Long getReaderId() {
        return readerId;
    }
    
    public void setReaderId(Long readerId) {
        this.readerId = readerId;
    }
    
    public String getTransType() {
        return transType;
    }
    
    public void setTransType(String transType) {
        this.transType = transType;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public LocalDateTime getTime() {
        return time;
    }
    
    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}