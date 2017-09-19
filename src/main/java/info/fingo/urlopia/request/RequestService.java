package info.fingo.urlopia.request;

import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.holidays.WorkingDaysCalculator;
import info.fingo.urlopia.request.acceptance.Acceptance;
import info.fingo.urlopia.request.acceptance.AcceptanceRepository;
import info.fingo.urlopia.request.acceptance.AcceptanceService;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class RequestService {

    private final RequestRepository requestRepository;

    private final AcceptanceRepository acceptanceRepository;

    private final UserRepository userRepository;

    @Autowired
    private AcceptanceService acceptanceService;

    private final HistoryLogService historyLogService;

    private final WorkingDaysCalculator workingDaysCalculator;

    @Value("${mails.master-leader}")
    private String masterLeaderMail;

    public RequestService(RequestRepository requestRepository, AcceptanceRepository acceptanceRepository,
                          UserRepository userRepository, HistoryLogService historyLogService,
                          WorkingDaysCalculator workingDaysCalculator) {
        this.requestRepository = requestRepository;
        this.acceptanceRepository = acceptanceRepository;
        this.userRepository = userRepository;
        this.historyLogService = historyLogService;
        this.workingDaysCalculator = workingDaysCalculator;
    }

    public Page<RequestExcerptProjection> get(Pageable pageable) {
        return requestRepository.findBy(pageable);
    }

    public Page<RequestExcerptProjection> get(Long userId, Pageable pageable) {
        return requestRepository.findByRequesterId(userId, pageable);
    }

    public List<Request> get(Long userId, Integer year) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        return requestRepository.findByRequesterIdAndYear(userId, year);
    }

    public void create(Long userId, RequestInput requestInput) {
        User user = userRepository.findOne(userId);
        if (requestInput.getEndDate().isBefore(requestInput.getStartDate())) {
            throw new RuntimeException("End date is before start date");
        }
        Request request = (requestInput.getType() == Request.Type.NORMAL)
                ? this.createNormal(user, requestInput) : this.createOccasional(user, requestInput);
        if (isOverlapping(request)) {
            throw new RequestOverlappingException();
        }
        requestRepository.save(request);
        if (request.getType() == Request.Type.OCCASIONAL) {
            historyLogService.create(request,0f, request.getTypeInfo().getInfo(), userId, userId);
        } else if (request.getType() == Request.Type.NORMAL) {
            Set<User> leaders = user.getTeams().stream()
                    .map(team -> {
                        Long teamLeaderId = team.getLeader().getId();
                        return (teamLeaderId.equals(userId)) ?
                                userRepository.findFirstByMail(masterLeaderMail) : team.getLeader();
                    }).collect(Collectors.toSet());
            leaders.forEach(leader -> acceptanceService.create(request, leader));
        }
    }

    private Request createNormal(User user, RequestInput requestInput) { // thing about factory
        float hoursRemaining = historyLogService.countRemainingHours(user.getId());
        float hoursNeeded = workingDaysCalculator.calculate(requestInput.getStartDate(), requestInput.getEndDate()) * user.getWorkTime();
        if (hoursRemaining < hoursNeeded) {
            throw new NotEnoughDaysException();
        }
        Float workingDays = hoursNeeded / user.getWorkTime();
        return new Request(user, requestInput.getStartDate(), requestInput.getEndDate(), workingDays.intValue(),
                Request.Type.NORMAL, null, Request.Status.PENDING);
    }

    private Request createOccasional(User user, RequestInput requestInput) { // thing about factory
        float hoursNeeded = workingDaysCalculator.calculate(requestInput.getStartDate(), requestInput.getEndDate()) * user.getWorkTime();
        Float workingDays = hoursNeeded / user.getWorkTime();
        return new Request(user, requestInput.getStartDate(), requestInput.getEndDate(), workingDays.intValue(),
                Request.Type.OCCASIONAL, requestInput.getOccasionalType(), Request.Status.ACCEPTED);
    }

    private boolean isOverlapping(Request newRequest) {
        Long userId = newRequest.getRequester().getId();
        List<Request> requests = requestRepository.findByRequesterId(userId);

        return requests.stream()
                .filter(request -> request.getStatus() == Request.Status.ACCEPTED
                        || request.getStatus() == Request.Status.PENDING)
                .filter(request -> !(request.getEndDate().isBefore(newRequest.getStartDate()))
                        && !(newRequest.getEndDate().isBefore(request.getStartDate())))
                .count() > 0;
    }

    // *** ACTIONS ***

    public void accept(Long requestId, Long deciderId) {
        Request request = requestRepository.findOne(requestId);
        Request.Status[] supportedStatuses = {Request.Status.PENDING};
        this.validateStatus(request.getStatus(), supportedStatuses);
        request = this.changeStatus(request, Request.Status.ACCEPTED);
        Float hours = request.getWorkingDays() * request.getRequester().getWorkTime();
        historyLogService.create(request, -hours, "", request.getRequester().getId(), deciderId);
    }

    public void reject(Long requestId) {
        Request request = requestRepository.findOne(requestId);
        Request.Status[] supportedStatuses = {Request.Status.PENDING};
        this.validateStatus(request.getStatus(), supportedStatuses);
        this.changeStatus(request, Request.Status.REJECTED);
    }

    public void cancel(Long requestId, Long deciderId) {
        Request request = requestRepository.findOne(requestId);
        Request.Status[] supportedStatuses = {Request.Status.PENDING, Request.Status.ACCEPTED};
        this.validateStatus(request.getStatus(), supportedStatuses);
        if (request.getStatus() == Request.Status.ACCEPTED) {
            historyLogService.createReverse(request, "Anulowanie", deciderId);
        }
        this.changeStatus(request, Request.Status.CANCELED);
    }

    private void validateStatus(Request.Status status, Request.Status[] supportedStatuses) {
        List<Request.Status> supported = Arrays.asList(supportedStatuses);
        if (!supported.contains(status)) {
            throw new RuntimeException("Status unsupported");
        }
    }

    private Request changeStatus(Request request, Request.Status status) {
        this.expireAcceptances(request.getAcceptances());
        request.setStatus(status);
        return requestRepository.save(request);
    }

    private void expireAcceptances(Collection<Acceptance> acceptances) {
        for (Acceptance acceptance : acceptances) {
            if (acceptance.getStatus() == Acceptance.Status.PENDING) {
                acceptance.setStatus(Acceptance.Status.EXPIRED);
                acceptanceRepository.save(acceptance);
            }
        }
    }

}
