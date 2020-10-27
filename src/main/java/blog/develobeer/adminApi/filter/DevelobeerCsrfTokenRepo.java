package blog.develobeer.adminApi.filter;

import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

public class DevelobeerCsrfTokenRepo {

    private final CookieCsrfTokenRepository cookieCsrfTokenRepository;
    public static String CSRF_HEADER = "DEVELOBEER-CSRF-HEADER";
    public static String CSRF_PARAM = "DEVELOBEER-CSRF-PARAM";
    public static String CSRF_ATTR = "DEVELOBEER-CSRF-TOKEN";

    public DevelobeerCsrfTokenRepo() {
        this.cookieCsrfTokenRepository = new CookieCsrfTokenRepository();
        this.cookieCsrfTokenRepository.setCookieHttpOnly(false);
        this.cookieCsrfTokenRepository.setHeaderName(CSRF_HEADER);
        this.cookieCsrfTokenRepository.setCookieName(CSRF_ATTR);
        this.cookieCsrfTokenRepository.setParameterName(CSRF_PARAM);
    }

    public CookieCsrfTokenRepository getCsrfTokenRepo() {
        return this.cookieCsrfTokenRepository;
    }
}
