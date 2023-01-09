package info.fingo.urlopia.config.authentication.noauth;

import info.fingo.urlopia.config.authentication.oauth.OAuthUserIdInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ad.configuration.enabled", havingValue = "false")
public class NoAuthUserIdInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler){
        var userId = request.getHeader(OAuthUserIdInterceptor.USER_ID_ATTRIBUTE);
        request.setAttribute(OAuthUserIdInterceptor.USER_ID_ATTRIBUTE, Long.valueOf(userId));
        return true;
    }

}