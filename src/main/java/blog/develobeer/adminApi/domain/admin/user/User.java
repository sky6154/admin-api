package blog.develobeer.adminApi.domain.admin.user;

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
@Table(name="user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seq;

    @Column
    private String id;

    @Column
    private String pwd;

    @Column
    private String description;

    @Column
    private String name;

    @Column(name = "is_active")
    private boolean isActive;

    @Column
    private String email;

    @Column(name = "modify_date")
    @UpdateTimestamp
    private Timestamp modifyDate;

    @Column(name = "reg_date")
    @CreationTimestamp
    private Timestamp regDate;
}
