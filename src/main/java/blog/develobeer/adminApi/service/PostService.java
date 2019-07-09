package blog.develobeer.adminApi.service;

import blog.develobeer.adminApi.dao.blog.BlogPostRepository;
import blog.develobeer.adminApi.domain.blog.BlogPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class PostService {

    private final BlogPostRepository blogPostRepository;

    private static String UPLOAD_ROOT;
    private static String ACCESS_ADDR;

    private static final String BOARD_FOLDER = "board/";

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


    public Map<String, String> uploadFile(Integer boardId, int index, MultipartFile file) throws IOException {
        Map<String, String> result = new HashMap<>();

        if (file == null) {
            throw new IOException("File not exist.");
        } else {
            // 경로(폴더)가 없을 경우 생성
            if (Files.notExists(Paths.get(UPLOAD_ROOT + BOARD_FOLDER))) {
                Files.createDirectory(Paths.get(UPLOAD_ROOT + BOARD_FOLDER));
            }

            byte[] bytes = file.getBytes();

            int counter = 2;
            int dot = file.getOriginalFilename().lastIndexOf('.');

            String fileName = file.getOriginalFilename();
            String extension = "";
            String orignalFileName = file.getOriginalFilename();

            if (dot > 0) {
                fileName = file.getOriginalFilename().substring(0, dot);
                extension = file.getOriginalFilename().substring(dot + 1);
                orignalFileName = file.getOriginalFilename().substring(0, dot);
            }

            // TODO
            // 파일명 = 게시판 번호 + timestamp + 파일 index + 유저 ID ?? => hash.. 겹칠 가능성 ?
            // 글번호는 글을 등록하고 알 수 있어서 이미지 업로드가 선행되어야 함..

            Path path = Paths.get(UPLOAD_ROOT + BOARD_FOLDER + file.getOriginalFilename());

            // 파일 이름이 겹칠 경우 괄호안에 카운팅을 하여 저장한다.
            while (Files.exists(path)) {
                if (extension.equals("")) {
                    fileName = orignalFileName + " (" + counter + ")";
                } else {
                    fileName = orignalFileName + " (" + counter + ")." + extension;
                }

                path = Paths.get(UPLOAD_ROOT + BOARD_FOLDER + fileName);
                counter++;
            }

            Files.write(path, bytes);

            result.put("fileName", fileName);
            result.put("path", ACCESS_ADDR + BOARD_FOLDER);

            return result;
        }
    }

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
