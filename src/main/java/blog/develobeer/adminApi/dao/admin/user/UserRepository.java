package blog.develobeer.adminApi.dao.admin.user;

import blog.develobeer.adminApi.domain.admin.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(String id);
}
