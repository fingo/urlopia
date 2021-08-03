package info.fingo.urlopia.api.v2;

import info.fingo.urlopia.config.authentication.Credentials;
import info.fingo.urlopia.config.authentication.SessionService;
import info.fingo.urlopia.config.authentication.UserData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2/session")
public class SessionControllerV2 {

    private final SessionService sessionService;

    public SessionControllerV2(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<UserData> login(@RequestBody Credentials credentials) {
        var userData = sessionService.authenticate(credentials);
        return ResponseEntity.ok(userData);
    }
}
