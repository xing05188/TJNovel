package edu.tongji.contentservice.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "REPORT")
public class Report implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REPORT_ID", nullable = false)
    private Long reportId;
    
    @Column(name = "REASON", length = 200)
    private String reason;
    
    @Column(name = "REPORT_TIME", updatable = false)
    private LocalDateTime reportTime;
    
    @Column(name = "PROGRESS", length = 10)
    private String progress = "未处理";
    
    @Column(name = "COMMENT_ID")
    private Long commentId;
    
    @Column(name = "READER_ID", nullable = false)
    private Long readerId;
    
    // 默认构造函数
    public Report() {
        this.reportTime = LocalDateTime.now();
    }
    
    // 带参数的构造函数
    public Report(String reason, Long commentId, Long readerId) {
        this.reason = reason;
        this.commentId = commentId;
        this.readerId = readerId;
        this.reportTime = LocalDateTime.now();
        this.progress = "未处理";
    }
}