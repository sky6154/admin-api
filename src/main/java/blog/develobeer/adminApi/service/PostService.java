package blog.develobeer.adminApi.service;

import blog.develobeer.adminApi.dao.blog.BlogPostRepository;
import blog.develobeer.adminApi.domain.blog.BlogPost;
import blog.develobeer.adminApi.utils.CommonTemplateMethod;
import blog.develobeer.adminApi.utils.SimpleAES256;
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
import java.security.Principal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Service
public class PostService {

    private final BlogPostRepository blogPostRepository;

    private static String UPLOAD_ROOT;
    private static String ACCESS_ADDR;

    private static int ITERATION_COUNT;
    private static String KEY;

    private static final String BOARD_FOLDER = "board/";
    private static final String FILE_SEPARATOR = "_";

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

    @Autowired
    public void setIterationCount(@Value("${secure.image.iteration-count}") int iterationCount) {
        this.ITERATION_COUNT = iterationCount;
    }

    @Autowired
    public void setKey(@Value("${secure.image.key}") String key) {
        this.KEY = key;
    }


    public Map<String, String> uploadFile(Integer boardId, int index, MultipartFile file) throws IOException, Exception {
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

            Timestamp now = new Timestamp(System.currentTimeMillis());
            Principal principal = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            String fileName = file.getOriginalFilename();
            String extension = "";

            if (dot > 0) {
                fileName = file.getOriginalFilename().substring(0, dot);
                extension = file.getOriginalFilename().substring(dot + 1);
            }

            boolean isBase32 = true;
            String baseFileName = boardId + FILE_SEPARATOR + fileName + FILE_SEPARATOR + principal.getName() + FILE_SEPARATOR + now;
            baseFileName = SimpleAES256.encryptAES256(baseFileName, KEY, ITERATION_COUNT, isBase32);

            Path path = Paths.get(UPLOAD_ROOT + BOARD_FOLDER + baseFileName + "." + extension);

            // 파일 이름이 겹칠 경우 괄호안에 카운팅을 하여 저장한다.
            while (Files.exists(path)) {
                if (extension.equals("")) {
                    fileName = baseFileName + " (" + counter + ")";
                } else {
                    fileName = baseFileName + " (" + counter + ")." + extension;
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

        return CommonTemplateMethod.simpleSaveTryCatchBooleanReturn(blogPostRepository, blogPost);
    }
}
