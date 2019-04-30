package blog.develobeer.adminApi.config;

import blog.develobeer.adminApi.filter.CustomTokenAuthenticationFilter;
import blog.develobeer.adminApi.filter.MyLogoutSuccessHandler;
import blog.develobeer.adminApi.filter.RestAuthenticationEntryPoint;
import blog.develobeer.adminApi.service.AdminService;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {

    private final AdminService adminService;
    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ApplicationSecurity(AdminService adminService,
                               FindByIndexNameSessionRepository<? extends Session> sessionRepository,
                               RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                               PasswordEncoder passwordEncoder) {
        this.adminService = adminService;
        this.sessionRepository = sessionRepository;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.passwordEncoder = passwordEncoder;
    }

    private String[] AUTHENTICATION_REQUIRED_PATTERN = {"/admin/**", "/post/**", "/board/**", "/authCheck"};

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .httpBasic().disable()
                .sessionManagement()
                .maximumSessions(1)
                .sessionRegistry(sessionRegistry())
                .maxSessionsPreventsLogin(true)
                .and()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(customTokenAuthenticationFilter(AUTHENTICATION_REQUIRED_PATTERN), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/", "/login", "/logout", "/error").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/authCheck").hasAnyAuthority("ROLE_BLOG", "ROLE_ADMIN", "ROLE_ETC")
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
                .logoutSuccessHandler(new MyLogoutSuccessHandler());
//                  .invalidateHttpSession(true);

    }

    private CustomTokenAuthenticationFilter customTokenAuthenticationFilter(String[] patterns) throws Exception {
        return new CustomTokenAuthenticationFilter(getRequiredAuthPath(patterns), this.authenticationManagerBean());
    }

    public OrRequestMatcher getRequiredAuthPath(String[] patterns) {
        List<RequestMatcher> requestMatchers = new ArrayList<>();

        for (String pattern : patterns) {
            RequestMatcher requestMatcher = new AntPathRequestMatcher(pattern);

            requestMatchers.add(requestMatcher);
        }

        OrRequestMatcher orRequestMatcher = new OrRequestMatcher(requestMatchers);

        return orRequestMatcher;
    }

    @Bean
    public SpringSessionBackedSessionRegistry sessionRegistry() {
        return new SpringSessionBackedSessionRegistry(this.sessionRepository);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(adminService).passwordEncoder(passwordEncoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

//
//    @Bean
//    public HttpSessionEventPublisher httpSessionEventPublisher() {
//        return new HttpSessionEventPublisher();
//    }

//    @Bean
//    GrantedAuthorityDefaults grantedAuthorityDefaults() {
//        return new GrantedAuthorityDefaults(""); // Remove the ROLE_ prefix
//    }
}
