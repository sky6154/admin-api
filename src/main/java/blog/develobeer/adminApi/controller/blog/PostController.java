package blog.develobeer.adminApi.controller.blog;

import blog.develobeer.adminApi.service.PostService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private static final Gson gson = new Gson();

    @Autowired
    public PostController(PostService postService){
        this.postService = postService;
    }

    @RequestMapping(value = "/uploadFile/{boardId}", method = RequestMethod.POST)
    public ResponseEntity uploadFile(MultipartFile[] files, @PathVariable Integer boardId) throws Exception {
        List<Map<String, String>> result = new ArrayList<>();

        if(files == null || files.length < 1){
            return ResponseEntity.badRequest().build();
        }

        for(int index = 0; index < files.length; index++){
            result.add(postService.uploadFile(boardId, index, files[index]));
        }

        return ResponseEntity.ok(result);
}

    @RequestMapping(value = "write/{boardId}", method = RequestMethod.POST)
    public ResponseEntity uploadPost(@RequestBody String jsonData, @PathVariable Integer boardId) {
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> postInfo = gson.fromJson(jsonData, type);

        URI uri = ControllerLinkBuilder.linkTo(PostController.class).slash("write").slash(boardId).toUri();

        return ResponseEntity.created(uri).body( postService.uploadPost(boardId, postInfo.get("title"), postInfo.get("content")) );
    }

    @RequestMapping(value = "update/{boardId}/post/{seq}", method = RequestMethod.PUT)
    public ResponseEntity updatePost(@RequestBody String jsonData, @PathVariable Integer boardId, @PathVariable Integer seq) {
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> postInfo = gson.fromJson(jsonData, type);

        return ResponseEntity.ok( postService.updatePost(boardId, seq, postInfo.get("title"), postInfo.get("content")) );
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
