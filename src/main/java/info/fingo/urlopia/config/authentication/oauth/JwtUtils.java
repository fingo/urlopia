package info.fingo.urlopia.config.authentication.oauth;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class JwtUtils {
    private static final String PRINCIPAL_KEY = "unique_name";

    public static JsonObject decodeTokenPayloadToJsonObject(DecodedJWT decodedJWT) {
        try {
            String payloadAsString = decodedJWT.getPayload();
            return new Gson().fromJson(
                    new String(Base64.getUrlDecoder().decode(payloadAsString), StandardCharsets.UTF_8),
                    JsonObject.class);
        } catch (RuntimeException exception) {
            throw new InvalidTokenException("Invalid JWT or JSON format of each of the jwt parts", exception);
        }
    }

    public static String getAccountNameFromDecodedToken(DecodedJWT decodedToken) {
        var payloadAsJson = decodeTokenPayloadToJsonObject(decodedToken);
        var principal = payloadAsJson.getAsJsonPrimitive(PRINCIPAL_KEY).getAsString();
        return principal.substring(0, principal.indexOf("@"));
    }
}
