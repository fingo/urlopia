package info.fingo.urlopia.request.occasional;

import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.holidays.WorkingDaysCalculator;
import info.fingo.urlopia.request.*;
import info.fingo.urlopia.request.normal.events.NormalRequestCanceled;
import info.fingo.urlopia.request.occasional.events.OccasionalRequestCreated;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service("occasionalRequestService")
@Transactional
public class OccasionalRequestService implements RequestTypeService {

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final HistoryLogService historyLogService;

    private final WorkingDaysCalculator workingDaysCalculator;

    private final ApplicationEventPublisher publisher;

    @Autowired
    public OccasionalRequestService(RequestRepository requestRepository, UserRepository userRepository,
                                HistoryLogService historyLogService, WorkingDaysCalculator workingDaysCalculator, ApplicationEventPublisher publisher) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.historyLogService = historyLogService;
        this.workingDaysCalculator = workingDaysCalculator;
        this.publisher = publisher;
    }

    @Override
    public void create(Long userId, RequestInput requestInput) {
        User user = userRepository.findById(userId).orElseThrow();
        int workingDays = workingDaysCalculator.calculate(requestInput.getStartDate(), requestInput.getEndDate());

        Request request = this.createRequestObject(user, requestInput, workingDays);
        this.validateRequest(request);
        requestRepository.save(request);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String message = String.format("%s - %s (%s)",
                request.getStartDate().format(formatter),
                request.getEndDate().format(formatter),
                request.getTypeInfo().getInfo());
        historyLogService.create(request,0f, message, userId, userId);

        publisher.publishEvent(new OccasionalRequestCreated(request));
    }

    private Request createRequestObject(User user, RequestInput requestInput, int workingDays) {
        return new Request(user,
                requestInput.getStartDate(),
                requestInput.getEndDate(),
                workingDays,
                RequestType.OCCASIONAL,
                requestInput.getOccasionalType(),
                Request.Status.ACCEPTED);
    }

    private void validateRequest(Request request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("End date is before start date");
        }
    }

    @Override
    public void accept(Request request) {
        throw new RuntimeException("Method not supported");
    }

    @Override
    public void reject(Request request) {
        throw new RuntimeException("Method not supported");
    }

    @Override
    public void cancel(Request request) {
        request.setStatus(Request.Status.CANCELED);
        request = requestRepository.save(request);
        publisher.publishEvent(new NormalRequestCanceled(request));
    }

}
