package blog.develobeer.adminApi.controller.admin;

import blog.develobeer.adminApi.domain.admin.user.User;
import blog.develobeer.adminApi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home(){
        return "HELLO WORLD";
    }

    @GetMapping("/create")
    public User create(){
        User user = new User();
        user.setId("kokj");
        user.setPwd(passwordEncoder.encode("test"));

        System.out.println(user.getId());
        System.out.println(user.getPwd());

        return userService.save(user);
    }
}
