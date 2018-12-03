package blog.develobeer.adminApi.domain.admin.role;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name="user_role")
public class UserRole {
    @EmbeddedId
    private UserRoleId userRoleId;

    private String id;

    private String role;

    @CreationTimestamp
    @Column(name="reg_date")
    private Timestamp regDate;
}
