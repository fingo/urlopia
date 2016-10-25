package info.fingo.urlopia.request;

import info.fingo.urlopia.ad.LocalTeam;
import info.fingo.urlopia.events.OccasionalInfoEvent;
import info.fingo.urlopia.events.OccasionalResponseEvent;
import info.fingo.urlopia.events.RequestAcceptedEvent;
import info.fingo.urlopia.history.DurationCalculator;
import info.fingo.urlopia.history.HistoryService;
import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tomasz Urbas
 */
@Service
@Transactional
public class RequestService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestFactory requestFactory;

    @Autowired
    private AcceptanceService acceptanceService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private AcceptanceRepository acceptanceRepository;

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private UserFactory userFactory;

    public RequestDTO getRequest(long id) {
        Request request = requestRepository.findOne(id);
        return requestFactory.create(request);
    }

    public List<RequestDTO> getRequestsFromWorker(long userId) {
        List<Request> requests = requestRepository.findByRequesterId(userId);

        return requests.stream()
                .map(requestFactory::create)
                .collect(Collectors.toList());
    }

    public List<RequestDTO> getRequestsFromAdmin() {
        List<Request> requests = requestRepository.findAll();

        return requests.stream()
                .map(requestFactory::create)
                .collect(Collectors.toList());
    }

    public List<RequestDTO> getRequestsFromWorker(long userId, LocalDateTime lastUpdate) {
        int newsCount = requestRepository.countByRequesterIdAndModifiedAfter(userId, lastUpdate);

        List<RequestDTO> requests = new LinkedList<>();

        // if there is any new request
        if (newsCount > 0) {
            requests = getRequestsFromWorker(userId);
        }

        return requests;
    }

    public List<RequestDTO> getRequestsFromAdmin(LocalDateTime lastUpdate) {
        int newsCount = requestRepository.countByModifiedAfter(lastUpdate);

        List<RequestDTO> requests = new LinkedList<>();

        // if there is any new request
        if (newsCount > 0) {
            requests = getRequestsFromAdmin();
        }

        return requests;
    }

    public boolean insert(long requesterId, LocalDate startDate, LocalDate endDate, String mailContent, int type) {
        UserDTO requester = userService.getUser(requesterId);
        LocalDate previousMonthDate = LocalDate.now().minus(Period.ofMonths(1));

        Request request = new Request(userRepository.findFirstByMail(requester.getPrincipalName()), startDate, endDate, mailContent);
        RequestDTO requestDTO = requestFactory.create(request);
        float remainingPool = historyService.getHolidaysPool(requesterId, null);
        float currentPool = DurationCalculator.calculate(requestDTO, holidayService);

        if (remainingPool < currentPool && type == RequestDTO.USUAL) {
            return false;
        }

        if (startDate.isAfter(previousMonthDate) && !endDate.isBefore(startDate)) {
            requestRepository.save(request);

            for (LocalTeam team : requester.getTeams()) {
                User leader = userRepository.findFirstByMail(team.getLeader().getPrincipalName());
                acceptanceService.insert(request, leader);
            }

            if (type != RequestDTO.USUAL)
                acceptOccasional(request.getId(), requesterId, -currentPool, type);
        }

        return true;
    }

    public boolean acceptOccasional(long requestId, long deciderId, float hours, int type) {
        List<AcceptanceDTO> acceptances = acceptanceService.getAcceptancesFromRequest(requestId);
        UserDTO userDTO = userFactory.create(requestRepository.findOne(requestId).getRequester());
        RequestDTO requestDTO = getRequest(requestId);
        LocalDate startDate = requestDTO.getStartDate();
        LocalDate endDate = requestDTO.getEndDate();
        boolean success = true;

        eventPublisher.publishEvent(new OccasionalInfoEvent(this, userDTO, type, hours, startDate, endDate));
        eventPublisher.publishEvent(new OccasionalResponseEvent(this, userDTO, type, hours));

        for (AcceptanceDTO acceptance : acceptances) {
            boolean acceptationSuccess = acceptanceService.acceptOccasional(acceptance.getId(), deciderId, hours, type);
            if (!acceptationSuccess) {
                success = false;
            }
        }

        return success;

    }

    public boolean accept(long id, long deciderId) {
        List<AcceptanceDTO> acceptances = acceptanceService.getAcceptancesFromRequest(id);
        boolean success = true;

        for (AcceptanceDTO acceptance : acceptances) {
            if (!acceptanceService.accept(acceptance.getId(), deciderId)) {
                success = false;
            }
        }

        return success;
    }

    public boolean reject(long id, long deciderId) {
        List<AcceptanceDTO> acceptances = acceptanceService.getAcceptancesFromRequest(id);
        boolean success = true;

        for (AcceptanceDTO acceptance : acceptances) {
            if (!acceptanceService.reject(acceptance.getId(), deciderId)) {
                success = false;
            }
        }

        return success;
    }

    public boolean cancel(long id) {
        List<AcceptanceDTO> acceptances = acceptanceService.getAcceptancesFromRequest(id);
        boolean success = true;
        boolean cancelingAfterAccepting = true;

        // canceling all acceptances
        for (AcceptanceDTO acceptance : acceptances) {
            if (acceptance.getDecider() == null) {
                cancelingAfterAccepting = false;
            }

            if (!acceptanceService.reject(acceptance.getId(), acceptance.getRequest().getRequester().getId())) {
                success = false;
            }
        }

        // reverting days pool
        if (success && cancelingAfterAccepting) {
            historyService.insertReversed(getRequest(id));
        }

        return success;
    }

    boolean isValidRequest(long acceptanceId) {

        Acceptance acceptance = acceptanceRepository.findOne(acceptanceId);

        User requester = acceptance.getRequest().getRequester();
        RequestDTO requestDTO = requestFactory.create(acceptance.getRequest());
        float userHolidaysPool = historyService.getHolidaysPool(requester.getId(), null);
        float requestedPool = DurationCalculator.calculate(requestDTO, holidayService);
        return userHolidaysPool >= requestedPool;
    }

    void checkForActions(Request request) {
        RequestDTO requestDTO = requestFactory.create(request);
        List<AcceptanceDTO> acceptances = acceptanceService.getAcceptancesFromRequest(request.getId());

        int accepted = 0;
        for (AcceptanceDTO acceptance : acceptances) {
            if (acceptance.isAccepted()) {
                accepted++;
            }
        }

        // if request is accepted
        if (accepted == acceptances.size()) {
            eventPublisher.publishEvent(new RequestAcceptedEvent(this, requestDTO));
            historyService.insert(requestDTO);
        }
    }
}
