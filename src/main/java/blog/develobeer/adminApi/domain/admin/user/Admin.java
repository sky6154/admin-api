package blog.develobeer.adminApi.domain.admin.user;

import blog.develobeer.adminApi.domain.admin.role.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name="user")
@ToString
public class Admin implements Serializable {
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

    @Transient
    private Role role;

    @Column(name = "modify_date")
    @UpdateTimestamp
    private Timestamp modifyDate;

    @Column(name = "reg_date")
    @CreationTimestamp
    private Timestamp regDate;
}
