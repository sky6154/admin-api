package blog.develobeer.adminApi.controller;

import blog.develobeer.adminApi.domain.admin.user.AuthenticationRequest;
import blog.develobeer.adminApi.domain.admin.user.AuthenticationToken;
import blog.develobeer.adminApi.domain.admin.user.User;
import blog.develobeer.adminApi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.security.Security;

@RestController
@RequestMapping("/")
public class BasicController {

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @GetMapping("/")
    public String home() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        System.out.println("################### AUTH ######################");
        System.out.println(auth.getPrincipal());
        System.out.println(auth.getAuthorities());

        return "HELLO WORLD";
    }

    @GetMapping("/test")
    public String test() {
        UserDetails user = userService.loadUserByUsername("kokj");

        System.out.println(user.getUsername());
        System.out.println(user.getPassword());
        System.out.println(user.getAuthorities());

        return "WOW";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public AuthenticationToken login(@RequestBody AuthenticationRequest authenticationRequest, HttpSession session) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);

        System.out.println("################################");
        System.out.println(authentication.getPrincipal());
        System.out.println(authentication.getName());

        if (authentication.isAuthenticated()) {
            // token으로 Authentication 생성 후 getPrincipal 시 UserDetails를 반환하여 인증에 실패하는것 같음
            // 인증에 성공할 경우 해당 ID / PW / 권한을 가지고 다시 생성하여 세션을 생성함
            // principal에 대해 연구 필요 ..
//            SecurityContextHolder.getContext().setAuthentication(authentication);
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(authentication.getName(), authentication.getCredentials(), authentication.getAuthorities()));
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            return new AuthenticationToken(authentication.getName(), authentication.getAuthorities(), session.getId());
        }
        else{
            return null;
        }


    }
}
