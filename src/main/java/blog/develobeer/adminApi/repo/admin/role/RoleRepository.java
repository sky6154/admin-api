package blog.develobeer.adminApi.repo.admin.role;

import blog.develobeer.adminApi.domain.admin.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

}
