package blog.develobeer.adminApi.controller;

import blog.develobeer.adminApi.domain.admin.user.AuthenticationRequest;
import blog.develobeer.adminApi.domain.admin.user.AuthenticationToken;
import blog.develobeer.adminApi.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
            return tokenService.authCheck(token);
        }catch(Exception e){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());

            return null;
        }
    }
}
