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

        Principal principal = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        blogPost.setAuthor(principal.getName());

        return blogPostRepository.saveAndFlush(blogPost);
    }

    public BlogPost updatePost(int boardId, int seq, String title, String content) {
        Optional<BlogPost> blogPostOptional = blogPostRepository.findById(seq);

        if(blogPostOptional.isPresent()){
            BlogPost post = blogPostOptional.get();

            post.setBoardId(boardId);
            post.setTitle(title);
            post.setContent(content);

            Principal principal = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            post.setAuthor(principal.getName());
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

    public List<BlogPost> getPostList(int boardId){
        return blogPostRepository.findAllByBoardIdOrderBySeqDesc(boardId);
    }
}
