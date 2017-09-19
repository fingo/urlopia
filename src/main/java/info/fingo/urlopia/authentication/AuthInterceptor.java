package info.fingo.urlopia.authentication;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

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
    private List<String> currentRoles;

    @Autowired
    private WebTokenService webTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        currentRoles = getRoles(handler);

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
        if (!isAllowed(userRoles)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            LOGGER.info("User role is not allowed for this resource");
            return false;
        }

        //add explicit userId parameter to request
        request.setAttribute(USER_ID_ATTRIBUTE, webTokenService.getUserId());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // Needs to be blank
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Needs to be blank
    }

    private boolean hasRolesAllowed(final HandlerMethod handlerMethod) {
        return handlerMethod.getMethodAnnotation(RolesAllowed.class) != null;
    }

    private List getRoles(Object handler) {
        List<String> roles = null;
        //Check if we intercepted a request to HandlerMethod
        if (handler instanceof HandlerMethod) {
            // This cast is save!
            final HandlerMethod handlerMethod = (HandlerMethod) handler;
            // Check if the method has an annotation for RolesAllowed.
            if (hasRolesAllowed(handlerMethod)) {

                final RolesAllowed rolesAllowedAnnotation = handlerMethod.getMethodAnnotation(RolesAllowed.class);
                roles = Arrays.asList(rolesAllowedAnnotation.value());

                if (roles == null)
                    LOGGER.error("No roles defined for this resource " + handler.toString());
            } else {
                LOGGER.error("No roles defined, deny acces " + handler.toString());
            }
        } else {
            LOGGER.error("Unknown handler mehod");
        }

        // proceed with the HandlerMethod
        return roles;
    }

    private boolean isAllowed(List<String> userRoles) {
        List<String> temp = new ArrayList<>(userRoles);
        temp.retainAll(currentRoles);
        return !temp.isEmpty();
    }
}
