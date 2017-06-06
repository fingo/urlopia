package info.fingo.urlopia.request.acceptance;

import info.fingo.urlopia.events.DecisionResultEvent;
import info.fingo.urlopia.events.NewRequestNotificationEvent;
import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.request.RequestDTO;
import info.fingo.urlopia.request.RequestRepository;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tomasz Urbas
 */

@Service
@Transactional
public class AcceptanceService {

    @Autowired
    private AcceptanceRepository acceptanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AcceptanceFactory acceptanceFactory;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private RequestService requestService;

    @Autowired
    private RequestRepository requestRepository;


    public AcceptanceDTO getAcceptance(long acceptanceId) {
        Acceptance acceptance = acceptanceRepository.findOne(acceptanceId);

        return acceptanceFactory.create(acceptance);
    }

    public List<AcceptanceDTO> getAcceptancesFromRequest(long requestId) {
        List<Acceptance> acceptances = acceptanceRepository.findByRequestId(requestId);

        List<AcceptanceDTO> requestAcceptances = new LinkedList<>();
        requestAcceptances.addAll(acceptances.stream()
                .map(acceptanceFactory::create)
                .collect(Collectors.toList()));

        return requestAcceptances;
    }

    public List<AcceptanceDTO> getAcceptancesFromLeader(long leaderId) {
        List<Acceptance> acceptances = acceptanceRepository.findByLeaderId(leaderId);

        List<AcceptanceDTO> leaderAcceptances = new LinkedList<>();
        leaderAcceptances.addAll(acceptances.stream()
                .map(acceptanceFactory::create)
                .collect(Collectors.toList()));

        return leaderAcceptances;
    }

    public Page<AcceptanceDTO> getAcceptancesFromLeader(long leaderId, Pageable pageable) {
        Page<Acceptance> acceptancePage = acceptanceRepository.findByLeaderId(leaderId, pageable);
        List<Acceptance> acceptances = acceptancePage.getContent();
        List<AcceptanceDTO> acceptancesDTO = acceptances.stream()
                .map(acceptanceFactory::create)
                .collect(Collectors.toList());
        return new PageImpl<>(acceptancesDTO, pageable, acceptancePage.getTotalElements());
    }

    public boolean accept(long id, long deciderId) {
        Acceptance acceptance = acceptanceRepository.findOne(id);
        User decider = userRepository.findOne(deciderId);
        boolean success = false;

        if (acceptance.getDecider() == null
                && (acceptance.getLeader().getId() == decider.getId() || decider.isAdmin())) {
            acceptance.setAccepted(true);
            acceptance.setDecider(decider);
            acceptance.getRequest().setModified(LocalDateTime.now());

            eventPublisher.publishEvent(new DecisionResultEvent(this, acceptanceFactory.create(acceptance)));
            requestService.checkForActions(acceptance.getRequest());
            success = true;
        }

        return success;
    }

    public boolean reject(long id, long deciderId) {
        Acceptance acceptance = acceptanceRepository.findOne(id);
        User decider = userRepository.findOne(deciderId);
        boolean success = false;

        if ((acceptance.getDecider() == null    // rejecting is accepted only once
                && (acceptance.getLeader().getId() == decider.getId() || decider.isAdmin())) // and only by leader or admin
                || acceptance.getRequest().getRequester().getId() == decider.getId()) {   // canceling is always possible
            acceptance.setAccepted(false);
            acceptance.setDecider(decider);
            acceptance.getRequest().setModified(LocalDateTime.now());
            acceptance.getRequest().setStatus(Request.Status.REJECTED);

            success = true;
        }

        // send mail to requester only if it is not canceling
        if (!(acceptance.getRequest().getRequester().getId() == decider.getId())) {
            eventPublisher.publishEvent(new DecisionResultEvent(this, acceptanceFactory.create(acceptance)));
        }

        return success;
    }

    public void insert(Request request, User leader) {
        Acceptance acceptance = acceptanceRepository.save(new Acceptance(request, leader));
        eventPublisher.publishEvent(new NewRequestNotificationEvent(this, acceptanceFactory.create(acceptance)));
    }

    /*
     *  Function created only for make canceling possible in occasional requests
     */
    public AcceptanceDTO insertCancelAcceptance(RequestDTO requestDTO) {
        Request request = requestRepository.findOne(requestDTO.getId());
        Acceptance acceptance = acceptanceRepository.save(new Acceptance(request, request.getRequester()));
        return acceptanceFactory.create(acceptance);
    }
}
