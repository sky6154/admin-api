package blog.develobeer.adminApi.service;

import blog.develobeer.adminApi.dao.blog.BlogPostRepository;
import blog.develobeer.adminApi.domain.blog.BlogPost;
import blog.develobeer.adminApi.utils.AdminContext;
import blog.develobeer.adminApi.utils.CommonTemplateMethod;
import blog.develobeer.adminApi.utils.SimpleAES256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PostService {

    private final BlogPostRepository blogPostRepository;

    @Value("${path.upload-root}")
    private String UPLOAD_ROOT;
    @Value("${path.access-addr}")
    private String ACCESS_ADDR;

    @Value("${secure.image.iteration-count}")
    private int ITERATION_COUNT;
    @Value("${secure.image.key}")
    private static String KEY;

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
            // 경로(폴더)가 없을 경우 생성
            if (Files.notExists(Paths.get(UPLOAD_ROOT + BOARD_FOLDER))) {
                Files.createDirectory(Paths.get(UPLOAD_ROOT + BOARD_FOLDER));
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

            Path path = Paths.get(UPLOAD_ROOT + BOARD_FOLDER + fileName);

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

    public BlogPost uploadPost(Integer boardId, String title, String content) {
        BlogPost blogPost = new BlogPost();

        blogPost.setBoardId(boardId);
        blogPost.setTitle(title);
        blogPost.setContent(content);

        blogPost.setAuthor(AdminContext.getAdminName());

        return blogPostRepository.saveAndFlush(blogPost);
    }

    public BlogPost updatePost(int boardId, int seq, String title, String content) {
        Optional<BlogPost> blogPostOptional = blogPostRepository.findById(seq);

        if(blogPostOptional.isPresent()){
            BlogPost post = blogPostOptional.get();

            post.setBoardId(boardId);
            post.setTitle(title);
            post.setContent(content);

            post.setAuthor(AdminContext.getAdminName());
            post.setModifyDate(Timestamp.valueOf(LocalDateTime.now()));

            return CommonTemplateMethod.simpleSaveTryCatchObjectReturn(blogPostRepository, post);
        }
        else{
            throw new NoResultException("Post does not exist.");
        }
    }

    public BlogPost deletePost(int seq) {
        Optional<BlogPost> blogPostOptional = blogPostRepository.findById(seq);

        if(blogPostOptional.isPresent()){
            BlogPost post = blogPostOptional.get();

            post.setIsDelete(true);
            post.setModifyDate(Timestamp.valueOf(LocalDateTime.now()));

            return CommonTemplateMethod.simpleSaveTryCatchObjectReturn(blogPostRepository, post);
        }
        else{
            throw new NoResultException("Post does not exist.");
        }
    }

    public BlogPost restorePost(int seq) {
        Optional<BlogPost> blogPostOptional = blogPostRepository.findById(seq);

        if(blogPostOptional.isPresent()){
            BlogPost post = blogPostOptional.get();

            post.setIsDelete(false);
            post.setModifyDate(Timestamp.valueOf(LocalDateTime.now()));

            return CommonTemplateMethod.simpleSaveTryCatchObjectReturn(blogPostRepository, post);
        }
        else{
            throw new NoResultException("Post does not exist.");
        }
    }

    public List<BlogPost> getPostList(int boardId){
        return blogPostRepository.findAllByBoardIdOrderBySeqDesc(boardId);
    }
}
