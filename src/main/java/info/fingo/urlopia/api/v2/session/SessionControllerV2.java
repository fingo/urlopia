package info.fingo.urlopia.api.v2.session;

import info.fingo.urlopia.config.authentication.Credentials;
import info.fingo.urlopia.config.authentication.SessionService;
import info.fingo.urlopia.config.authentication.UserData;
import info.fingo.urlopia.history.HistoryLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2/session")
@RequiredArgsConstructor
public class SessionControllerV2 {

    private final SessionService sessionService;
    private final HistoryLogService historyLogService;

    @PostMapping
    public UserData login(@RequestBody Credentials credentials) {
        var userData = sessionService.authenticate(credentials);
        var userEmploymentYear = historyLogService.getEmploymentYear(userData.getUserId());
        userData.setEmploymentYear(userEmploymentYear);
        return userData;
    }
}
