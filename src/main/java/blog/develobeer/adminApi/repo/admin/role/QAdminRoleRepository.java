package blog.develobeer.adminApi.repo.admin.role;

import blog.develobeer.adminApi.domain.admin.role.AdminRole;

import java.util.List;

public interface QAdminRoleRepository {
    List<AdminRole> getAdminRolesByUserId(String id);
}
