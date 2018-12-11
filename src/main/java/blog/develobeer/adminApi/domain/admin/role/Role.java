package blog.develobeer.adminApi.domain.admin.role;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name="role")
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="role_id")
    private Integer roleId;

    @Column
    private String role;

    @Column
    private String description;
}
