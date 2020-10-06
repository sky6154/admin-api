package blog.develobeer.adminApi.service;

import blog.develobeer.adminApi.dao.blog.BlogPostRepository;
import blog.develobeer.adminApi.domain.blog.BlogPost;
import blog.develobeer.adminApi.utils.AdminContext;
import blog.develobeer.adminApi.utils.CommonTemplateMethod;
import blog.develobeer.adminApi.utils.SimpleAES256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PostService {

    private final BlogPostRepository blogPostRepository;

    @Value("${path.access-addr}")
    private String ACCESS_ADDR;

    @Value("${secure.image.iteration-count}")
    private int ITERATION_COUNT;
    @Value("${secure.image.key}")
    private String KEY;

    @Autowired
    Environment env;

    private static final String BOARD_FOLDER = "board/";
    private static final String FILE_SEPARATOR = "_";

    @Autowired
    public PostService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    public Map<String, String> uploadFile(Integer boardId, int index, MultipartFile file) throws Exception {
        Map<String, String> result = new HashMap<>();

        if (file == null) {
            throw new IOException("File not exist.");
        } else {
            String SAVE_ROOT;

            if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
                SAVE_ROOT = System.getProperty("user.home") + "/workspace/static/";
            } else {
                SAVE_ROOT = System.getProperty("user.home");
            }

            if (!SAVE_ROOT.endsWith("/")) {
                SAVE_ROOT = SAVE_ROOT + "/";
            }

            String SAVE_LOCATION = SAVE_ROOT + BOARD_FOLDER;
            String REF_LOCATION = ACCESS_ADDR + BOARD_FOLDER;

            // 경로(폴더)가 없을 경우 생성
            if (Files.notExists(Paths.get(SAVE_LOCATION))) {
                Files.createDirectories(Paths.get(SAVE_LOCATION)); // 하위 디렉토리까지 모두 생성
            }

            byte[] bytes = file.getBytes();

            int counter = 2;
            int dot = file.getOriginalFilename().lastIndexOf('.');

            String fileName = file.getOriginalFilename();
            String extension = "";

            if (dot > 0) {
                fileName = file.getOriginalFilename().substring(0, dot);
                extension = file.getOriginalFilename().substring(dot + 1);
            }

            boolean isBase32 = true;
            String baseFileName = boardId + FILE_SEPARATOR + fileName + FILE_SEPARATOR + AdminContext.getAdminName() + FILE_SEPARATOR + System.currentTimeMillis();
            baseFileName = SimpleAES256.encryptAES256(baseFileName, KEY, ITERATION_COUNT, isBase32);

            fileName = baseFileName + "." + extension;

            Path path = Paths.get(SAVE_LOCATION + fileName);

            // 파일 이름이 겹칠 경우 괄호안에 카운팅을 하여 저장한다.
            while (Files.exists(path)) {
                if (extension.equals("")) {
                    fileName = baseFileName + " (" + counter + ")";
                } else {
                    fileName = baseFileName + " (" + counter + ")." + extension;
                }

                path = Paths.get(SAVE_LOCATION + fileName);
                counter++;
            }

            Path writePath = Files.write(path, bytes);
            System.out.println("### FILE SAVE RESULT ###");
            System.out.println(writePath);
            System.out.println(writePath.toString());
            System.out.println(writePath.toAbsolutePath());
            System.out.println(SAVE_LOCATION);
            System.out.println(path);
            System.out.println(fileName);
            System.out.println(REF_LOCATION);

            result.put("fileName", fileName);
            result.put("path", REF_LOCATION);

            return result;
        }
    }

    public BlogPost uploadPost(BlogPost blogPost) {
        blogPost.setAuthor(AdminContext.getAdminName());

        return blogPostRepository.saveAndFlush(blogPost);
    }

    public BlogPost updatePost(int boardId, int seq, BlogPost blogPost) {
        Optional<BlogPost> blogPostOptional = blogPostRepository.findById(seq);

        if (blogPostOptional.isPresent()) {
            BlogPost post = blogPostOptional.get();

            post.setBoardId(boardId);
            post.setTitle(blogPost.getTitle());
            post.setContent(blogPost.getContent());

            post.setAuthor(AdminContext.getAdminName());
            post.setModifyDate(Timestamp.valueOf(LocalDateTime.now()));

            return CommonTemplateMethod.simpleSaveTryCatchObjectReturn(blogPostRepository, post);
        } else {
            throw new NoResultException("Post does not exist.");
        }
    }

    public BlogPost deletePost(int seq) {
        Optional<BlogPost> blogPostOptional = blogPostRepository.findById(seq);

        if (blogPostOptional.isPresent()) {
            BlogPost post = blogPostOptional.get();

            post.setIsDelete(true);
            post.setModifyDate(Timestamp.valueOf(LocalDateTime.now()));

            return CommonTemplateMethod.simpleSaveTryCatchObjectReturn(blogPostRepository, post);
        } else {
            throw new NoResultException("Post does not exist.");
        }
    }

    public BlogPost restorePost(int seq) {
        Optional<BlogPost> blogPostOptional = blogPostRepository.findById(seq);

        if (blogPostOptional.isPresent()) {
            BlogPost post = blogPostOptional.get();

            post.setIsDelete(false);
            post.setModifyDate(Timestamp.valueOf(LocalDateTime.now()));

            return CommonTemplateMethod.simpleSaveTryCatchObjectReturn(blogPostRepository, post);
        } else {
            throw new NoResultException("Post does not exist.");
        }
    }

    public List<BlogPost> getPostList(int boardId) {
        return blogPostRepository.findAllByBoardIdOrderBySeqDesc(boardId);
    }
}
