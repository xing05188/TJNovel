package edu.tongji.transactionservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "PURCHASE")
public class Purchase {
    
    @Id
    @Column(name = "TRANSACTION_ID")
    private Long transactionId;
    
    @NotNull
    @Column(name = "CHAPTER_ID", nullable = false)
    private Long chapterId;
    
    @NotNull
    @Column(name = "NOVEL_ID", nullable = false)
    private Long novelId;
    
    // 与Transaction实体的一对一关系（通过相同的 TRANSACTION_ID 关联，但不作为共享主键映射）
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSACTION_ID", insertable = false, updatable = false)
    private Transaction transaction;
    
    // 默认构造函数
    public Purchase() {
    }
    
    // 带参数的构造函数
    public Purchase(Long chapterId, Long novelId) {
        this.chapterId = chapterId;
        this.novelId = novelId;
    }
    
    // Getters and Setters
    public Long getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
    
    public Long getChapterId() {
        return chapterId;
    }
    
    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
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