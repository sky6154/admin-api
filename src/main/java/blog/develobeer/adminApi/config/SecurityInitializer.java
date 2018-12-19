package blog.develobeer.adminApi.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {
    public SecurityInitializer() {
        super(ApplicationSecurity.class, RedisConfig.class);
//        super(ApplicationSecurity.class);
    }
}
