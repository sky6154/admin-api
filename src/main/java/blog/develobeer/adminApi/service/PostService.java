package blog.develobeer.adminApi.service;

import blog.develobeer.adminApi.dao.blog.BlogPostRepository;
import blog.develobeer.adminApi.domain.blog.BlogPost;
import blog.develobeer.adminApi.domain.common.CustomResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostService {

    private final BlogPostRepository blogPostRepository;

    private static String UPLOAD_ROOT;
    private static String ACCESS_ADDR;

    @Autowired
    public PostService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    @Autowired
    public void setUploadRoot(@Value("${path.upload-root}") String uploadRoot) {
        this.UPLOAD_ROOT = uploadRoot;
    }

    @Autowired
    public void setAccessAddr(@Value("${path.access-addr}") String accessAddr) {
        this.ACCESS_ADDR = accessAddr;
    }


    /**
     * 이미지 업로드
     * 1 : 성공
     * -1 : 파일 없음
     * -2 : 폴더 생성 실패
     * -3 : 파일 업로드 실패
     *
     * @param file
     * @return
     */
    public Map<String, String> uploadFile(MultipartFile file) throws IOException {
        Map<String, String> result = new HashMap<>();

        if (file == null) {
            throw new IOException("File not exist.");
        } else {
            // 경로(폴더)가 없을 경우 생성
            if (Files.notExists(Paths.get(UPLOAD_ROOT))) {
                Files.createDirectory(Paths.get(UPLOAD_ROOT));
            }

            byte[] bytes = file.getBytes();

            Path path = Paths.get(UPLOAD_ROOT + file.getOriginalFilename());
            Files.write(path, bytes);

            result.put("fileName", file.getOriginalFilename());
            result.put("path", ACCESS_ADDR);

            return result;
        }
    }

    /**
     * 글쓰기
     * 1 : 성공
     * -1 : DB insert 실패
     *
     * @param boardId
     * @param title
     * @param content
     * @return
     */
    public boolean uploadPost(Integer boardId, String title, String content) {
        BlogPost blogPost = new BlogPost();

        blogPost.setBoardId(boardId);
        blogPost.setTitle(title);
        blogPost.setContent(content);
        blogPost.setAuthor("kokj");

        try {
            blogPostRepository.saveAndFlush(blogPost);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
