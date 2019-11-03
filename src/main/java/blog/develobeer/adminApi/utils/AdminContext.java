package blog.develobeer.adminApi.utils;

import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

public class AdminContext {
    public static String getAdminName(){
        Principal principal = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getName();
    }
}
