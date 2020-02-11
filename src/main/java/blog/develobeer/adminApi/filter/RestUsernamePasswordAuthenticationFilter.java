package blog.develobeer.adminApi.filter;

import blog.develobeer.adminApi.domain.admin.user.AuthenticationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;

public class RestUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    public RestUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper){
        super();
        this.objectMapper = objectMapper;
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        return handleObtainLoginRequest(request).getUsername();
    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        return handleObtainLoginRequest(request).getPassword();
    }

    private AuthenticationRequest handleObtainLoginRequest(HttpServletRequest request){
        AuthenticationRequest authenticationRequest = (AuthenticationRequest) request.getAttribute(AuthenticationRequest.class.getName());

        if(authenticationRequest == null){
            try{
                authenticationRequest = this.objectMapper.readValue(request.getReader(), AuthenticationRequest.class);
                // 최초 readValue 후 stream이 closed 되어 username 읽고, password읽을 때 exception이 발생하므로 request에 객체로 저장한다.
                request.setAttribute(AuthenticationRequest.class.getName(), authenticationRequest);
            }
            catch(Exception e){
                e.printStackTrace();
                authenticationRequest = new AuthenticationRequest();
                authenticationRequest.setUsername("");
                authenticationRequest.setPassword("");
            }
        }

        return authenticationRequest;
    }
}
