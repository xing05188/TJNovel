package edu.tongji.contentservice.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "WHOLEPURCHASE")
@IdClass(WholePurchaseId.class)
public class WholePurchase implements Serializable {
    @Id
    @Column(name = "READER_ID", nullable = false)
    private Long readerId;
    
    @Id
    @Column(name = "NOVEL_ID", nullable = false)
    private Long novelId;
    
    @Column(name = "IS_BOUGHT", length = 2)
    private String isBought = "否";
    
    // 默认构造函数
    public WholePurchase() {}
    
    // 带参数的构造函数
    public WholePurchase(Long readerId, Long novelId) {
        this.readerId = readerId;
        this.novelId = novelId;
        this.isBought = "否";
    }
    
    // 带购买状态的构造函数
    public WholePurchase(Long readerId, Long novelId, String isBought) {
        this.readerId = readerId;
        this.novelId = novelId;
        this.isBought = isBought;
    }
}