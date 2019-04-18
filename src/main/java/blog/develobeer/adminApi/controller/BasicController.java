package blog.develobeer.adminApi.controller;

import blog.develobeer.adminApi.domain.admin.user.AuthenticationRequest;
import blog.develobeer.adminApi.domain.admin.user.AuthenticationToken;
import blog.develobeer.adminApi.service.TokenService;
import blog.develobeer.adminApi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;

@RestController
@RequestMapping("/")
public class BasicController {

    private final TokenService tokenService;

    @Autowired
    public BasicController(TokenService tokenService){
        this.tokenService = tokenService;
    }

    @GetMapping("/")
    public String home() {
        return "HELLO WORLD";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public AuthenticationToken login(@RequestBody AuthenticationRequest authenticationRequest, HttpSession session) {
        return tokenService.login(authenticationRequest, session);
    }

    @RequestMapping(value = "/authCheck", method = RequestMethod.GET)
    public Collection authCheck(@RequestHeader(name="X-Develobeer-Token") String token, HttpServletResponse response) throws IOException {
        try {
            Collection result = tokenService.authCheck(token);

            return result;
        }catch(Exception e){
            response.sendError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());

            return null;
        }
    }
}
