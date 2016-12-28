package info.fingo.urlopia.request;

import info.fingo.urlopia.events.DecisionResultEvent;
import info.fingo.urlopia.events.NewRequestNotificationEvent;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
    private AcceptanceFactory factory;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private RequestService requestService;

    @Autowired
    private AcceptanceFactory acceptanceFactory;

    @Autowired
    private RequestRepository requestRepository;


    public AcceptanceDTO getAcceptance(long acceptanceId) {
        Acceptance acceptance = acceptanceRepository.findOne(acceptanceId);

        return factory.create(acceptance);
    }

    public List<AcceptanceDTO> getAcceptancesFromRequest(long requestId) {
        List<Acceptance> acceptances = acceptanceRepository.findByRequestId(requestId);

        List<AcceptanceDTO> requestAcceptances = new LinkedList<>();
        requestAcceptances.addAll(acceptances.stream()
                .map(factory::create)
                .collect(Collectors.toList()));

        return requestAcceptances;
    }

    public List<AcceptanceDTO> getAcceptancesFromLeader(long leaderId) {
        List<Acceptance> acceptances = acceptanceRepository.findByLeaderId(leaderId);

        List<AcceptanceDTO> leaderAcceptances = new LinkedList<>();
        leaderAcceptances.addAll(acceptances.stream()
                .map(factory::create)
                .collect(Collectors.toList()));

        return leaderAcceptances;
    }

    public List<AcceptanceDTO> getAcceptancesFromLeader(long leaderId, LocalDateTime lastUpdate) {
        int newsCount = acceptanceRepository.countByLeaderIdAndRequestModifiedAfter(leaderId, lastUpdate);

        List<AcceptanceDTO> acceptances = new LinkedList<>();

        // if there is any new request
        if (newsCount > 0) {
            acceptances = getAcceptancesFromLeader(leaderId);
        }

        return acceptances;
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

            eventPublisher.publishEvent(new DecisionResultEvent(this, acceptance.getId()));
            requestService.checkForActions(acceptance.getRequest());
            success = true;
        }

        return success;
    }

    public boolean reject(long id, long deciderId) {
        Acceptance acceptance = acceptanceRepository.findOne(id);
        User decider = userRepository.findOne(deciderId);
        boolean success = false;

        if ((acceptance.getDecider() == null    // rejecting/rejecting is accepted only once
                && (acceptance.getLeader().getId() == decider.getId() || decider.isAdmin())) // and only by leader or admin
                || acceptance.getRequest().getRequester().getId() == decider.getId()) {   // canceling is always possible
            acceptance.setAccepted(false);
            acceptance.setDecider(decider);
            acceptance.getRequest().setModified(LocalDateTime.now());

            success = true;
        }

        // send mail to requester only if it isn't canceling
        if (!(acceptance.getRequest().getRequester().getId() == decider.getId())) {
            eventPublisher.publishEvent(new DecisionResultEvent(this, acceptance.getId()));
        }

        return success;
    }

    public void insert(Request request, User leader) {
        Acceptance acceptance = acceptanceRepository.save(new Acceptance(request, leader));
        eventPublisher.publishEvent(new NewRequestNotificationEvent(this, acceptance.getId()));
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
