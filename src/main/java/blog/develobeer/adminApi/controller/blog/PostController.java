package blog.develobeer.adminApi.controller.blog;

import blog.develobeer.adminApi.domain.blog.BlogPost;
import blog.develobeer.adminApi.service.PostService;
import blog.develobeer.adminApi.utils.CompressUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService){
        this.postService = postService;
    }

    @RequestMapping(value = "/uploadFile/{boardId}", method = RequestMethod.POST)
    public ResponseEntity uploadFile(MultipartFile[] files, @PathVariable Integer boardId) throws Exception {
        List<Map<String, String>> result = new ArrayList<>();

        if(boardId == null || files == null || files.length < 1){
            return ResponseEntity.badRequest().build();
        }

        for(int index = 0; index < files.length; index++){
            MultipartFile compressedImage = CompressUtil.compressImage(files[index]);
            result.add(postService.uploadFile(boardId, index, compressedImage));
        }

        return ResponseEntity.ok(result);
}

    @RequestMapping(value = "write/{boardId}", method = RequestMethod.POST)
    public ResponseEntity uploadPost(@RequestBody BlogPost blogPost, @PathVariable Integer boardId) {
        URI uri = ControllerLinkBuilder.linkTo(PostController.class).slash("write").slash(boardId).toUri();

        return ResponseEntity.created(uri).body( postService.uploadPost(blogPost) );
    }

    @RequestMapping(value = "update/{boardId}/post/{seq}", method = RequestMethod.PUT)
    public ResponseEntity updatePost(@RequestBody BlogPost blogPost, @PathVariable Integer boardId, @PathVariable Integer seq) {
        return ResponseEntity.ok( postService.updatePost(boardId, seq, blogPost) );
    }

    @RequestMapping(value = "delete/{seq}", method = RequestMethod.DELETE)
    public ResponseEntity deletePost(@PathVariable Integer seq) {
        return ResponseEntity.ok( postService.deletePost(seq) );
    }

    @RequestMapping(value = "restore/{seq}", method = RequestMethod.PATCH)
    public ResponseEntity restorePost(@PathVariable Integer seq) {
        return ResponseEntity.ok( postService.restorePost(seq) );
    }

    @RequestMapping(value = "/list/{boardId}", method = RequestMethod.GET)
    public ResponseEntity getList(@PathVariable Integer boardId) {
        return ResponseEntity.ok( postService.getPostList(boardId) );
    }
}
