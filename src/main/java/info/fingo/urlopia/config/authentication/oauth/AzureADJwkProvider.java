package info.fingo.urlopia.config.authentication.oauth;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class AzureADJwkProvider implements JwkProvider {

    @Value("${azure.ad.clientId}")
    private String clientId;

    @Value("${azure.ad.tenantId}")
    private String tenantId;

    private static final String AZURE_CONFIG_URL_TEMPLATE = "https://login.microsoftonline.com/%s/discovery/v2.0/keys?appid=%s";
    @Override
    public Jwk get(String keyId) throws JwkException {
        try{
            var urlString = String.format(AZURE_CONFIG_URL_TEMPLATE, tenantId, clientId);
            var url = new URL(urlString);
            var delegateProvider = new UrlJwkProvider(url);
            return delegateProvider.get(keyId);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
