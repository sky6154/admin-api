package blog.develobeer.adminApi.domain.common;

import lombok.Getter;
import lombok.Setter;

@Getter
public class CustomResult {
    public CustomResult(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    private Integer code;

    private String message;

    @Setter
    private Object additional;
}
