package info.fingo.urlopia.config.authentication;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.lang.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String USER_ID_ATTRIBUTE = "userId";

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthInterceptor.class);

    private final WebTokenService webTokenService;

    public AuthInterceptor(WebTokenService webTokenService) {
        this.webTokenService = webTokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        var rolesAllowed = getRoles(handler);

        try {
            webTokenService.authorize(request);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            LOGGER.error("Invalid token ", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        } catch (ExpiredJwtException e) {
            LOGGER.info("Session expired ", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        } catch (Exception e) {
            LOGGER.error("Unhandled exception ", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        List<String> userRoles = webTokenService.getRoles();
        if (!Collections.containsAny(rolesAllowed, userRoles)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            LOGGER.info("User role is not allowed for this resource");
            return false;
        }

        //add explicit userId parameter to request
        request.setAttribute(USER_ID_ATTRIBUTE, webTokenService.getUserId());
        return true;
    }

    private List<String> getRoles(Object handler) {
        List<String> roles = new ArrayList<>();

        if (handler instanceof HandlerMethod handlerMethod) {
            RolesAllowed annotation = handlerMethod.getMethodAnnotation(RolesAllowed.class);
            if (annotation != null) {
                roles = Arrays.asList(annotation.value());
                if (roles.isEmpty())
                    LOGGER.error("No roles defined for this resource " + handler);
            }
        }

        return roles;
    }

}
