package info.fingo.urlopia.api.v2.request;

import info.fingo.urlopia.acceptance.AcceptanceService;
import info.fingo.urlopia.api.v2.exceptions.InvalidActionException;
import info.fingo.urlopia.config.authentication.oauth.OAuthUserIdInterceptor;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.request.RequestInput;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.request.absence.SpecialAbsence;
import info.fingo.urlopia.request.absence.SpecialAbsenceRequestInput;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v2/absence-requests")
@RequiredArgsConstructor
public class AbsenceRequestControllerV2 {
    private final AcceptanceService acceptanceService;

    private final RequestService requestService;


    @RolesAllowed("ROLES_ADMIN")
    @PostMapping(value = "/special-absence", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public RequestsOutput createSpecialAbsence(@RequestBody SpecialAbsence specialAbsence) {
        var requesterId = specialAbsence.requesterId();
        var requestInput = SpecialAbsenceRequestInput.fromSpecialAbsence(specialAbsence);
        var request = requestService.create(requesterId, requestInput);
        return RequestsOutput.fromRequest(request);
    }

    @RolesAllowed("ROLES_WORKER")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public RequestsOutput create(@RequestBody RequestInput input,
                                    HttpServletRequest httpRequest) {
        var authenticatedId = (Long) httpRequest.getAttribute(OAuthUserIdInterceptor.USER_ID_ATTRIBUTE);
        var request = requestService.create(authenticatedId, input);
        request = requestService.getById(request.getId());
        var acceptances = acceptanceService.getAcceptancesByRequestId(request.getId());
        return RequestsOutput.fromRequest(request, acceptances);
    }

    @RolesAllowed("ROLES_WORKER")
    @GetMapping(value = "/requester/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<RequestsOutput> getMyRequests(@RequestParam(name = "filter", defaultValue = "") String[] filters,
                                              Pageable pageable,
                                              HttpServletRequest httpRequest) {
        var authenticatedId = (Long) httpRequest.getAttribute(OAuthUserIdInterceptor.USER_ID_ATTRIBUTE);
        var filter = Filter.from(filters);
        var requestsPage = requestService.getFromUser(authenticatedId, filter, pageable);
        return requestsPage.map(RequestsOutput::fromRequestExcerptProjection);
    }

    @RolesAllowed("ROLES_ADMIN")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<RequestsOutput> getAllRequests(@RequestParam(name = "filter", defaultValue = "") String[] filters,
                                               Pageable pageable) {
        var filter = Filter.from(filters);
        var requestsPage = requestService.get(filter, pageable);
        return requestsPage.map(RequestsOutput::fromRequestExcerptProjection);
    }

    @RolesAllowed({"ROLES_WORKER", "ROLES_ADMIN"})
    @PatchMapping(value = "/{requestId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestStatus updateAbsenceRequestStatus(@PathVariable Long requestId,
                                                    @RequestBody RequestStatus status,
                                                    HttpServletRequest httpRequest) {
        var authenticatedId = (Long) httpRequest.getAttribute(OAuthUserIdInterceptor.USER_ID_ATTRIBUTE);

        switch (status.status()) {
            case CANCELED -> requestService.cancel(requestId, authenticatedId);
            case ACCEPTED -> requestService.validateAdminPermissionAndAccept(requestId, authenticatedId);
            case REJECTED -> requestService.validateAdminPermissionAndReject(requestId);
            default -> throw InvalidActionException.invalidAction();
        }

        var request = requestService.getById(requestId);
        return new RequestStatus(request.getStatus());
    }
}
