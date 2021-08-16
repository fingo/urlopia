package info.fingo.urlopia.api.v2.presence;

import info.fingo.urlopia.config.authentication.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v2/presence-confirmations")
@RequiredArgsConstructor
public class PresenceConfirmationControllerV2 {
    private final PresenceConfirmationService presenceConfirmationService;

    @RolesAllowed({"ROLES_WORKER", "ROLES_ADMIN"})
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PresenceConfirmationInputOutput> savePresenceConfirmation(
            @RequestBody PresenceConfirmationInputOutput inputDto, HttpServletRequest httpRequest) {
        var authenticatedUserId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);
        var addedPresenceConfirmation = presenceConfirmationService.confirmPresence(authenticatedUserId, inputDto);
        var outputDto = PresenceConfirmationInputOutput.from(addedPresenceConfirmation);
        return ResponseEntity.status(HttpStatus.CREATED).body(outputDto);
    }
}
