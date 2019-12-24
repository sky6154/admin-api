package blog.develobeer.adminApi.domain.blog;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name="blog_post")
public class BlogPost implements Serializable {
    private static final long serialVersionUID = 1254837323714973222L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer seq;

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private Integer hits = 0;

    @Column
    private String author;

    @Column(name="board_id")
    private Integer boardId;

    @Column(name="is_delete")
    private Boolean isDelete = false;

    @UpdateTimestamp
    @Column(name="modify_date")
    private Timestamp modifyDate;

    @CreationTimestamp
    @Column(name="reg_date", updatable = false)
    private Timestamp regDate;
}
