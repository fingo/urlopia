package info.fingo.urlopia.request.occasional;

import info.fingo.urlopia.request.absence.BaseRequestInput;
import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService;
import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.holidays.WorkingDaysCalculator;
import info.fingo.urlopia.request.*;
import info.fingo.urlopia.request.absence.InvalidDatesOrderException;
import info.fingo.urlopia.request.absence.OperationNotSupportedException;
import info.fingo.urlopia.request.normal.events.NormalRequestCanceled;
import info.fingo.urlopia.request.occasional.events.OccasionalRequestCreated;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("occasionalRequestService")
@Transactional
@Slf4j
@RequiredArgsConstructor
public class OccasionalRequestService implements RequestTypeService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final HistoryLogService historyLogService;
    private final WorkingDaysCalculator workingDaysCalculator;
    private final ApplicationEventPublisher publisher;
    private final PresenceConfirmationService presenceConfirmationService;

    @Override
    public Request create(Long userId, BaseRequestInput requestInput) {
        User user = userRepository.findById(userId).orElseThrow();
        int workingDays = workingDaysCalculator.calculate(requestInput.getStartDate(), requestInput.getEndDate());

        Request request = this.createRequestObject(user, requestInput, workingDays);
        this.validateRequest(request);
        request = requestRepository.save(request);

        var startDate = request.getStartDate();
        var endDate = request.getEndDate();
        presenceConfirmationService.deletePresenceConfirmations(userId, startDate, endDate);

        var term = request.getTerm();
        var typeInfo = request.getTypeInfo().getInfo();
        var message = String.format("%s (%s)", term, typeInfo);
        historyLogService.create(request,0f, message, userId, userId);

        publisher.publishEvent(new OccasionalRequestCreated(request));
        var loggerInfo = "New occasional request with id: %d has been created"
                .formatted(request.getId());
        log.info(loggerInfo);

        return request;
    }

    private Request createRequestObject(User user, BaseRequestInput requestInput, int workingDays) {
        var typeInfo = OccasionalType.WRONG;
        if(requestInput instanceof RequestInput input) {
            typeInfo = input.getOccasionalType();
        }
        return new Request(user,
                requestInput.getStartDate(),
                requestInput.getEndDate(),
                workingDays,
                RequestType.OCCASIONAL,
                typeInfo,
                Request.Status.ACCEPTED);
    }

    private void validateRequest(Request request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw InvalidDatesOrderException.invalidDatesOrder();
        }
    }

    @Override
    public void accept(Request request) {
        throw OperationNotSupportedException.operationNotSupported();
    }

    @Override
    public void reject(Request request) {
        throw OperationNotSupportedException.operationNotSupported();
    }

    @Override
    public void cancel(Request request) {
        request.setStatus(Request.Status.CANCELED);
        request = requestRepository.save(request);
        publisher.publishEvent(new NormalRequestCanceled(request));
        var loggerInfo = "Request with id: %d has been cancelled"
                .formatted(request.getId());
        log.info(loggerInfo);
    }

}
