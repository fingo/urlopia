package info.fingo.urlopia.config.authentication.oauth;

import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.JsonObject;
import info.fingo.urlopia.api.v2.oauth.OAuthRedirectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPublicKey;
import java.time.Instant;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Component
public class JwtTokenValidator {
    private final JwkProvider jwkProvider;
    private final JwtTokenAuthoritiesProvider jwtTokenAuthoritiesProvider;

    public AccessToken validateAuthorizationHeader(String authorizationHeader) throws InvalidTokenException {
        var tokenValue = subStringBearer(authorizationHeader);
        validateToken(tokenValue);
        return new AccessToken(tokenValue, jwtTokenAuthoritiesProvider);
    }

    private void validateToken(String value) {
        DecodedJWT decodedJWT = decodeToken(value);
        verifyTokenHeader(decodedJWT);
        verifySignature(decodedJWT);
        verifyPayload(decodedJWT);
    }

    private DecodedJWT decodeToken(String value) {
        if (isNull(value)){
            throw new InvalidTokenException("Token has not been provided");
        }
        return JWT.decode(value);
    }

    private void verifyTokenHeader(DecodedJWT decodedJWT) {
        var isJWTToken = decodedJWT.getType().equals("JWT");
        if (!isJWTToken) {
            throw new InvalidTokenException("Token is not JWT type");
        }
    }

    private void verifySignature(DecodedJWT decodedJWT) {
        try {
            var jwk = jwkProvider.get(decodedJWT.getKeyId());
            var publicKey = (RSAPublicKey) jwk.getPublicKey();
            var algorithm = Algorithm.RSA256(publicKey, null);
            algorithm.verify(decodedJWT);
        } catch (JwkException | SignatureVerificationException ex) {
            throw new InvalidTokenException("Token has invalid signature");
        }
    }

    private void verifyPayload(DecodedJWT decodedJWT) {
        var payloadAsJson = JwtUtils.decodeTokenPayloadToJsonObject(decodedJWT);
        if (hasTokenExpired(payloadAsJson)) {
            throw new InvalidTokenException("Token has expired");
        }
    }

    private boolean hasTokenExpired(JsonObject payloadAsJson) {
        var expirationDatetime = extractExpirationDate(payloadAsJson);
        return Instant.now().isAfter(expirationDatetime);
    }

    private Instant extractExpirationDate(JsonObject payloadAsJson) {
        try {
            return Instant.ofEpochSecond(payloadAsJson.get("exp").getAsLong());
        } catch (NullPointerException ex) {
            throw new InvalidTokenException("There is no 'exp' claim in the token payload");
        }
    }

    private String subStringBearer(String authorizationHeader) {
        try {
            return authorizationHeader.replace(OAuthRedirectService.BEARER_PREFIX, "");
        } catch (Exception ex) {
            throw new InvalidTokenException("There is no AccessToken in a request header");
        }
    }
}

