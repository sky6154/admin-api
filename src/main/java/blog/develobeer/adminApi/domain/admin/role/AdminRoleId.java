package blog.develobeer.adminApi.domain.admin.role;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Setter
@Getter
@Embeddable
@EqualsAndHashCode
@ToString
public class AdminRoleId implements Serializable {
    @Column(name="user_seq")
    private Integer userSeq;

    @Column(name="role_id")
    private Integer roleId;
}
