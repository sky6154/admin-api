package blog.develobeer.adminApi.domain.blog;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name = "blog_board")
public class BlogBoard implements Serializable {
    private static final long serialVersionUID = -5208624951912909252L;

    @Id
    @Column(name = "board_id")
    private Integer boardId;

    @Column
    private String description;

    @Column(name = "is_open")
    private Boolean isOpen;

    @CreationTimestamp
    @Column(name = "reg_date")
    private Timestamp regDate;
}
