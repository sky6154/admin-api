package blog.develobeer.adminApi.service;

import blog.develobeer.adminApi.dao.blog.BlogPostRepository;
import blog.develobeer.adminApi.domain.blog.BlogPost;
import blog.develobeer.adminApi.domain.common.CustomResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private BlogPostRepository blogPostRepository;

    private static String UPLOAD_ROOT;
    private static String ACCESS_ADDR;

    @Autowired
    public void setUploadRoot(@Value("${path.upload-root}") String uploadRoot){
        this.UPLOAD_ROOT = uploadRoot;
    }

    @Autowired
    public void setAccessAddr(@Value("${path.access-addr}") String accessAddr){
        this.ACCESS_ADDR = accessAddr;
    }


    /**
     * 이미지 업로드
     *  1 : 성공
     * -1 : 파일 없음
     * -2 : 폴더 생성 실패
     * -3 : 파일 업로드 실패
     * @param files
     * @return
     */
    public List<CustomResult> fileUpload(MultipartFile[] files){
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

                    CustomResult customResult = new CustomResult(1, "File is uploaded successfully : " + multipartFile.getOriginalFilename());
                    HashMap<String, String> map = new HashMap<>();

                    map.put("fileName", multipartFile.getOriginalFilename());
                    map.put("path", ACCESS_ADDR);

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

    /**
     * 글쓰기
     *  1 : 성공
     * -1 : DB insert 실패
     * @param boardId
     * @param title
     * @param content
     * @return
     */
    public CustomResult uploadPost(Integer boardId, String title, String content){
        BlogPost blogPost = new BlogPost();

        blogPost.setBoardId(boardId);
        blogPost.setTitle(title);
        blogPost.setContent(content);
        blogPost.setAuthor("kokj");

        CustomResult customResult;

        try{
            blogPostRepository.saveAndFlush(blogPost);

            customResult = new CustomResult(1, "Upload success");
        }
        catch(Exception e){
            e.printStackTrace();
            customResult = new CustomResult(-1, e.getMessage());
        }

        return customResult;
    }
}
