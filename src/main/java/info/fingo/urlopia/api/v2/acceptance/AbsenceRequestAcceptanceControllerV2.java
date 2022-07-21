package info.fingo.urlopia.api.v2.acceptance;

import info.fingo.urlopia.acceptance.AcceptanceExcerptProjection;
import info.fingo.urlopia.acceptance.AcceptanceService;
import info.fingo.urlopia.api.v2.exceptions.InvalidActionException;
import info.fingo.urlopia.config.authentication.UserIdInterceptor;
import info.fingo.urlopia.config.persistance.filter.Filter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v2/absence-request-acceptances")
public class AbsenceRequestAcceptanceControllerV2 {
    private final AcceptanceService acceptanceService;

    private final ModelMapper modelMapper = new ModelMapper();

    private AcceptancesOutput mapAcceptanceOutput(AcceptanceExcerptProjection projection) {
        return modelMapper.map(projection, AcceptancesOutput.class);
    }

    @Autowired
    public AbsenceRequestAcceptanceControllerV2(AcceptanceService acceptanceService) {
        this.acceptanceService = acceptanceService;
    }

    @RolesAllowed("ROLES_LEADER")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<AcceptancesOutput> getAcceptances(@RequestParam(name = "filter", defaultValue = "") String[] filters,
                                                  Pageable pageable,
                                                  HttpServletRequest httpRequest) {
        var authenticatedId = (Long) httpRequest.getAttribute(UserIdInterceptor.USER_ID_ATTRIBUTE);
        var filter = Filter.from(filters);
        var acceptances = acceptanceService.get(authenticatedId, filter, pageable);

        return acceptances.map(this::mapAcceptanceOutput);
    }

    @RolesAllowed("ROLES_LEADER")
    @GetMapping(value = "/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<AcceptanceHistoryOutput> getAcceptancesHistory(@RequestParam(name = "filter", defaultValue = "") String[] filters,
                                                               Pageable pageable,
                                                               HttpServletRequest httpRequest) {
        var authenticatedId = (Long) httpRequest.getAttribute(UserIdInterceptor.USER_ID_ATTRIBUTE);
        var filter = Filter.from(filters);
        return acceptanceService.getHistory(authenticatedId, filter, pageable);
    }

    @RolesAllowed("ROLES_LEADER")
    @PatchMapping(value = "/{acceptanceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AcceptanceStatus updateAcceptanceStatus(@PathVariable Long acceptanceId,
                                                   @RequestBody AcceptanceStatus status,
                                                   HttpServletRequest httpRequest) {
        var authenticatedId = (Long) httpRequest.getAttribute(UserIdInterceptor.USER_ID_ATTRIBUTE);

        switch (status.status()) {
            case ACCEPTED -> acceptanceService.accept(acceptanceId, authenticatedId);
            case REJECTED -> acceptanceService.reject(acceptanceId, authenticatedId);
            default -> throw InvalidActionException.invalidAction();
        }

        var acceptance = acceptanceService.getAcceptance(acceptanceId);
        return new AcceptanceStatus(acceptance.getStatus());
    }
}
