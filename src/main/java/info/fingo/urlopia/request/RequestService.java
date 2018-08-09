package info.fingo.urlopia.request;

import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.config.persistance.filter.Operator;
import info.fingo.urlopia.history.HistoryLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class RequestService {

    private final RequestRepository requestRepository;

    private final HistoryLogService historyLogService;

    @Autowired
    public RequestService(RequestRepository requestRepository, HistoryLogService historyLogService) {
        this.requestRepository = requestRepository;
        this.historyLogService = historyLogService;
    }

    public Page<RequestExcerptProjection> getFromUser(Long userId, Filter filter, Pageable pageable) {
        Filter filterWithRestrictions = filter.toBuilder()
                .and("requester.id", Operator.EQUAL, userId.toString())
                .build();
        return this.get(filterWithRestrictions, pageable);
    }

    public Page<RequestExcerptProjection> get(Filter filter, Pageable pageable) {
        return requestRepository.findAll(filter, pageable, RequestExcerptProjection.class);
    }

    public List<Request> get(Long userId, Integer year) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        return requestRepository.findByRequesterIdAndYear(userId, year);
    }

    public void create(Long userId, RequestInput requestInput) {
        RequestTypeService service = requestInput.getType().getService();
        service.create(userId, requestInput);
    }

    // *** ACTIONS ***

    public void accept(Long requestId, Long deciderId) {
        Request request = requestRepository.findOne(requestId);
        RequestTypeService service = request.getType().getService();
        service.accept(request);

        Float workingHours = request.getWorkingDays() * request.getRequester().getWorkTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String term = String.format("%s - %s",
                request.getStartDate().format(formatter), request.getEndDate().format(formatter));
        historyLogService.create(request, -workingHours, term, request.getRequester().getId(), deciderId);
    }

    public void reject(Long requestId) {
        Request request = requestRepository.findOne(requestId);
        RequestTypeService service = request.getType().getService();
        service.reject(request);
    }

    public void cancel(Long requestId, Long deciderId) {
        Request request = requestRepository.findOne(requestId);
        RequestTypeService service = request.getType().getService();
        Request.Status previousStatus = request.getStatus();
        service.cancel(request);

        if (previousStatus == Request.Status.ACCEPTED) {
            historyLogService.createReverse(request, "Anulowanie", deciderId);
        }
    }

}
