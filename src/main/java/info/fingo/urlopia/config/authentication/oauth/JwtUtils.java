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
    private static final String FIRST_NAME_KEY = "given_name";
    private static final String LAST_NAME_KEY = "family_name";


    public static String getPrincipalNameFromDecodedToken(DecodedJWT decodedToken){
        var payloadAsJson = decodeTokenPayloadToJsonObject(decodedToken);
        return payloadAsJson.getAsJsonPrimitive(PRINCIPAL_KEY).getAsString();
    }

    public static JsonObject decodeTokenPayloadToJsonObject(DecodedJWT decodedJWT) {
        try {
            String payloadAsString = decodedJWT.getPayload();
            return new Gson().fromJson(
                    new String(Base64.getDecoder().decode(payloadAsString), StandardCharsets.UTF_8),
                    JsonObject.class);
        } catch (RuntimeException exception) {
            throw new InvalidTokenException("Invalid JWT or JSON format of each of the jwt parts", exception);
        }
    }

    //TEMPORARY FIX

    public static String getFirstNameFromDecodedToken(DecodedJWT decodedToken){
        var payloadAsJson = decodeTokenPayloadToJsonObject(decodedToken);
        return payloadAsJson.getAsJsonPrimitive(FIRST_NAME_KEY).getAsString();
    }

    public static String getLastNameFromDecodedToken(DecodedJWT decodedToken){
        var payloadAsJson = decodeTokenPayloadToJsonObject(decodedToken);
        return payloadAsJson.getAsJsonPrimitive(LAST_NAME_KEY).getAsString();
    }
}
