package blog.develobeer.adminApi.dao.admin.user;

import blog.develobeer.adminApi.domain.admin.user.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Optional<Admin> findById(String id);
}
