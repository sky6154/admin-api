package blog.develobeer.adminApi.domain.admin.role;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name="user_role")
public class AdminRole implements Serializable {
    @EmbeddedId
    private AdminRoleId adminRoleId;

    private String id;

    private String role;

    @CreationTimestamp
    @Column(name="reg_date")
    private Timestamp regDate;
}
