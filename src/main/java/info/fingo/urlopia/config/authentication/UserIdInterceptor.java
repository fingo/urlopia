package info.fingo.urlopia.config.authentication;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserIdInterceptor implements HandlerInterceptor {


    public static final String USER_ID_ATTRIBUTE = "userId";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler){
        var token = request.getHeader("Authorization");
        var claim = JwtUtils.getClaimFromToken(token);
        claim.ifPresent(claims -> request.setAttribute(USER_ID_ATTRIBUTE, Long.valueOf(claims.getSubject())));
        return true;
    }



}