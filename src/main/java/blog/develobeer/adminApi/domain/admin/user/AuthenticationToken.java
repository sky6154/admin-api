package blog.develobeer.adminApi.domain.admin.user;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Setter
@Getter
public class AuthenticationToken {
    private String username;
    private Collection authorities;
    private String csrfToken;
    private String csrfHeader;

    public AuthenticationToken(String username, Collection collection) {
        this.username = username;
        this.authorities = collection;
    }
}
