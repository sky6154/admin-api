package blog.develobeer.adminApi.domain.admin.role;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class UserRoleId implements Serializable {
    @Column(name="user_seq")
    private Integer userSeq;

    @Column(name="role_id")
    private Integer roleId;
}
