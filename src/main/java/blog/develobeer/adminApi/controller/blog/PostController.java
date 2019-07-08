package blog.develobeer.adminApi.controller.blog;

import blog.develobeer.adminApi.domain.common.CustomResult;
import blog.develobeer.adminApi.domain.common.error_code.ResponseErrorCode;
import blog.develobeer.adminApi.service.PostService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Type;
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

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public ResponseEntity uploadFile(MultipartFile[] files) {
        List<Map<String, String>> result = new ArrayList<>();

        if(files == null || files.length < 1){
            return ResponseEntity.badRequest().build();
        }

        for(MultipartFile file : files){
            try {
                result.add(postService.uploadFile(file));
            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity<>(file.getOriginalFilename(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return ResponseEntity.ok(result);

//        return ResponseEntity.created().build();
    }

    @RequestMapping(value = "/{boardId}", method = RequestMethod.POST)
    public ResponseEntity uploadPost(@RequestBody String jsonData, @PathVariable String boardId) {
        Gson gson = new Gson();

        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> postInfo = gson.fromJson(jsonData, type);

        if(postService.uploadPost(Integer.parseInt(postInfo.get("boardId")), postInfo.get("title"), postInfo.get("content"))){
            return ResponseEntity.ok().build();
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }
}
