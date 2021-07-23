package info.fingo.urlopia.config.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * JSON Web Token generator.
 * methods have package-private access type
 * Created by Jakub Licznerski on 18.10.2016.
 */
public class WebToken {

    private long userId;
    private Date issuedAt;
    private Date expiration;
    private List<String> roles;

    private WebToken(long userId, List<String> roles) {
        this.userId = userId;
        this.issuedAt = new Date();
        this.expiration = new Date(issuedAt.getTime() + WebTokenService.getSessionDuration());
        this.roles = roles;
    }


    static WebToken fromCredentials(long userId, List<String> roles) {
        return new WebToken(userId, roles);
    }

    static WebToken fromWebToken(String token, String secretKey) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return new WebToken(Long.valueOf(claims.getSubject()), claims.get("role", List.class));
    }

    static WebToken fromRequest(HttpServletRequest request, String secretKey) throws Exception {
        return fromWebToken(request.getHeader("authorization"), secretKey);
    }

    String toJsonWebToken(String secretKey) {
        return Jwts.builder()
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .setSubject(String.valueOf(userId))
                .claim("role", roles)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    long getUserId() {
        return userId;
    }

    List<String> getRoles() {
        return roles;
    }
}
