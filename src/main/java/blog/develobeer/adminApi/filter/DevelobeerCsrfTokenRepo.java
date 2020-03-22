package blog.develobeer.adminApi.filter;

import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

public class DevelobeerCsrfTokenRepo {

    private final HttpSessionCsrfTokenRepository httpSessionCsrfTokenRepository;
    public static String CSRF_SESSION_HEADER = "DEVELOBEER-CSRF";
    public static String CSRF_SESSION_PARAM = "DEVELOBEER-CSRF-PARAM";
    public static String CSRF_SESSION_ATTR = "DEVELOBEER-CSRF-TOKEN";

    public DevelobeerCsrfTokenRepo(){
        this.httpSessionCsrfTokenRepository = new HttpSessionCsrfTokenRepository();
        this.httpSessionCsrfTokenRepository.setSessionAttributeName(CSRF_SESSION_ATTR);
        this.httpSessionCsrfTokenRepository.setHeaderName(CSRF_SESSION_HEADER);
        this.httpSessionCsrfTokenRepository.setParameterName(CSRF_SESSION_PARAM);
//        this.cookieCsrfTokenRepository.setCookieHttpOnly(true);
//        this.cookieCsrfTokenRepository.setCookieName(CSRF_COOKIE_ATTR);
    }

    public HttpSessionCsrfTokenRepository getCsrfTokenRepo(){
        return this.httpSessionCsrfTokenRepository;
    }
}
