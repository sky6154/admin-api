package blog.develobeer.adminApi.domain.admin.role;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Setter
@Getter
@Entity
@Table(name="role")
public class Role implements Serializable {
    @Id
    @Column(name="role_id")
    private Integer roleId;

    @Column
    private String role;

    @Column
    private String description;
}
