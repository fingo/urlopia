package info.fingo.urlopia.request.normal;

import info.fingo.urlopia.acceptance.AcceptanceService;
import info.fingo.urlopia.acceptance.StatusNotSupportedException;
import info.fingo.urlopia.api.v2.user.PendingDaysOutput;
import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.holidays.WorkingDaysCalculator;
import info.fingo.urlopia.request.*;
import info.fingo.urlopia.request.absence.BaseRequestInput;
import info.fingo.urlopia.request.absence.InvalidDatesOrderException;
import info.fingo.urlopia.request.normal.events.NormalRequestAccepted;
import info.fingo.urlopia.request.normal.events.NormalRequestCanceled;
import info.fingo.urlopia.request.normal.events.NormalRequestCreated;
import info.fingo.urlopia.request.normal.events.NormalRequestRejected;
import info.fingo.urlopia.user.NoSuchUserException;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserRepository;
import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.DoubleStream;

@Service("normalRequestService")
@Transactional
@Slf4j
@RequiredArgsConstructor
public class NormalRequestService implements RequestTypeService {

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final HistoryLogService historyLogService;

    private final WorkingDaysCalculator workingDaysCalculator;

    private final ApplicationEventPublisher publisher;

    private final AcceptanceService acceptanceService;

    private final UserService userService;


    @Override
    public Request create(Long userId, BaseRequestInput requestInput) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> {
                    log.error("Could not create new normal request: user with id: {} does not exist", userId);
                    return NoSuchUserException.invalidId();
                });
        int workingDays = workingDaysCalculator.calculate(requestInput.getStartDate(), requestInput.getEndDate());
        float workingHours =  workingDays * user.getWorkTime();

        this.ensureUserOwnRequiredHoursNumber(user, workingHours);
        Request request = this.createRequestObject(user, requestInput, workingDays);
        this.validateRequest(request);

        request = requestRepository.save(request);
        publisher.publishEvent(new NormalRequestCreated(request));
        log.info("New normal request with id: %d has been created".formatted(request.getId()));

        var leader = userService.getAcceptanceLeaderForUser(user);
        if (leader != null) {
            this.acceptanceService.create(request, leader);
        } else {
            this.accept(request);
        }

        return requestRepository
                .findById(request.getId())
                .orElseThrow(NoSuchElementException::new);
    }

    private void ensureUserOwnRequiredHoursNumber(User user, float requiredHours) {
        double hoursRemaining = historyLogService.countRemainingHours(user.getId());
        double pendingRequestsHours = countPendingRequestsHours(user);
        boolean userOwnRequiredHoursNumber =
                (hoursRemaining - pendingRequestsHours) >= requiredHours;
        if (!userOwnRequiredHoursNumber) {
            var userId = user.getId();
            log.error("New normal request could not be created for user with id: {}. Reason: not enough days", userId);
            throw new NotEnoughDaysException();
        }
    }

    private double countPendingRequestsHours(User user) {
        Long requesterId = user.getId();
        return requestRepository.findByRequesterId(requesterId).stream()
                .filter(Request::isPending)
                .filter(Request::isNormal)
                .map(request -> request.getWorkingDays() * request.getRequester().getWorkTime())
                .flatMapToDouble(DoubleStream::of)
                .sum();
    }

    public DayHourTime getPendingRequestsTime(Long userId) {
        var user = getUserById(userId);
        double pendingRequestsHours = countPendingRequestsHours(user);
        float userWorkTime = user.getWorkTime();
        int days = (int) Math.floor(pendingRequestsHours / userWorkTime);
        double hours =  Math.round((pendingRequestsHours % userWorkTime) * 100.0) / 100.0;
        return DayHourTime.of(days, hours);
    }

    public PendingDaysOutput getPendingRequestsTimeV2(Long userId) {
        var user = getUserById(userId);
        double pendingRequestsHours = countPendingRequestsHours(user);
        float userWorkTime = user.getWorkTime();
        int days = (int) Math.floor(pendingRequestsHours / userWorkTime);
        double hours =  Math.round((pendingRequestsHours % userWorkTime) * 100.0) / 100.0;
        if (userWorkTime == 8) {
            return new PendingDaysOutput(days, hours);
        }
        return new PendingDaysOutput(0, pendingRequestsHours);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("Could not get pending requests time for a non-existent user with id: {}", userId);
            return NoSuchUserException.invalidId();
        });
    }

    private Request createRequestObject(User user, BaseRequestInput requestInput, int workingDays) {
        return new Request(user,
                requestInput.getStartDate(),
                requestInput.getEndDate(),
                workingDays,
                RequestType.NORMAL,
                null,
                Request.Status.PENDING);
    }

    private void validateRequest(Request request) {
        var requesterId = request.getRequester().getId();
        if (request.getEndDate().isBefore(request.getStartDate())) {
            log.error("Could not create new normal request for user with id: {} because dates are in invalid order",
                    requesterId);
            throw InvalidDatesOrderException.invalidDatesOrder();
        }
        if (isOverlapping(request)) {
            log.error("Could not create normal request for user with id: {} because it is overlapping other requests",
                    requesterId);
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

    @Override
    public void accept(Request request) {
        this.validateStatus(request.getStatus(), Request.Status.PENDING);
        request = this.changeStatus(request, Request.Status.ACCEPTED);
        publisher.publishEvent(new NormalRequestAccepted(request)); //TODO: Create general Event RequestAccepted instead of Normal/OccasionalRequestAccepted
        var loggerInfo = "Request with id: %d has been accepted"
                .formatted(request.getId());
        log.info(loggerInfo);
    }

    @Override
    public void reject(Request request) {
        this.validateStatus(request.getStatus(), Request.Status.PENDING);
        request = this.changeStatus(request, Request.Status.REJECTED);
        publisher.publishEvent(new NormalRequestRejected(request));
        var loggerInfo = "Request with id: %d has been rejected"
                .formatted(request.getId());
        log.info(loggerInfo);
    }

    @Override
    public void cancel(Request request) {
        Request.Status[] supportedStatuses = {Request.Status.PENDING, Request.Status.ACCEPTED};
        this.validateStatus(request.getStatus(), supportedStatuses);
        request = this.changeStatus(request, Request.Status.CANCELED);
        publisher.publishEvent(new NormalRequestCanceled(request));
        var loggerInfo = "Request with id: %d has been canceled"
                .formatted(request.getId());
        log.info(loggerInfo);
    }

    private void validateStatus(Request.Status status, Request.Status... supportedStatuses) {
        List<Request.Status> supported = Arrays.asList(supportedStatuses);
        if (!supported.contains(status)) {
            log.error("Status: {} does not exist", status.toString());
            throw StatusNotSupportedException.invalidStatus(status.toString());
        }
    }

    private Request changeStatus(Request request, Request.Status status) {
        request.getAcceptances().forEach(acceptance -> acceptanceService.expire(acceptance.getId()));
        request.setStatus(status);
        return requestRepository.save(request);
    }

}
