package info.fingo.urlopia.acceptance;

import info.fingo.urlopia.acceptance.events.AcceptanceAccepted;
import info.fingo.urlopia.acceptance.events.AcceptanceCreated;
import info.fingo.urlopia.acceptance.events.AcceptanceRejected;
import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class AcceptanceService {

    private final AcceptanceRepository acceptanceRepository;

    private final RequestService requestService;

    private final ApplicationEventPublisher publisher;

    @Autowired
    public AcceptanceService(AcceptanceRepository acceptanceRepository, RequestService requestService, ApplicationEventPublisher publisher) {
        this.acceptanceRepository = acceptanceRepository;
        this.requestService = requestService;
        this.publisher = publisher;
    }

    public Page<AcceptanceExcerptProjection> get(Long userId, Pageable pageable) {
        return acceptanceRepository.findByLeaderId(userId, pageable);
    }

    public void create(Request request, User leader) {
        Acceptance acceptance = new Acceptance(request, leader);
        acceptance = acceptanceRepository.save(acceptance);
        publisher.publishEvent(new AcceptanceCreated(acceptance));
    }

    // *** ACTIONS ***

    public void accept(Long acceptanceId) {
        Acceptance acceptance = acceptanceRepository.findOne(acceptanceId);
        this.validateStatus(acceptance.getStatus(), Acceptance.Status.PENDING);
        acceptance = this.changeStatus(acceptance, Acceptance.Status.ACCEPTED);
        publisher.publishEvent(new AcceptanceAccepted(acceptance));

        Long requestId = acceptance.getRequest().getId();
        List<Acceptance> userAcceptances = acceptanceRepository.findByRequestId(requestId);
        boolean isRequestAccepted = userAcceptances.stream()
                .noneMatch(accept -> accept.getStatus() != Acceptance.Status.ACCEPTED);
        if (isRequestAccepted) {
            Long deciderId = acceptance.getLeader().getId();
            requestService.accept(requestId, deciderId);
        }
    }

    public void reject(Long acceptanceId) {
        Acceptance acceptance = acceptanceRepository.findOne(acceptanceId);
        this.validateStatus(acceptance.getStatus(), Acceptance.Status.PENDING);
        acceptance = this.changeStatus(acceptance, Acceptance.Status.REJECTED);
        publisher.publishEvent(new AcceptanceRejected(acceptance));
        requestService.reject(acceptance.getRequest().getId());
    }

    public void expire(Long acceptanceId) {
        Acceptance acceptance = acceptanceRepository.findOne(acceptanceId);
        if (acceptance.getStatus() == Acceptance.Status.PENDING) {
            this.changeStatus(acceptance, Acceptance.Status.EXPIRED);
        }
    }

    private void validateStatus(Acceptance.Status status, Acceptance.Status... supportedStatuses) {
        List<Acceptance.Status> supported = Arrays.asList(supportedStatuses);
        if (!supported.contains(status)) {
            throw new RuntimeException("Status not supported");
        }
    }

    private Acceptance changeStatus(Acceptance acceptance, Acceptance.Status status) {
        acceptance.setStatus(status);
        return acceptanceRepository.save(acceptance);
    }

}
