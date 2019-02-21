package blog.develobeer.adminApi.domain.admin.role;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Setter
@Getter
@Embeddable
public class UserRoleId implements Serializable {
    @Column(name="user_seq")
    private Integer userSeq;

    @Column(name="role_id")
    private Integer roleId;
}
