package blog.develobeer.adminApi.dao.admin.user;

import blog.develobeer.adminApi.domain.admin.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(String id);
}
