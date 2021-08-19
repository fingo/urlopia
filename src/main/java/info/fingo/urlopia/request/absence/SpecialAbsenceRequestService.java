package info.fingo.urlopia.request.absence;

import info.fingo.urlopia.api.v2.exceptions.UnauthorizedException;
import info.fingo.urlopia.config.authentication.WebTokenService;
import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.holidays.WorkingDaysCalculator;
import info.fingo.urlopia.request.*;
import info.fingo.urlopia.request.absence.events.SpecialAbsenceRequestCanceled;
import info.fingo.urlopia.request.absence.events.SpecialAbsenceRequestCreated;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("specialRequestService")
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SpecialAbsenceRequestService implements RequestTypeService {

    private final UserService userService;
    private final WorkingDaysCalculator workingDaysCalculator;
    private final HistoryLogService historyLogService;
    private final RequestRepository requestRepository;
    private final ApplicationEventPublisher publisher;
    private final WebTokenService webTokenService;

    @Override
    public Request create(Long userId,
                          BaseRequestInput input) {
        var user = userService.get(userId);
        if (!ensureAdmin()) throw UnauthorizedException.unauthorized();
        var adminId = (Long) webTokenService.getUserId();
        var workingDays = workingDaysCalculator.calculate(input.getStartDate(), input.getEndDate());
        var request = mapToRequest(user, input, workingDays);
        validateRequest(request);
        requestRepository.save(request);
        log.info("New request with id: {} has been saved", request.getId());
        publisher.publishEvent(new SpecialAbsenceRequestCreated(request));
        var message = request.getRequestDescription();
        historyLogService.create(request, 0f, message, userId, adminId);

        return request;
    }

    @Override
    public void cancel(Request request) {
        if (!ensureAdmin()) throw UnauthorizedException.unauthorized();
        request.setStatus(Request.Status.CANCELED);
        requestRepository.save(request);
        publisher.publishEvent(new SpecialAbsenceRequestCanceled(request));
        log.info("Request with id: {} has been cancelled", request.getId());
    }

    @Override
    public void accept(Request request) {
        throw OperationNotSupportedException.operationNotSupported();
    }

    @Override
    public void reject(Request request) {
        throw OperationNotSupportedException.operationNotSupported();
    }

    private boolean ensureAdmin() {
        return webTokenService.isCurrentUserAnAdmin();
    }

    public Request mapToRequest(User user,
                                BaseRequestInput input,
                                int workingDays){
        var typeInfo = SpecialAbsenceReason.WRONG.toString();
        if (input instanceof SpecialAbsenceRequestInput specialInput) {
          typeInfo = specialInput.getReason().toString();
        }
        return new Request(
                user,
                input.getStartDate(),
                input.getEndDate(),
                workingDays,
                typeInfo
        );
    }

    private void validateRequest(Request request) {
        if (isEndBeforeStart(request)) {
            throw InvalidDatesOrderException.invalidDatesOrder();
        }
        if (isOverlapping(request)) {
            throw new RequestOverlappingException();
        }
    }

    private boolean isEndBeforeStart(Request request) {
        return request.getEndDate().isBefore(
                request.getStartDate()
        );
    }

    private boolean isOverlapping(Request newRequest) {
        var userId = newRequest.getRequester().getId();
        var requests = requestRepository.findByRequesterId(userId);

        return requests.stream()
                .filter(Request::isAffecting)
                .anyMatch(request -> request.isOverlapping(newRequest));
    }
}