package info.fingo.urlopia.request;

import info.fingo.urlopia.history.HistoryLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class RequestService {

    private final RequestRepository requestRepository;

    private final HistoryLogService historyLogService;

    @Value("${mails.master-leader}")
    private String masterLeaderMail;

    @Autowired
    public RequestService(RequestRepository requestRepository, HistoryLogService historyLogService) {
        this.requestRepository = requestRepository;
        this.historyLogService = historyLogService;
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
        RequestTypeService service = requestInput.getType().getService();
        service.create(userId, requestInput);
    }

    // *** ACTIONS ***

    public void accept(Long requestId, Long deciderId) {
        Request request = requestRepository.findOne(requestId);
        RequestTypeService service = request.getType().getService();
        service.accept(request);

        Float workingHours = request.getWorkingDays() * request.getRequester().getWorkTime();
        historyLogService.create(request, -workingHours, "", request.getRequester().getId(), deciderId);
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
