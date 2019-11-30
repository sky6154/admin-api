package blog.develobeer.adminApi.filter;

import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

public class DevelobeerCsrfTokenRepo {

    private final HttpSessionCsrfTokenRepository httpSessionCsrfTokenRepository;
    public static String CSRF_SESSION_ATTR = "DEVELOBEER_CSRF_TOKEN";

    public DevelobeerCsrfTokenRepo(){
        this.httpSessionCsrfTokenRepository = new HttpSessionCsrfTokenRepository();
        this.httpSessionCsrfTokenRepository.setSessionAttributeName(CSRF_SESSION_ATTR);
    }

    public HttpSessionCsrfTokenRepository getCsrfTokenRepo(){
        return this.httpSessionCsrfTokenRepository;
    }
}
