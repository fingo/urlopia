package info.fingo.urlopia.api.v2.presence;

import info.fingo.urlopia.config.authentication.oauth.OAuthUserIdInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v2/presence-confirmations")
@RequiredArgsConstructor
public class PresenceConfirmationControllerV2 {
    private final PresenceConfirmationService presenceConfirmationService;

    @RolesAllowed({"ROLES_WORKER", "ROLES_ADMIN"})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PresenceConfirmationInputOutput> getPresenceConfirmations(
            @RequestParam(name = "filter", defaultValue = "") String[] filters, HttpServletRequest httpRequest) {
        var authenticatedUserId = (Long) httpRequest.getAttribute(OAuthUserIdInterceptor.USER_ID_ATTRIBUTE);
        var presenceConfirmations = presenceConfirmationService.getPresenceConfirmations(authenticatedUserId, filters);
        return PresenceConfirmationInputOutput.listFrom(presenceConfirmations);
    }

    @RolesAllowed({"ROLES_WORKER", "ROLES_ADMIN"})
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public PresenceConfirmationInputOutput savePresenceConfirmation(
            @RequestBody PresenceConfirmationInputOutput inputDto, HttpServletRequest httpRequest) {
        var authenticatedUserId = (Long) httpRequest.getAttribute(OAuthUserIdInterceptor.USER_ID_ATTRIBUTE);
        var addedPresenceConfirmation = presenceConfirmationService.confirmPresence(authenticatedUserId, inputDto);
        return PresenceConfirmationInputOutput.from(addedPresenceConfirmation);
    }
}
