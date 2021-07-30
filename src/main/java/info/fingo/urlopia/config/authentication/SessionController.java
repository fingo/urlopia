package info.fingo.urlopia.config.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/session")
public class SessionController {

    private final SessionService sessionService;

    @Autowired
    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<UserData> login(@RequestBody Credentials credentials) {
        var userData = sessionService.authenticate(credentials);
        return ResponseEntity.ok(userData);
    }

    @GetMapping(value = "/version", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getAppVersion() {
        return sessionService.getAppVersion();
    }

}
