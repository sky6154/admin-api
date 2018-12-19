package blog.develobeer.adminApi.domain.admin.user;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class AuthenticationToken {
    private String username;
    private Collection authorities;
    private String token;

    public AuthenticationToken(String username, Collection collection, String token) {
        this.username = username;
        this.authorities = collection;
        this.token = token;
    }
}
