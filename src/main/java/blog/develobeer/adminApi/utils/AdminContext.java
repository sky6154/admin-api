package blog.develobeer.adminApi.utils;

import blog.develobeer.adminApi.domain.admin.user.AdminDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class AdminContext {
    public static String getAdminName() {
        AdminDetails adminDetails = (AdminDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return adminDetails.getUsername();
    }
}
