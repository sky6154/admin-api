package blog.develobeer.adminApi.controller.blog;

import blog.develobeer.adminApi.domain.blog.BlogBoard;
import blog.develobeer.adminApi.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService){
        this.boardService = boardService;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<BlogBoard> getBoardList() {
        return boardService.getBoardList();
    }
}
