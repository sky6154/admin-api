package blog.develobeer.adminApi.filter;

import blog.develobeer.adminApi.service.TokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MyLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    public final String HEADER_SECURITY_TOKEN = "X-Develobeer-Token";

    private TokenService tokenService;

    public MyLogoutSuccessHandler(TokenService tokenService){
        this.tokenService = tokenService;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String token = request.getHeader(HEADER_SECURITY_TOKEN);

        if (token != null) {
            try{
                tokenService.logout(token);
                response.sendError( HttpServletResponse.SC_OK);
            }
            catch (Exception e){
                e.printStackTrace();
                response.sendError( HttpServletResponse.SC_BAD_REQUEST, e.getMessage() );
            }
        }
        else{
            response.sendError( HttpServletResponse.SC_BAD_REQUEST, "Token is not exist." );
        }
    }

}
