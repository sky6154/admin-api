package blog.develobeer.adminApi.controller.blog;

import blog.develobeer.adminApi.domain.common.CustomResult;
import blog.develobeer.adminApi.service.PostService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService){
        this.postService = postService;
    }

    @RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
    public List<CustomResult> upload(MultipartFile[] files) {
        return postService.fileUpload(files);
    }

    @RequestMapping(value = "/uploadPost", method = RequestMethod.POST)
    public CustomResult uploadPost(@RequestBody String jsonData) {
        Gson gson = new Gson();

        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> postInfo = gson.fromJson(jsonData, type);

        return postService.uploadPost(Integer.parseInt(postInfo.get("boardId")), postInfo.get("title"), postInfo.get("content"));
    }
}
