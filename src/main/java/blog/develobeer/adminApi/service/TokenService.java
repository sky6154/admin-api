package blog.develobeer.adminApi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TokenService<S extends Session> {
    @Autowired
    private FindByIndexNameSessionRepository sessionRepository;

    public Map<String, ? extends Session> getSessionByName(String name){
        return sessionRepository.findByPrincipalName(name);
    }

    public void updateAccessTime(S session){
        sessionRepository.save(session);
    }
}
