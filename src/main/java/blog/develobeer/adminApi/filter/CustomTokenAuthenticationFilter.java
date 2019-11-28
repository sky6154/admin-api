package blog.develobeer.adminApi.filter;

import blog.develobeer.adminApi.service.AdminService;
import blog.develobeer.adminApi.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.session.MapSession;
import org.springframework.session.Session;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

public class CustomTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger logger = LoggerFactory.getLogger(CustomTokenAuthenticationFilter.class);
    public final String HEADER_SECURITY_TOKEN = "X-Develobeer-Token";

    private TokenService tokenService;
    private AdminService adminService;

    public CustomTokenAuthenticationFilter(RequestMatcher requestMatcher,
                                           AuthenticationManager authenticationManager) {
        super(requestMatcher);
        setAuthenticationManager(authenticationManager);
        setAuthenticationSuccessHandler(new TokenSimpleUrlAuthenticationSuccessHandler());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // chrome preflight options인 경우 pass 한다.
        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return null;
        }

        String token = request.getHeader(HEADER_SECURITY_TOKEN);
        AbstractAuthenticationToken userAuthenticationToken = authUserByToken(token);

        if (userAuthenticationToken == null) {
            throw new AuthenticationServiceException(MessageFormat.format("Error | {0}", "Bad Token"));
        }
        return userAuthenticationToken;
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response, AuthenticationException failed)
            throws IOException {
        SecurityContextHolder.clearContext();

        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }


    private AbstractAuthenticationToken authUserByToken(String token) {
        if (token == null) {
            return null;
        }

        String decodedString = DevelobeerAuthenticationToken.decode(token);
        String[] info = DevelobeerAuthenticationToken.splitToken(decodedString);

        String userName = info[0];
        String sessionId = info[1];

        Map<String, MapSession> result = tokenService.getSessionByName(userName);

        MapSession session = result.get(sessionId);

        if (session == null || session.isExpired()) {
            throw new CredentialsExpiredException("Invalid token.");
        } else {
            session.setLastAccessedTime(new Date().toInstant());
            tokenService.updateAccessTime(session);

            UserDetails userDetails = adminService.loadUserByUsername(userName);

            DevelobeerAuthenticationToken auth = new DevelobeerAuthenticationToken(userDetails);

            try {
                return auth;
            } catch (Exception e) {
                logger.error("Authenticate user by token error: ", e);
            }
        }

        return null;
    }


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (tokenService == null) {
            ServletContext servletContext = req.getServletContext();
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            tokenService = webApplicationContext.getBean(TokenService.class);
        }

        if (adminService == null) {
            ServletContext servletContext = req.getServletContext();
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            adminService = webApplicationContext.getBean(AdminService.class);
        }

        super.doFilter(req, res, chain);
    }

}