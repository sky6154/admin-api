package blog.develobeer.adminApi.domain.admin.role;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Setter
@Getter
@Entity
@Table(name="role")
@ToString
public class Role implements Serializable {
    @Id
    @Column(name="role_id")
    private Integer roleId;

    @Column
    private String role;

    @Column
    private String description;
}
