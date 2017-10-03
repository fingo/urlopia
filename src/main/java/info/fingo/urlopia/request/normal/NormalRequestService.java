package info.fingo.urlopia.request.normal;

import info.fingo.urlopia.acceptance.AcceptanceService;
import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.holidays.WorkingDaysCalculator;
import info.fingo.urlopia.request.*;
import info.fingo.urlopia.request.normal.events.NormalRequestAccepted;
import info.fingo.urlopia.request.normal.events.NormalRequestCanceled;
import info.fingo.urlopia.request.normal.events.NormalRequestCreated;
import info.fingo.urlopia.request.normal.events.NormalRequestRejected;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service("normalRequestService")
@Transactional
public class NormalRequestService implements RequestTypeService {

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final HistoryLogService historyLogService;

    private final WorkingDaysCalculator workingDaysCalculator;

    private final ApplicationEventPublisher publisher;

    private final AcceptanceService acceptanceService;

    @Value("${mails.master-leader}")
    private String masterLeaderMail;

    @Autowired
    public NormalRequestService(RequestRepository requestRepository, UserRepository userRepository,
                                HistoryLogService historyLogService, WorkingDaysCalculator workingDaysCalculator, ApplicationEventPublisher publisher, AcceptanceService acceptanceService) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.historyLogService = historyLogService;
        this.workingDaysCalculator = workingDaysCalculator;
        this.publisher = publisher;
        this.acceptanceService = acceptanceService;
    }

    @Override
    public void create(Long userId, RequestInput requestInput) {
        User user = userRepository.findOne(userId);
        int workingDays = workingDaysCalculator.calculate(requestInput.getStartDate(), requestInput.getEndDate());
        float workingHours =  workingDays * user.getWorkTime();

        this.validateUserPreconditions(user, workingHours);
        Request request = this.createRequestObject(user, requestInput, workingDays);
        this.validateRequest(request);

        request = requestRepository.save(request);
        this.createAcceptances(user, request);

        publisher.publishEvent(new NormalRequestCreated(request));
    }

    private void validateUserPreconditions(User user, float hoursNeeded) {
        float hoursRemaining = historyLogService.countRemainingHours(user.getId());
        if (hoursRemaining < hoursNeeded) {
            throw new NotEnoughDaysException();
        }
    }

    private Request createRequestObject(User user, RequestInput requestInput, int workingDays) {
        return new Request(user,
                requestInput.getStartDate(),
                requestInput.getEndDate(),
                workingDays,
                RequestType.NORMAL,
                null,
                Request.Status.PENDING);
    }

    private void validateRequest(Request request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("End date is before start date");
        }
        if (isOverlapping(request)) {
            throw new RequestOverlappingException();
        }
    }

    private boolean isOverlapping(Request newRequest) {
        Long userId = newRequest.getRequester().getId();
        List<Request> requests = requestRepository.findByRequesterId(userId);

        return requests.stream()
                .filter(Request::isAffecting)
                .anyMatch(request -> request.isOverlapping(newRequest));
    }

    private void createAcceptances(User user, Request request) {
        user.getTeams().stream()
                .map(team -> {
                    Long teamLeaderId = team.getLeader().getId();
                    return (teamLeaderId.equals(user.getId())) ?
                            userRepository.findFirstByMail(masterLeaderMail) : team.getLeader();
                })
                .distinct()
                .forEach(leader -> acceptanceService.create(request, leader));
    }

    @Override
    public void accept(Request request) {
        this.validateStatus(request.getStatus(), Request.Status.PENDING);
        request = this.changeStatus(request, Request.Status.ACCEPTED);
        publisher.publishEvent(new NormalRequestAccepted(request));
    }

    @Override
    public void reject(Request request) {
        this.validateStatus(request.getStatus(), Request.Status.PENDING);
        request = this.changeStatus(request, Request.Status.REJECTED);
        publisher.publishEvent(new NormalRequestRejected(request));
    }

    @Override
    public void cancel(Request request) {
        Request.Status[] supportedStatuses = {Request.Status.PENDING, Request.Status.ACCEPTED};
        this.validateStatus(request.getStatus(), supportedStatuses);
        request = this.changeStatus(request, Request.Status.CANCELED);
        publisher.publishEvent(new NormalRequestCanceled(request));
    }

    private void validateStatus(Request.Status status, Request.Status... supportedStatuses) {
        List<Request.Status> supported = Arrays.asList(supportedStatuses);
        if (!supported.contains(status)) {
            throw new RuntimeException("Status not supported");
        }
    }

    private Request changeStatus(Request request, Request.Status status) {
        request.getAcceptances().forEach(acceptance -> acceptanceService.expire(acceptance.getId()));
        request.setStatus(status);
        return requestRepository.save(request);
    }

}
