package blog.develobeer.adminApi.domain.admin.role;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name="user_role")
@ToString
public class AdminRole implements Serializable {
    private static final long serialVersionUID = -4245799947333023767L;

    @EmbeddedId
    private AdminRoleId adminRoleId;

    @Transient
    private String id;

    @Transient
    private String role;

    @CreationTimestamp
    @Column(name="reg_date")
    private Timestamp regDate;
}
