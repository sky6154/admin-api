package blog.develobeer.adminApi.service;

import blog.develobeer.adminApi.repo.blog.BlogBoardRepository;
import blog.develobeer.adminApi.repo.blog.BlogPostRepository;
import blog.develobeer.adminApi.domain.blog.BlogBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {

    private BlogBoardRepository blogBoardRepository;

    @Autowired
    public BoardService(BlogBoardRepository blogBoardRepository) {
        this.blogBoardRepository = blogBoardRepository;
    }

    public List<BlogBoard> getBoardList() {
        return blogBoardRepository.findAll();
    }
}
