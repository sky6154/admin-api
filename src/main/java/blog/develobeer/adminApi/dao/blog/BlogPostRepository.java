package blog.develobeer.adminApi.dao.blog;

import blog.develobeer.adminApi.domain.blog.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Integer> {
}
