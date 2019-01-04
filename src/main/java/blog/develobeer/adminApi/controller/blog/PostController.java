package blog.develobeer.adminApi.controller.blog;

import blog.develobeer.adminApi.domain.common.CustomResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {
    private static String UPLOAD_ROOT = "D://temp//";

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public List<CustomResult> upload(MultipartFile[] files) {
        ArrayList<CustomResult> result = new ArrayList<>();

        if (files == null || files.length < 1) {
            result.add(new CustomResult(-1, "File doesn't exist !!"));
        } else {
            // 경로(폴더)가 없을 경우 생성
            if (Files.notExists(Paths.get(UPLOAD_ROOT))) {
                try {
                    Files.createDirectory(Paths.get(UPLOAD_ROOT));
                } catch (Exception e) {
                    e.printStackTrace();
                    result.add(new CustomResult(-2, "Fail to create destination folder."));
                }
            }

            // 파일 저장
            for(MultipartFile multipartFile : files){
                try {
                    byte[] bytes = multipartFile.getBytes();

                    Path path = Paths.get(UPLOAD_ROOT + multipartFile.getOriginalFilename());
                    Files.write(path, bytes);

                    CustomResult customResult = new CustomResult(1, "File is successfully uploaded : " + multipartFile.getOriginalFilename());
                    HashMap<String, String> map = new HashMap<>();

                    map.put("file name", multipartFile.getOriginalFilename());
                    map.put("path", UPLOAD_ROOT);

                    customResult.setAdditional(map);

                    result.add(customResult);
                } catch (IOException e) {
                    e.printStackTrace();
                    result.add(new CustomResult(-3, "Fail to upload : " + multipartFile.getOriginalFilename()));
                }
            }
        }

        return result;
    }
}
