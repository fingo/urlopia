package info.fingo.urlopia.config.authentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class WebTokenService {

    final static long SESSION_DURATION = 24 * 60 * 60 * (long) 1000; // 1 day in milliseconds
    private WebToken webToken;

    @Value("${webtoken.secret}")
    private String SECRET_KEY;

    public void authorize(HttpServletRequest request) throws Exception{
        webToken = WebToken.fromRequest(request, SECRET_KEY);
    }

    public void authorize(String token) throws Exception{
        webToken = WebToken.fromWebToken(token, SECRET_KEY);
    }

    public List<String> getRoles() {
        return webToken.getRoles();
    }

    public long getUserId() {
        return webToken.getUserId();
    }

    static long getSessionDuration() {
        return SESSION_DURATION;
    }

    public String generateWebToken(Long userId, List<String> roles) {
        return WebToken.fromCredentials(userId, roles).toJsonWebToken(SECRET_KEY);
    }
}
