package info.fingo.urlopia.config.authentication

import info.fingo.urlopia.api.v2.exceptions.UnauthorizedException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import spock.lang.Specification
import javax.servlet.http.HttpServletRequest

class WebTokenServiceSpec extends Specification{
    private WebToken webToken
    private WebTokenService webTokenService
    private static final ADMIN_STRING = "ROLES_ADMIN"
    private static final USER_STRING = "ROLES_WORKER"
    private static final LEADER_STRING = "ROLES_LEADER"
    private static final SECRET_KEY = "test"


    void setup(){
        webTokenService = new WebTokenService(SECRET_KEY);
        webToken = Mock(WebToken){
            userId >> 5
        }
    }

    def static toJsonWebToken(String secretKey, userId, roles) {
        def issuedAt = new Date(3000,1,1)
        def expiration = new Date(3000,12,31)
        return Jwts.builder()
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .setSubject(String.valueOf(userId))
                .claim("role", roles)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    def "ensureAdmin() WHEN called for webToken from admin SHOULD not thrown UnauthorizedException"(){
        given:
        def httpRequest = Mock(HttpServletRequest){
            getHeader("authorization") >> toJsonWebToken(SECRET_KEY,webToken.userId,[ADMIN_STRING])
        }
        when:
        webTokenService.authorize(httpRequest)
        webTokenService.ensureAdmin()

        then:
        notThrown(UnauthorizedException)
    }

    def "ensureAdmin() WHEN called for webToken from not admin SHOULD  thrown UnauthorizedException"(){
        given:
        def httpRequest = Mock(HttpServletRequest){
            getHeader("authorization") >> toJsonWebToken(SECRET_KEY,webToken.userId,[LEADER_STRING,USER_STRING])
        }

        when:
        webTokenService.authorize(httpRequest)
        webTokenService.ensureAdmin()

        then:
        thrown(UnauthorizedException)


    }

    def "isCurrentUserAnAdmin() WHEN user is an admin SHOULD return true"() {
        def httpRequest = Mock(HttpServletRequest){
            getHeader("authorization") >> toJsonWebToken(SECRET_KEY,webToken.userId,[ADMIN_STRING])
        }

        when:
        webTokenService.authorize(httpRequest)
        def result = webTokenService.isCurrentUserAnAdmin()

        then:
        result
    }

    def "isCurrentUserAnAdmin() WHEN user is not an admin SHOULD return false"() {
        given:

        def httpRequest = Mock(HttpServletRequest){
            getHeader("authorization") >> toJsonWebToken(SECRET_KEY,webToken.userId,[LEADER_STRING,USER_STRING])
        }

        when:
        webTokenService.authorize(httpRequest)
        def result = webTokenService.isCurrentUserAnAdmin()

        then:
        !result
    }


}


