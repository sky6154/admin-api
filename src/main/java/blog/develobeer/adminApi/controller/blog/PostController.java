package blog.develobeer.adminApi.controller.blog;

import blog.develobeer.adminApi.service.PostService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService){
        this.postService = postService;
    }

    @RequestMapping(value = "/uploadFile/{boardId}", method = RequestMethod.POST)
    public ResponseEntity uploadFile(MultipartFile[] files, @PathVariable Integer boardId) {
        List<Map<String, String>> result = new ArrayList<>();

        if(files == null || files.length < 1){
            return ResponseEntity.badRequest().build();
        }

        for(int index = 0; index < files.length; index++){
            try {
                result.add(postService.uploadFile(boardId, index, files[index]));
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(files[index].getOriginalFilename(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "write/{boardId}", method = RequestMethod.POST)
    public ResponseEntity uploadPost(@RequestBody String jsonData, @PathVariable Integer boardId) {
        Gson gson = new Gson();

        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> postInfo = gson.fromJson(jsonData, type);

        if(postService.uploadPost(boardId, postInfo.get("title"), postInfo.get("content"))){
            URI uri = ControllerLinkBuilder.linkTo(PostController.class).slash(boardId).toUri();

            return ResponseEntity.created(uri).build();
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "update/{boardId}/{seq}", method = RequestMethod.PUT)
    public ResponseEntity updatePost(@RequestBody String jsonData, @PathVariable Integer boardId, @PathVariable Integer seq) {
        Gson gson = new Gson();

        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> postInfo = gson.fromJson(jsonData, type);

        try{
            return new ResponseEntity(postService.updatePost(boardId, seq, postInfo.get("title"), postInfo.get("content")), HttpStatus.OK);
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/list/{boardId}", method = RequestMethod.GET)
    public ResponseEntity getList(@PathVariable Integer boardId) {
        try {
            return ResponseEntity.ok(postService.getPostList(boardId));
        }
        catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
