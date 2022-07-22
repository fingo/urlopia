package info.fingo.urlopia.config.authentication

import info.fingo.urlopia.api.v2.exceptions.UnauthorizedException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import spock.lang.Specification

class WebTokenServiceSpec extends Specification{
    private WebToken webToken
    private WebTokenService webTokenService
    private static final ADMIN_STRING = "ROLES_ADMIN"
    private static final USER_STRING = "ROLES_WORKER"
    private static final LEADER_STRING = "ROLES_LEADER"
    private static final SECRET_KEY = "test"


    void setup(){
        webTokenService = new WebTokenService(SECRET_KEY)
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
                .compact()
    }

    def static getClaimFromToken(String token){
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
    }

    def "ensureAdmin() WHEN called for webToken from admin SHOULD not thrown UnauthorizedException"(){
        given:
        def token = toJsonWebToken(SECRET_KEY,webToken.userId,[ADMIN_STRING])
        def claim = getClaimFromToken(token)

        when:
        webTokenService.setWebToken(claim)
        webTokenService.ensureAdmin()

        then:
        notThrown(UnauthorizedException)
    }

    def "ensureAdmin() WHEN called for webToken from not admin SHOULD  thrown UnauthorizedException"(){
        given:
        def token = toJsonWebToken(SECRET_KEY,webToken.userId,[LEADER_STRING,USER_STRING])
        def claim = getClaimFromToken(token)

        when:
        webTokenService.setWebToken(claim)
        webTokenService.ensureAdmin()

        then:
        thrown(UnauthorizedException)


    }

    def "isCurrentUserAnAdmin() WHEN user is an admin SHOULD return true"() {
        def token = toJsonWebToken(SECRET_KEY,webToken.userId,[ADMIN_STRING])
        def claim = getClaimFromToken(token)

        when:
        webTokenService.setWebToken(claim)
        def result = webTokenService.isCurrentUserAnAdmin()

        then:
        result
    }

    def "isCurrentUserAnAdmin() WHEN user is not an admin SHOULD return false"() {
        given:
        def token = toJsonWebToken(SECRET_KEY,webToken.userId,[LEADER_STRING,USER_STRING])
        def claim = getClaimFromToken(token)


        when:
        webTokenService.setWebToken(claim)
        def result = webTokenService.isCurrentUserAnAdmin()

        then:
        !result
    }


}


