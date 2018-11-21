package blog.develobeer.adminApi.dao.admin.user;

import blog.develobeer.adminApi.domain.admin.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findById(String id);
}
