package info.fingo.urlopia.config.authentication.oauth;

import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ad.configuration.enabled", havingValue = "true", matchIfMissing = true)
public class OAuthUserIdInterceptor implements HandlerInterceptor {


    public static final String USER_ID_ATTRIBUTE = "userId";

    private final UserService userService;
    private final JwtTokenValidator jwtTokenValidator;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler){
        var token = request.getHeader("Authorization");
        try{
            var accessToken = jwtTokenValidator.validateAuthorizationHeader(token);
//            var principal = accessToken.getPrincipal(); TODO: go back to checking @ / sth else than name and surname
//            var user = userService.getByPrincipal(principal);

            var firstName = accessToken.getFirstName();
            var lastName = accessToken.getLastName();
            var user = userService.getByFirstNameAndLastName(firstName, lastName);
            request.setAttribute(USER_ID_ATTRIBUTE, user.getId());
        } catch (RuntimeException ignored){
        }
        return true;
    }

}