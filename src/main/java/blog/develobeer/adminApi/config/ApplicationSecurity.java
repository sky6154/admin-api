package blog.develobeer.adminApi.config;

import blog.develobeer.adminApi.filter.*;
import blog.develobeer.adminApi.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {

    private final AdminService adminService;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final PasswordEncoder passwordEncoder;
    private final FindByIndexNameSessionRepository sessionRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public ApplicationSecurity(AdminService adminService,
                               RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                               PasswordEncoder passwordEncoder,
                               FindByIndexNameSessionRepository sessionRepository,
                               ObjectMapper objectMapper) {
        this.adminService = adminService;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.passwordEncoder = passwordEncoder;
        this.sessionRepository = sessionRepository;
        this.objectMapper = objectMapper;
    }

    private static String[] AUTHENTICATION_REQUIRED_PATTERN = {"/admin/**", "/post/**", "/board/**", "/getAuthorities"};

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic()
                    .disable()
                .csrf()
                    .csrfTokenRepository(getCsrfTokenRepo()).ignoringAntMatchers("/", "/login", "/error")
                .and()
                .sessionManagement()
                    .enableSessionUrlRewriting(false)
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(false)
                    .sessionRegistry(this.sessionRegistry())
                    .expiredSessionStrategy(new RestSessionExpiredStrategy())
                .and()
                    .sessionFixation().changeSessionId()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .addFilterAt(this.restUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .antMatchers("/", "/login", "/logout", "/error").permitAll()
                    .antMatchers("/getAuthorities").hasAnyAuthority("ROLE_BLOG", "ROLE_ADMIN", "ROLE_ETC")
                    .antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                    .antMatchers("/post/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_BLOG")
                    .antMatchers("/board/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_BLOG")
                    .anyRequest().authenticated()
                .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .logout()
                    .logoutUrl("/logout")
                    .deleteCookies("DEVELOBEER-SESSION")
                    .invalidateHttpSession(true)
                    .logoutSuccessHandler(new RestLogoutSuccessHandler());
    }

    private RestUsernamePasswordAuthenticationFilter restUsernamePasswordAuthenticationFilter(){
        RestUsernamePasswordAuthenticationFilter filter = new RestUsernamePasswordAuthenticationFilter(this.objectMapper);
        filter.setPostOnly(true);
        filter.setSessionAuthenticationStrategy(this.authStrategy());
        filter.setAuthenticationSuccessHandler(new RestLoginSuccessHandler(objectMapper));
        filter.setAuthenticationManager(this.authenticationManagerBean());

        return filter;
    }

    @Bean
    public SpringSessionBackedSessionRegistry sessionRegistry() {
        return new SpringSessionBackedSessionRegistry<>(this.sessionRepository);
    }

    private OrRequestMatcher getRequiredAuthPath(String[] patterns) {
        List<RequestMatcher> requestMatchers = new ArrayList<>();

        for (String pattern : patterns) {
            RequestMatcher requestMatcher = new AntPathRequestMatcher(pattern);
            requestMatchers.add(requestMatcher);
        }

        return new OrRequestMatcher(requestMatchers);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(adminService).passwordEncoder(this.passwordEncoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() {
        try{
            return super.authenticationManagerBean();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Bean
    public HttpSessionCsrfTokenRepository getCsrfTokenRepo(){
        return new DevelobeerCsrfTokenRepo().getCsrfTokenRepo();
    }

    @Bean
    public ConcurrentSessionControlAuthenticationStrategy authStrategy(){
        ConcurrentSessionControlAuthenticationStrategy strategy = new ConcurrentSessionControlAuthenticationStrategy(this.sessionRegistry());
        strategy.setExceptionIfMaximumExceeded(false);
        strategy.setMaximumSessions(1);

        return strategy;
    }
}
