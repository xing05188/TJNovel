package edu.tongji.transactionservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "AUTHOR_INCOME")
public class AuthorIncome {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    
    @NotNull
    @Column(name = "AUTHOR_ID", nullable = false)
    private Long authorId;
    
    @NotNull
    @Column(name = "TYPE", nullable = false, length = 20)
    private String type;
    
    @NotNull
    @Column(name = "AMOUNT", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "CREATE_TIME", updatable = false)
    private LocalDateTime createTime;
    
    @Column(name = "NOVEL_ID")
    private Long novelId;
    
    // 默认构造函数
    public AuthorIncome() {
        this.createTime = LocalDateTime.now();
    }
    
    // 带参数的构造函数
    public AuthorIncome(Long authorId, String type, BigDecimal amount) {
        this.authorId = authorId;
        this.type = type;
        this.amount = amount;
        this.createTime = LocalDateTime.now();
    }
    
    // 带参数的构造函数（包含novelId）
    public AuthorIncome(Long authorId, String type, BigDecimal amount, Long novelId) {
        this.authorId = authorId;
        this.type = type;
        this.amount = amount;
        this.novelId = novelId;
        this.createTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getAuthorId() {
        return authorId;
    }
    
    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public Long getNovelId() {
        return novelId;
    }
    
    public void setNovelId(Long novelId) {
        this.novelId = novelId;
    }
}