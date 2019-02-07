package blog.develobeer.adminApi.service;

import blog.develobeer.adminApi.dao.blog.BlogBoardRepository;
import blog.develobeer.adminApi.dao.blog.BlogPostRepository;
import blog.develobeer.adminApi.domain.blog.BlogBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {

    @Autowired
    BlogBoardRepository blogBoardRepository;

    @Autowired
    BlogPostRepository blogPostRepository;

    public List<BlogBoard> getBoardList(){
        return blogBoardRepository.findAll();
    }
}
