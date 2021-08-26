package info.fingo.urlopia.acceptance;

import info.fingo.urlopia.acceptance.events.AcceptanceAccepted;
import info.fingo.urlopia.acceptance.events.AcceptanceCreated;
import info.fingo.urlopia.acceptance.events.AcceptanceRejected;
import info.fingo.urlopia.api.v2.exceptions.UnauthorizedException;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.config.persistance.filter.Operator;
import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
@Slf4j
public class AcceptanceService {

    private final AcceptanceRepository acceptanceRepository;
    private final RequestService requestService;
    private final ApplicationEventPublisher publisher;

    public AcceptanceService(AcceptanceRepository acceptanceRepository,
                             RequestService requestService,
                             ApplicationEventPublisher publisher) {
        this.acceptanceRepository = acceptanceRepository;
        this.requestService = requestService;
        this.publisher = publisher;
    }

    public Page<AcceptanceExcerptProjection> get(Long userId,
                                                 Filter filter,
                                                 Pageable pageable) {
        var filterWithRestrictions = filter.toBuilder()
                .and("leader.id", Operator.EQUAL, userId.toString())
                .build();
        return acceptanceRepository.findAll(filterWithRestrictions, pageable, AcceptanceExcerptProjection.class);
    }

    public void create(Request request,
                       User leader) {
        var acceptance = acceptanceRepository.save(new Acceptance(request, leader));
        publisher.publishEvent(new AcceptanceCreated(acceptance));
        var loggerInfo = "New acceptance with id: %d has been created"
                .formatted(acceptance.getId());
        log.info(loggerInfo);
    }

    public List<Acceptance> getAcceptancesByRequestId(Long requestId) {
        return acceptanceRepository.findByRequestId(requestId);
    }


    // *** ACTIONS ***
    public Acceptance getAcceptance(Long acceptanceId) {
        return acceptanceRepository
                .findById(acceptanceId)
                .orElseThrow(() -> {
                    log.error("There is no acceptance with id: {}", acceptanceId);
                    return NoSuchAcceptanceException.invalidId();
                });
    }

    public void accept(Long acceptanceId, Long deciderId) {
        var acceptance = getAcceptance(acceptanceId);
        var leaderId = acceptance.getLeader().getId();
        var isCurrentUserAuthorizedToUpdateAcceptance = leaderId.equals(deciderId);

        if (!isCurrentUserAuthorizedToUpdateAcceptance) {
            log.error("Acceptance with id: {} could not be accepted because user with id: {} who tried to" +
                    " perform this operation is not authorized", acceptanceId, deciderId);
            throw UnauthorizedException.unauthorized();
        }

        validateStatus(acceptance.getStatus(), Acceptance.Status.PENDING);
        acceptance = changeStatus(acceptance, Acceptance.Status.ACCEPTED);
        publisher.publishEvent(new AcceptanceAccepted(acceptance));

        var requestId = acceptance.getRequest().getId();
        var userAcceptances = acceptanceRepository.findByRequestId(requestId);

        var isRequestAccepted = userAcceptances.stream()
                .noneMatch(accept ->
                        accept.getStatus() != Acceptance.Status.ACCEPTED);
        if (isRequestAccepted) {
            requestService.accept(requestId, deciderId);
        }
    }

    public void reject(Long acceptanceId, Long deciderId) {
        var acceptance = getAcceptance(acceptanceId);
        var leaderId = acceptance.getLeader().getId();
        var isCurrentUserAuthorizedToUpdateAcceptance = leaderId.equals(deciderId);

        if (!isCurrentUserAuthorizedToUpdateAcceptance) {
            log.error("Acceptance with id: {} could not be rejected because user with id: {} who tried to" +
                    " perform this operation is not authorized", acceptanceId, deciderId);
            throw UnauthorizedException.unauthorized();
        }

        validateStatus(acceptance.getStatus(), Acceptance.Status.PENDING);
        acceptance = changeStatus(acceptance, Acceptance.Status.REJECTED);
        publisher.publishEvent(new AcceptanceRejected(acceptance));
        requestService.reject(
                acceptance.getRequest().getId());
    }

    public void expire(Long acceptanceId) {
        var acceptance = getAcceptance(acceptanceId);

        if (acceptance.getStatus() == Acceptance.Status.PENDING) {
            this.changeStatus(acceptance, Acceptance.Status.EXPIRED);
            var loggerInfo = "Acceptance with id: %d has expired"
                    .formatted(acceptanceId);
            log.info(loggerInfo);
        }
    }

    private void validateStatus(Acceptance.Status status,
                                Acceptance.Status... supportedStatuses) {
        var supported = Arrays.asList(supportedStatuses);
        if (!supported.contains(status)) {
            log.error("Status {} not supported", status.toString());
            throw StatusNotSupportedException.invalidStatus(status.toString());
        }
    }

    private Acceptance changeStatus(Acceptance acceptance,
                                    Acceptance.Status status) {
        acceptance.setStatus(status);
        var loggerInfo = ("Status of acceptance with id: %d " +
                "has been changed to: %s by leader with id: %d")
                .formatted(acceptance.getId(), status.toString(),
                        acceptance.getLeader().getId());
        log.info(loggerInfo);
        return acceptanceRepository.save(acceptance);
    }

}
