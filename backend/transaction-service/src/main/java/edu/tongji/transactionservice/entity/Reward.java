package edu.tongji.transactionservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "REWARD")
public class Reward {
    
    @Id
    @Column(name = "TRANSACTION_ID")
    private Long transactionId;
    
    @NotNull
    @Column(name = "NOVEL_ID", nullable = false)
    private Long novelId;
    
    // 与Transaction实体的一对一关系（通过 TRANSACTION_ID 关联，避免共享主键带来的复杂性）
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSACTION_ID", insertable = false, updatable = false)
    private Transaction transaction;
    
    // 默认构造函数
    public Reward() {
    }
    
    // 带参数的构造函数
    public Reward(Long novelId) {
        this.novelId = novelId;
    }
    
    // Getters and Setters
    public Long getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
    
    public Long getNovelId() {
        return novelId;
    }
    
    public void setNovelId(Long novelId) {
        this.novelId = novelId;
    }
    
    public Transaction getTransaction() {
        return transaction;
    }
    
    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}