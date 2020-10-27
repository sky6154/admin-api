package blog.develobeer.adminApi.filter;

import blog.develobeer.adminApi.domain.admin.user.AuthenticationToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

public class RestLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper mapper;

    public RestLoginSuccessHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String json = mapper.writeValueAsString(new AuthenticationToken(username, authorities));

        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(json);
    }
}
