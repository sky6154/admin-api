package blog.develobeer.adminApi.service;

import blog.develobeer.adminApi.domain.admin.user.AuthenticationRequest;
import blog.develobeer.adminApi.domain.admin.user.AuthenticationToken;
import blog.develobeer.adminApi.filter.DevelobeerAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

@Service
public class TokenService<S extends Session> {
    private final FindByIndexNameSessionRepository sessionRepository;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Autowired
    public TokenService(FindByIndexNameSessionRepository sessionRepository,
                        AuthenticationManager authenticationManager,
                        UserService userService) {
        this.sessionRepository = sessionRepository;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    public Map<String, ? extends Session> getSessionByName(String name) {
        return sessionRepository.findByPrincipalName(name);
    }

    public void updateAccessTime(S session) {
        sessionRepository.save(session);
    }

    public AuthenticationToken login(AuthenticationRequest authenticationRequest, HttpSession session) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

//        SecurityContextHolder.getContext().setAuthentication(authentication);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        Map<String, ? extends Session> sessions = this.getSessionByName(authentication.getName());

        // 기존 로그인된 세션을 모두 지운다.
        sessions.forEach((s, o) -> {
            sessionRepository.deleteById(o.getId());
        });

        session.setAttribute(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, authentication.getName());

        String token = DevelobeerAuthenticationToken.encode(authentication.getName(), session.getId());

//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        return new AuthenticationToken(authentication.getName(), authentication.getAuthorities(), token);
    }

    public boolean logout(String token) {
        String decodedString = DevelobeerAuthenticationToken.decode(token);
        String[] info = DevelobeerAuthenticationToken.splitToken(decodedString);

        if (info.length < 1) {
            throw new AuthenticationCredentialsNotFoundException("Token is not found.");
        } else {
            String userName = info[0];
            String sessionId = info[1];

            Map<String, ? extends Session> result = this.getSessionByName(userName);

            if (result.size() < 1) {
                throw new AuthenticationCredentialsNotFoundException("Token is not found.");
            } else {
                Session session = result.get(sessionId);

                if (session == null) {
                    throw new CredentialsExpiredException("Token is not valid.");
                }

                if (session.isExpired()) {
                    throw new CredentialsExpiredException("Token is expired.");
                } else {
                    Map<String, ? extends Session> sessions = this.getSessionByName(userName);

                    // 기존 로그인된 세션을 모두 지운다.
                    sessions.forEach((s, o) -> {
                        sessionRepository.deleteById(o.getId());
                    });

                    return true;
                }
            }
        }
    }


    public Collection<? extends GrantedAuthority> authCheck(String token) {
        if (token == null) {
            return null;
        }

        String decodedString = DevelobeerAuthenticationToken.decode(token);

        if (decodedString == null) {
            throw new AuthenticationServiceException("Invalid token");
        }

        String[] info = DevelobeerAuthenticationToken.splitToken(decodedString);

        if (info.length < 1) {
            throw new AuthenticationCredentialsNotFoundException("Token is not found.");
        } else {
            String userName = info[0];
            String sessionId = info[1];

            Map<String, ? extends Session> result = this.getSessionByName(userName);

            if (result.size() < 1) {
                throw new AuthenticationCredentialsNotFoundException("Token is not found.");
            } else {
                Session session = result.get(sessionId);

                if (session == null) {
                    throw new CredentialsExpiredException("Token is invalid.");
                }

                if (session.isExpired()) {
                    throw new CredentialsExpiredException("Token is expired.");
                } else {
                    session.setLastAccessedTime(new Date().toInstant());
                    this.updateAccessTime((S) session);

                    UserDetails userDetails = userService.loadUserByUsername(userName);

                    try {
                        return userDetails.getAuthorities();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return null;
    }
}
