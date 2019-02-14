package blog.develobeer.adminApi.controller.admin;

import blog.develobeer.adminApi.domain.admin.user.User;
import blog.develobeer.adminApi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @RequestMapping(value = "/")
    public String home(){
        return "HELLO ADMIN !";
    }

    @GetMapping("/create")
    public User create(){
        User user = new User();
        user.setId("kokj");
        user.setPwd(passwordEncoder.encode("test"));

        return userService.save(user);
    }
}
