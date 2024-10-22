package info.fingo.urlopia.config.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AccessDeniedExceptionHandler implements AccessDeniedHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessDeniedExceptionHandler.class);

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex) throws IOException, ServletException {
        LOGGER.warn("Access denied", ex);
        response.setStatus(HttpStatus.FORBIDDEN.value());
    }
}