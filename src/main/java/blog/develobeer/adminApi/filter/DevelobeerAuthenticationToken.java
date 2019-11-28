package blog.develobeer.adminApi.filter;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Collection;

public class DevelobeerAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 1L;
    private final Object principal;
    private Object details;

    private UserDetails userDetails;

    DevelobeerAuthenticationToken(UserDetails userDetails) {
        super(null);

        // 위에서 토큰 체크를 해주었다고 가정한 후 인증처리를 한다.
        super.setAuthenticated(true); // must use super, as we override

        this.userDetails = userDetails;
        this.principal = (Principal) userDetails::getUsername;
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
        String[] list = decodedString.split(":");

        if(list.length < 1){
            throw new AuthenticationCredentialsNotFoundException("Token is not found.");
        }
        else{
            return list;
        }
    }

    public static String encode(String id, String sessionId){
        String makeOneString = id + ":" + sessionId;
        return TokenManager.encrypt(makeOneString);
    }

    public static String decode(String token){
        String decrypted = TokenManager.decrypt(token);

        if(decrypted == null){
            throw new AuthenticationServiceException("Invalid token");
        }
        else{
            return decrypted;
        }
    }

}