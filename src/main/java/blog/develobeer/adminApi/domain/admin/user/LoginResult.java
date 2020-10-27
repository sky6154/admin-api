package blog.develobeer.adminApi.domain.admin.user;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Setter
@Getter
public class LoginResult {
    private String username;
    private Collection authorities;

    public LoginResult(String username, Collection collection) {
        this.username = username;
        this.authorities = collection;
    }
}
