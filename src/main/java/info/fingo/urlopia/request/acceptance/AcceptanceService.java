package info.fingo.urlopia.request.acceptance;

import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.user.User;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private RequestService requestService;

    public AcceptanceService(AcceptanceRepository acceptanceRepository) {
        this.acceptanceRepository = acceptanceRepository;
    }

    public Page<AcceptanceExcerptProjection> get(Long userId, Pageable pageable) {
        return acceptanceRepository.findByLeaderId(userId, pageable);
    }

    public void create(Request request, User leader) {
        Acceptance acceptance = new Acceptance(request, leader);
        acceptanceRepository.save(acceptance);
    }

    // *** ACTIONS ***

    public void accept(Long acceptanceId) {
        Acceptance acceptance = acceptanceRepository.findOne(acceptanceId);
        Acceptance.Status[] supportedStatuses = {Acceptance.Status.PENDING};
        this.validateStatus(acceptance.getStatus(), supportedStatuses);
        acceptance = this.changeStatus(acceptance, Acceptance.Status.ACCEPTED);

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
        Acceptance.Status[] supportedStatuses = {Acceptance.Status.PENDING};
        this.validateStatus(acceptance.getStatus(), supportedStatuses);
        acceptance = this.changeStatus(acceptance, Acceptance.Status.REJECTED);
        requestService.reject(acceptance.getRequest().getId());
    }

    private void validateStatus(Acceptance.Status status, Acceptance.Status[] supportedStatuses) {
        List<Acceptance.Status> supported = Arrays.asList(supportedStatuses);
        if (!supported.contains(status)) {
            throw new RuntimeException("Status unsupported");
        }
    }

    private Acceptance changeStatus(Acceptance acceptance, Acceptance.Status status) {
        acceptance.setStatus(status);
        return acceptanceRepository.save(acceptance);
    }

}
