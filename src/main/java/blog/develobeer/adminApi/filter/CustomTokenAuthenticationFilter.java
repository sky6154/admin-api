package blog.develobeer.adminApi.filter;

import blog.develobeer.adminApi.service.TokenService;
import blog.develobeer.adminApi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.session.Session;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

public class CustomTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger logger = LoggerFactory.getLogger(CustomTokenAuthenticationFilter.class);
    public final String HEADER_SECURITY_TOKEN = "X-Develobeer-Token";

    private TokenService tokenService;
    private UserService userService;

    public CustomTokenAuthenticationFilter(RequestMatcher requestMatcher, AuthenticationManager authenticationManager, TokenService tokenService, UserService userService) {
        super(requestMatcher);
        setAuthenticationManager(authenticationManager);
        setAuthenticationSuccessHandler(new TokenSimpleUrlAuthenticationSuccessHandler());

        this.tokenService = tokenService;
        this.userService = userService;
    }

    /**
     * Attempt to authenticate request - basically just pass over to another method to authenticate request headers
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String token = request.getHeader(HEADER_SECURITY_TOKEN);

        AbstractAuthenticationToken userAuthenticationToken = authUserByToken(token);

        if(userAuthenticationToken == null){
            throw new AuthenticationServiceException(MessageFormat.format("Error | {0}", "Bad Token"));
        }
        return userAuthenticationToken;
    }


    /**
     * authenticate the user based on token
     * @return
     */
    private AbstractAuthenticationToken authUserByToken(String token) {
        if(token == null) {
            return null;
        }

        byte[] decodedBytes = Base64.getDecoder().decode(token);
        String decodedString = new String(decodedBytes);

        String[] info = this.splitToken(decodedString);

        if(info.length < 1){
            throw new AuthenticationCredentialsNotFoundException("Token is not found.");
        }
        else{
            String userName = info[0];
            String sessionId = info[1];

            Map<String, ? extends Session> result = tokenService.getSessionByName(userName);

            if(result.size() < 1){
                throw new AuthenticationCredentialsNotFoundException("Token is not found.");
            }
            else{
                Session session = result.get(sessionId);

                if(session == null){
                    throw new CredentialsExpiredException("Token is not valid.");
                }

                if(session.isExpired()){
                    throw new CredentialsExpiredException("Token is expired.");
                }
                else{
                    session.setLastAccessedTime(new Date().toInstant());
                    tokenService.updateAccessTime(session);

                    UserDetails userDetails = userService.loadUserByUsername(userName);

                    DevelobeerAuthenticationToken auth = new DevelobeerAuthenticationToken(userDetails);

                    try {
                        return auth;
                    } catch (Exception e) {
                        logger.error("Authenticate user by token error: ", e);
                    }
                }
            }
        }

        return null;
    }


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        super.doFilter(req, res, chain);
    }


    public String[] splitToken(String decodedString){
        String[] info = decodedString.split(":");

        return info;
    }

}