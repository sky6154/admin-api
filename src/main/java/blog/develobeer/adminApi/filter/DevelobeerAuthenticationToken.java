package blog.develobeer.adminApi.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Base64;
import java.util.Collection;

public class DevelobeerAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 1L;
    private final Object principal;
    private Object details;

    private UserDetails userDetails;

    public DevelobeerAuthenticationToken(UserDetails userDetails) {
        super(null);

        // 위에서 토큰 체크를 해주었다고 가정한 후 인증처리를 한다.
        super.setAuthenticated(true); // must use super, as we override

        this.userDetails = userDetails;
        this.principal = (Principal) () -> userDetails.getUsername();
    }

    @Override
    public Object getCredentials() {
        return "Credentials: [PROTECTED];"; // like spring security
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Collection getAuthorities() {
        return userDetails.getAuthorities();
    }

    public static String[] splitToken(String decodedString){
        String[] info = decodedString.split(":");

        return info;
    }

    public static String encode(String id, String sessionId){
        String makeOneString = id + ":" + sessionId;
        String token = TokenManager.encrypt(makeOneString);

        return token;
    }

    public static String decode(String token){
        return TokenManager.decrypt(token);
    }

}