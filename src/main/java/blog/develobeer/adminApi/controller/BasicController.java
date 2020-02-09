package blog.develobeer.adminApi.controller;

import blog.develobeer.adminApi.domain.admin.user.AuthenticationRequest;
import blog.develobeer.adminApi.domain.admin.user.AuthenticationToken;
import blog.develobeer.adminApi.filter.CustomTokenAuthenticationFilter;
import blog.develobeer.adminApi.filter.DevelobeerAuthenticationToken;
import blog.develobeer.adminApi.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;

@RestController
@RequestMapping("/")
public class BasicController {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    Environment env;

    @Autowired
    public BasicController(TokenService tokenService, AuthenticationManager authenticationManager){
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/")
    public String home() {
        return "HELLO WORLD";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response, HttpSession session) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

            System.out.println("##########");
            System.out.println(session.getId());

            session.setAttribute(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, authenticationRequest.getUsername());
//            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            System.out.println("###");
            System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
            System.out.println(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());

            return ResponseEntity.ok(new AuthenticationToken(authenticationRequest.getUsername(), authorities));
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }


//        if(tokenService.login(authenticationRequest)){
//            session.setAttribute(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, authenticationRequest.getUsername());
//
////            String token = DevelobeerAuthenticationToken.encode(authenticationRequest.getUsername(), session.getId());
//            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
////
////            Cookie authToken = new Cookie(CustomTokenAuthenticationFilter.CUSTOM_TOKEN_HEADER, token);
////            authToken.setHttpOnly(true);
////            authToken.setSecure(true); // default
////
////            for(String activeProfile : env.getActiveProfiles()){
////                if(activeProfile.contains("test")){
////                    authToken.setSecure(false); // for test env
////                }
////            }
////
////            response.addCookie(authToken);
//
//
//
//            return ResponseEntity.ok(new AuthenticationToken(authenticationRequest.getUsername(), authorities));
//        }
//        else{
//            return ResponseEntity.badRequest().build();
//        }
    }

    @RequestMapping(value = "/getAuthorities", method = RequestMethod.GET)
    public ResponseEntity<Collection> getAuthorities() {
        try {
            return ResponseEntity.ok(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        }catch(Exception e){
//            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
