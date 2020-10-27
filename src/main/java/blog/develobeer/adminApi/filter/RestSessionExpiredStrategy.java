package blog.develobeer.adminApi.filter;

import org.springframework.http.HttpStatus;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RestSessionExpiredStrategy implements SessionInformationExpiredStrategy {
    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException {
        HttpServletResponse response = event.getResponse();

        response.sendError(HttpStatus.UNAUTHORIZED.value(), "Session is expired.");
    }
}
