package blog.develobeer.adminApi.domain.admin.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AuthenticationRequest {
    private String username;
    private String password;
}
