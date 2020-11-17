package blog.develobeer.adminApi.repo.admin.user;

import blog.develobeer.adminApi.domain.admin.user.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer>, QAdminRepository {
    Optional<Admin> findById(String id);
}
