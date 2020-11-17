package blog.develobeer.adminApi.repo.blog;

import blog.develobeer.adminApi.domain.blog.BlogBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogBoardRepository extends JpaRepository<BlogBoard, Integer> {
}
