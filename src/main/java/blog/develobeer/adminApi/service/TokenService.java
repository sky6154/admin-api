package blog.develobeer.adminApi.service;

import blog.develobeer.adminApi.domain.admin.user.AuthenticationRequest;
import blog.develobeer.adminApi.filter.DevelobeerAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Deprecated
public class TokenService {
    private final FindByIndexNameSessionRepository sessionRepository;
    private final AuthenticationManager authenticationManager;
    private final AdminService adminService;

    @Autowired
    public TokenService(FindByIndexNameSessionRepository sessionRepository,
                        AuthenticationManager authenticationManager,
                        AdminService adminService) {
        this.sessionRepository = sessionRepository;
        this.authenticationManager = authenticationManager;
        this.adminService = adminService;
    }

    public Map<String, ? extends Session> getSessionByName(String name) {
        Map<String, ? extends Session> sessions = sessionRepository.findByPrincipalName(name);

        if (sessions == null || sessions.size() < 1) {
            throw new CredentialsExpiredException("Token is not found.");
        } else {
            return sessions;
        }
    }

    public void updateAccessTime(Session session) {
        sessionRepository.save(session);
    }

    public boolean login(AuthenticationRequest authenticationRequest) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        try {
            Map<String, ? extends Session> sessions = this.getSessionByName(authentication.getName());

            // 기존 로그인된 세션을 모두 지운다.
            sessions.forEach((s, o) -> {
                sessionRepository.deleteById(o.getId());
            });
        }
        catch(CredentialsExpiredException ce){
            // 토큰이 없을 경우 스킵
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean logout(String token) {
        try {
            String decodedString = DevelobeerAuthenticationToken.decode(token);
            String[] info = DevelobeerAuthenticationToken.splitToken(decodedString);

            String userName = info[0];
            String sessionId = info[1];

            Map<String, ? extends Session> sessions = this.getSessionByName(userName);

            // 기존 로그인된 세션을 모두 지운다.
            sessions.forEach((s, o) -> {
                sessionRepository.deleteById(o.getId());
            });

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
