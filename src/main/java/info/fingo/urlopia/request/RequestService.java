package info.fingo.urlopia.request;

import info.fingo.urlopia.UrlopiaApplication;
import info.fingo.urlopia.api.v2.exceptions.UnauthorizedException;
import info.fingo.urlopia.config.authentication.WebTokenService;
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

    private final WebTokenService webTokenService;

    @Autowired
    public RequestService(RequestRepository requestRepository,
                          HistoryLogService historyLogService,
                          UserService userService,
                          WebTokenService webTokenService) {
        this.requestRepository = requestRepository;
        this.historyLogService = historyLogService;
        this.userService = userService;
        this.webTokenService = webTokenService;
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

    public Request getById(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow();
    }

    public Request create(Long userId, RequestInput requestInput) {
        RequestTypeService service = requestInput.getType().getService();
        return service.create(userId, requestInput);
    }

    public List<VacationDay> getTeammatesVacationsForNextTwoWeeks(Long userId) {
        var startDate = LocalDate.now();
        var endDate = startDate.plusWeeks(2);
        return getTeammatesVacations(userId, startDate, endDate);
    }

    public List<VacationDay> getTeammatesVacations(Long userId, 
                                                   LocalDate startDate, 
                                                   LocalDate endDate) {
        var user = userService.get(userId);
        var teammates = teammatesOf(user);
        return startDate.datesUntil(endDate)
                .filter(date -> !isWeekend(date))
                .map(date -> createVacationDay(teammates, date))
                .collect(Collectors.toList());
    }

    private VacationDay createVacationDay(List<User> users, 
                                          LocalDate date) {
        var namesOfVacationingUsers = users.stream()
                .filter(user -> isVacationing(user, date))
                .map(user -> user.getFirstName() + " " + user.getLastName())
                .collect(Collectors.toList());
        return new VacationDay(date, namesOfVacationingUsers);
    }

    private List<User> teammatesOf(User user) {
        return user.getTeams().stream()
                .flatMap(team -> team.getUsers().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean isWeekend(LocalDate date) {
        var dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    private boolean isVacationing(User user, 
                                  LocalDate date) {
        return this.getByUserAndDate(user.getId(), date).stream()
                .anyMatch(request -> request.getStatus() == Request.Status.ACCEPTED);
    }

    // *** ACTIONS ***

    public void validateAdminPermissionAndAccept(Long requestId,
                                                 Long deciderId) {
        webTokenService.ensureAdmin();

        accept(requestId, deciderId);
    }

    public void accept(Long requestId,
                       Long deciderId) {
        var request = requestRepository.findById(requestId).orElseThrow();
        var service = request.getType().getService();
        service.accept(request);

        float workingHours = request.getWorkingDays() * request.getRequester().getWorkTime();
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var term = String.format("%s - %s",
                request.getStartDate().format(formatter),
                request.getEndDate().format(formatter));
        historyLogService.create(request, -workingHours, term, request.getRequester().getId(), deciderId);
    }

    public void validateAdminPermissionAndReject(Long requestId) {
        webTokenService.ensureAdmin();

        reject(requestId);
    }

    public void reject(Long requestId) {
        var request = requestRepository.findById(requestId).orElseThrow();
        var service = request.getType().getService();
        service.reject(request);
    }

    public void cancel(Long requestId, 
                       Long deciderId) {
        var request = requestRepository.findById(requestId).orElseThrow();
        var isRequester = request.getRequester().getId()
                .equals(deciderId);

        try {
            webTokenService.ensureAdmin();
        } catch (UnauthorizedException exception) {
            if (!isRequester) {
                throw UnauthorizedException.unauthorized();
            }
        }


        var service = request.getType().getService();
        var previousStatus = request.getStatus();
        service.cancel(request);

        if (previousStatus == Request.Status.ACCEPTED) {
            historyLogService.createReverse(request, "Anulowanie", deciderId);
        }
    }

}
