package blog.develobeer.adminApi.domain.blog;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CustomMultipart {
    private String type;
    private String src;
    private String fileName;

    private List<MultipartFile> files;

    @Override
    public String toString(){
        StringBuffer result = new StringBuffer();

        result.append("type : " + this.type + "\n");
        result.append("src : " + this.src + "\n");
        result.append("fileName : " + this.fileName + "\n");

        if(files != null){
            for(int i = 0; i < files.size(); i++) {
                result.append("files["+ i + "] : " + files.get(i) + "\n");
            }
        }
        else{
            result.append("files : null\n");
        }

        return result.toString();
    }
}
