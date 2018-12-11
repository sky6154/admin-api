package blog.develobeer.adminApi.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import java.io.Serializable;

public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer implements Serializable {
    public SecurityInitializer() {
        super(ApplicationSecurity.class, RedisConfig.class);
    }
}
