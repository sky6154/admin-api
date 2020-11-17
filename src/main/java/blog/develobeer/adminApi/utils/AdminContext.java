package blog.develobeer.adminApi.utils;

import blog.develobeer.adminApi.domain.admin.user.AdminDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

public class AdminContext {
    public static String getAdminName() {
        AdminDetails adminDetails = (AdminDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return adminDetails.getUsername();
    }

    public static Collection<? extends GrantedAuthority> getAuthorities(){
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }
}
