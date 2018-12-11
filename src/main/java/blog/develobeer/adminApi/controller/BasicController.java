package blog.develobeer.adminApi.controller;

import blog.develobeer.adminApi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

@RestController
@RequestMapping("/")
public class BasicController {

    @Autowired
    UserService userService;

    @GetMapping("/")
    public String home(){
        return "HELLO WORLD";
    }

    @GetMapping("/test")
    public String test(){
        UserDetails user = userService.loadUserByUsername("kokj");

        System.out.println(user.getUsername());
        System.out.println(user.getPassword());
        System.out.println(user.getAuthorities());

        return "WOW";
    }
}
