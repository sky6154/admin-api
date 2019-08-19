package blog.develobeer.adminApi.dao.admin.user;

import blog.develobeer.adminApi.domain.admin.user.Admin;

import java.util.List;

public interface QAdminRepository {
    List<Admin> getAdminList();
}
