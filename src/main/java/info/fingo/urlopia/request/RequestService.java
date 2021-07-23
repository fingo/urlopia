package info.fingo.urlopia.request;

import info.fingo.urlopia.UrlopiaApplication;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.config.persistance.filter.Operator;
import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RequestService {

    private final RequestRepository requestRepository;

    private final HistoryLogService historyLogService;

    private final UserService userService;

    @Autowired
    public RequestService(RequestRepository requestRepository, HistoryLogService historyLogService, UserService userService) {
        this.requestRepository = requestRepository;
        this.historyLogService = historyLogService;
        this.userService = userService;
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

    public List<Request> getByUserAndDate(Long userId, LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(UrlopiaApplication.DATE_FORMAT);
        String formattedDate = formatter.format(date);
        Filter filter = Filter.newBuilder()
                .and("requester.id", Operator.EQUAL, String.valueOf(userId))
                .and("startDate", Operator.LESS_OR_EQUAL, formattedDate)
                .and("endDate", Operator.GREATER_OR_EQUAL, formattedDate)
                .build();
        return this.requestRepository.findAll(filter);
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

    public List<VacationDay> getTeammatesVacationsForNexTwoWeeks(Long userId) {
        User user = this.userService.get(userId);
        Set<User> teammates = user.getTeams().stream()
                .flatMap(team -> team.getUsers().stream())
                .collect(Collectors.toSet());

        List<VacationDay> teammatesVocations = new ArrayList<>(14);
        LocalDate currentDate = LocalDate.now();
        LocalDate lastDate = currentDate.plusWeeks(2);
        while (currentDate.isBefore(lastDate)) {
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                List<String> vocationTeammates = new ArrayList<>();
                for (User teammate : teammates) {
                    if (!this.isVacationing(teammate, currentDate)) continue;
                    vocationTeammates.add(String.format("%s %s", teammate.getFirstName(), teammate.getLastName()));
                }
                teammatesVocations.add(new VacationDay(currentDate, vocationTeammates));
            }
            currentDate = currentDate.plusDays(1);
        }
        return teammatesVocations;
    }

    private boolean isVacationing(User user, LocalDate date) {
        return this.getByUserAndDate(user.getId(), date).stream()
                .anyMatch(request -> request.getStatus() == Request.Status.ACCEPTED);
    }

    // *** ACTIONS ***

    public void accept(Long requestId, Long deciderId) {
        Request request = requestRepository.findById(requestId).orElseThrow();
        RequestTypeService service = request.getType().getService();
        service.accept(request);

        Float workingHours = request.getWorkingDays() * request.getRequester().getWorkTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String term = String.format("%s - %s",
                request.getStartDate().format(formatter), request.getEndDate().format(formatter));
        historyLogService.create(request, -workingHours, term, request.getRequester().getId(), deciderId);
    }

    public void reject(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow();
        RequestTypeService service = request.getType().getService();
        service.reject(request);
    }

    public void cancel(Long requestId, Long deciderId) {
        Request request = requestRepository.findById(requestId).orElseThrow();
        RequestTypeService service = request.getType().getService();
        Request.Status previousStatus = request.getStatus();
        service.cancel(request);

        if (previousStatus == Request.Status.ACCEPTED) {
            historyLogService.createReverse(request, "Anulowanie", deciderId);
        }
    }

}
