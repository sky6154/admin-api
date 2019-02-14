package blog.develobeer.adminApi.config;

import blog.develobeer.adminApi.filter.CustomTokenAuthenticationFilter;
import blog.develobeer.adminApi.filter.RestAuthenticationEntryPoint;
import blog.develobeer.adminApi.service.TokenService;
import blog.develobeer.adminApi.service.UserService;
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
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
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

    @Autowired
    private UserService userService;

    @Autowired
    FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    private String[] AUTHENTICATION_REQUIRED_PATTERN = {"/admin/**", "/post/**", "/board/**"};

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .sessionManagement()
                    .maximumSessions(1)
                    .sessionRegistry(sessionRegistry())
                    .maxSessionsPreventsLogin(true)
                    .and()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .addFilterBefore(customTokenAuthenticationFilter(AUTHENTICATION_REQUIRED_PATTERN), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                    .antMatchers("/", "/home", "/test", "/login", "/post/**", "/board/**", "/temp").permitAll()
                    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .antMatchers(AUTHENTICATION_REQUIRED_PATTERN).hasAnyAuthority("ROLE_ADMIN")
                    .anyRequest().authenticated()
                    .and()
                .httpBasic().disable()
                .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                    .invalidateHttpSession(true);
    }

    private CustomTokenAuthenticationFilter customTokenAuthenticationFilter(String[] patterns){
        return new CustomTokenAuthenticationFilter(getRequiredAuthPath(patterns), this.authenticationManager, this.tokenService, this.userService);
    }

    public OrRequestMatcher getRequiredAuthPath(String[] patterns){
        List<RequestMatcher> requestMatchers = new ArrayList<>();

        for(String pattern : patterns) {
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
        auth.userDetailsService(userService).passwordEncoder(getPasswordEncoder());
    }

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
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
