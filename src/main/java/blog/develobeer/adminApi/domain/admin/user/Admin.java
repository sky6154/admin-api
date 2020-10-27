package blog.develobeer.adminApi.domain.admin.user;

import blog.develobeer.adminApi.domain.admin.role.Role;
import com.querydsl.core.annotations.QueryProjection;
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
@Table(name = "user")
@ToString
public class Admin implements Serializable {

    private static final long serialVersionUID = 4987321495242877070L;

    public Admin(){}

    @QueryProjection
    public Admin(String id, String pwd, String description, String name, boolean isActive, String email, Role role, Timestamp modifyDate, Timestamp regDate) {
        this.id = id;
        this.pwd = pwd;
        this.description = description;
        this.name = name;
        this.isActive = isActive;
        this.email = email;
        this.role = role;
        this.modifyDate = modifyDate;
        this.regDate = regDate;
    }

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
