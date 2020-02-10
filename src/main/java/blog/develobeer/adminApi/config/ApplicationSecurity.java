package blog.develobeer.adminApi.config;

import blog.develobeer.adminApi.filter.*;
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
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {

    private final AdminService adminService;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ApplicationSecurity(AdminService adminService,
                               RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                               PasswordEncoder passwordEncoder) {
        this.adminService = adminService;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.passwordEncoder = passwordEncoder;
    }

    private static String[] AUTHENTICATION_REQUIRED_PATTERN = {"/admin/**", "/post/**", "/board/**", "/getAuthorities"};

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .sessionManagement()
//                .sessionAuthenticationStrategy(new SessionStrategy())
                .enableSessionUrlRewriting(false)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true)
                .and()
                .sessionFixation().changeSessionId()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
//                .addFilterBefore(customTokenAuthenticationFilter(AUTHENTICATION_REQUIRED_PATTERN), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/", "/login", "/logout", "/error").permitAll()
                .antMatchers("/getAuthorities").hasAnyAuthority("ROLE_BLOG", "ROLE_ADMIN", "ROLE_ETC")
                .antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .antMatchers("/post/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_BLOG")
                .antMatchers("/board/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_BLOG")
                .anyRequest().authenticated()
                .and()
                .csrf().csrfTokenRepository(getCsrfTokenRepo()).ignoringAntMatchers("/", "/login", "/error")
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(new MyLogoutSuccessHandler());
    }

    private CustomTokenAuthenticationFilter customTokenAuthenticationFilter(String[] patterns) throws Exception {
        return new CustomTokenAuthenticationFilter(getRequiredAuthPath(patterns), this.authenticationManagerBean());
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
        auth.userDetailsService(adminService).passwordEncoder(passwordEncoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public HttpSessionCsrfTokenRepository getCsrfTokenRepo(){
        return new DevelobeerCsrfTokenRepo().getCsrfTokenRepo();
    }
}
