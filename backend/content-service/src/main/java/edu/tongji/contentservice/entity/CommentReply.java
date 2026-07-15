package edu.tongji.contentservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "COMMENT_REPLY")
public class CommentReply implements Serializable {
    @Id
    @Column(name = "COMMENT_ID")
    private Long commentId;

    @Column(name = "PRE_COM_ID")
    private Long preComId;

    @Column(name = "COMMENT_LEVEL")
    private Integer commentLevel;

}
