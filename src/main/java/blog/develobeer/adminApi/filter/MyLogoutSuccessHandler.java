package blog.develobeer.adminApi.filter;

import blog.develobeer.adminApi.service.TokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MyLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        ServletContext servletContext = request.getServletContext();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        TokenService tokenService = webApplicationContext.getBean(TokenService.class);

        String token = null;

        for (Cookie cookie : request.getCookies()) {
            if(cookie.getName().equals(CustomTokenAuthenticationFilter.CUSTOM_TOKEN_HEADER)) {
                token = cookie.getValue();
            }
        }

        if (token != null) {
            try {
                tokenService.logout(token);
                response.setStatus(HttpServletResponse.SC_OK);
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

}
