package edu.tongji.contentservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "COMMENTS")
public class Comment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    private Long commentId;

    @Column(name = "READER_ID", nullable = false)
    private Long readerId;

    @Column(name = "NOVEL_ID", nullable = false)
    private Long novelId;

    @Column(name = "CHAPTER_ID", nullable = false)
    private Long chapterId;

    @Column(name = "TITLE", nullable = false, length = 40)
    private String title;

    @Column(name = "CONTENT", columnDefinition = "TEXT")
    private String content;

    @Column(name = "LIKES")
    private Integer likes = 0;

    @Column(name = "STATUS", length = 10)
    private String status = "通过";

    @Column(name = "CREATE_TIME", updatable = false)
    private Date createTime;

}
