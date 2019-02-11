package blog.develobeer.adminApi.controller;

import blog.develobeer.adminApi.domain.admin.user.AuthenticationRequest;
import blog.develobeer.adminApi.domain.admin.user.AuthenticationToken;
import blog.develobeer.adminApi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/")
public class BasicController {

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @GetMapping("/")
    public String home() {
        return "HELLO WORLD";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public AuthenticationToken login(@RequestBody AuthenticationRequest authenticationRequest, HttpSession session) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

//        SecurityContextHolder.getContext().setAuthentication(authentication);


        System.out.println("########");
        System.out.println(authentication.getPrincipal());
        System.out.println(authentication.getName());
        System.out.println(authentication.getAuthorities());

        System.out.println("#############");
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities());

        session.setAttribute(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, authentication.getName());

//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        return new AuthenticationToken(authentication.getName(), authentication.getAuthorities(), session.getId());
    }

    @RequestMapping(value = "/temp", method = RequestMethod.GET)
    public String temp() {

        System.out.println("#######");
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities());

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }

        System.out.println("###");
        System.out.println(username);

        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
    }


}
