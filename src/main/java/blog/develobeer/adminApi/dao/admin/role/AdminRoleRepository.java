package blog.develobeer.adminApi.dao.admin.role;

import blog.develobeer.adminApi.domain.admin.role.AdminRole;
import blog.develobeer.adminApi.domain.admin.role.AdminRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRoleRepository extends JpaRepository <AdminRole, AdminRoleId> {
    @Query(
            value = "SELECT u.id, r.role, ur.*\n" +
                    "FROM `user` AS u,\n" +
                    "  user_role AS ur,\n" +
                    "  role AS r\n" +
                    "WHERE u.id = :id AND u.seq = ur.user_seq AND ur.role_id = r.role_id",
            nativeQuery = true
    )
    Optional<List<AdminRole>> getAdminRolesByUserId(@Param("id") String id);
}
