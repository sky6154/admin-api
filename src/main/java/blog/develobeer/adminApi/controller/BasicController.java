package blog.develobeer.adminApi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/")
public class BasicController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "HELLO WORLD";
    }

    @RequestMapping(value = "/getAuthorities", method = RequestMethod.GET)
    public ResponseEntity<Collection> getAuthorities() {
        try {
            return ResponseEntity.ok(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        } catch (Exception e) {
//            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
