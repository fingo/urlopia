package info.fingo.urlopia.api.v2.proxy;

import info.fingo.urlopia.api.v2.proxy.presence.PresenceConfirmationProxyInput;
import info.fingo.urlopia.api.v2.proxy.presence.PresenceConfirmationProxyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/proxy")
public class ProxyController {
    private final String proxyToken;
    private final PresenceConfirmationProxyService presenceConfirmationProxyService;

    public ProxyController(@Value("${proxy-token}") String proxyToken,
                           PresenceConfirmationProxyService presenceConfirmationProxyService) {
        this.proxyToken = proxyToken;
        this.presenceConfirmationProxyService = presenceConfirmationProxyService;
    }

    @PostMapping("/presence-confirmations")
    public void confirmPresence(@RequestBody PresenceConfirmationProxyInput proxyInput) {
        validateInput(proxyInput);
        presenceConfirmationProxyService.confirmPresence(proxyInput);
    }

    private void validateInput(ProxyInput proxyInput) {
        if (!proxyInput.token().equals(proxyToken)) {
            throw ProxyException.invalidToken(proxyInput.token());
        }
    }

    @ExceptionHandler(ProxyException.class)
    public ResponseEntity<String> handleException(ProxyException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}
