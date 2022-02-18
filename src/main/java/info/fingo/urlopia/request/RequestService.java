package info.fingo.urlopia.request;

import info.fingo.urlopia.UrlopiaApplication;
import info.fingo.urlopia.api.v2.calendar.AbsentUserOutput;
import info.fingo.urlopia.api.v2.exceptions.UnauthorizedException;
import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService;
import info.fingo.urlopia.config.authentication.WebTokenService;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.config.persistance.filter.Operator;
import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.request.absence.BaseRequestInput;
import info.fingo.urlopia.request.absence.SpecialAbsenceReason;
import info.fingo.urlopia.request.absence.SpecialAbsenceRequestService;
import info.fingo.urlopia.request.normal.RequestTooFarInThePastException;
import info.fingo.urlopia.team.Team;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

@Service
@Transactional
@Slf4j
public class RequestService {
    private final RequestRepository requestRepository;
    private final HistoryLogService historyLogService;
    private final UserService userService;
    private final WebTokenService webTokenService;
    private final HolidayService holidayService;
    private final PresenceConfirmationService presenceConfirmationService;

    private static final String REQUEST_NOT_EXIST_MESSAGE = "Request with id: {} does not exist";
    private static final String REQUESTER_ID = "requester.id";
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";
    private static final String STATUS = "status";

    @Autowired
    public RequestService(RequestRepository requestRepository,
                          HistoryLogService historyLogService,
                          UserService userService,
                          WebTokenService webTokenService,
                          HolidayService holidayService,
                          @Lazy PresenceConfirmationService presenceConfirmationService) {
        this.requestRepository = requestRepository;
        this.historyLogService = historyLogService;
        this.userService = userService;
        this.webTokenService = webTokenService;
        this.holidayService = holidayService;
        this.presenceConfirmationService = presenceConfirmationService;
    }

    public Page<RequestExcerptProjection> getFromUser(Long userId, Filter filter, Pageable pageable) {
        Filter filterWithRestrictions = filter.toBuilder()
                .and(REQUESTER_ID, Operator.EQUAL, userId.toString())
                .build();
        return this.get(filterWithRestrictions, pageable);
    }

    public Page<RequestExcerptProjection> get(Filter filter, Pageable pageable) {
        return requestRepository.findAll(filter, pageable, RequestExcerptProjection.class);
    }

    public List<Request> getAll(Filter filter) {
        return requestRepository.findAll(filter);
    }

    public List<Request> getByUserAndDate(Long userId, LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(UrlopiaApplication.DATE_FORMAT);
        String formattedDate = formatter.format(date);
        Filter filter = Filter.newBuilder()
                .and(REQUESTER_ID, Operator.EQUAL, String.valueOf(userId))
                .and(START_DATE, Operator.LESS_OR_EQUAL, formattedDate)
                .and(END_DATE, Operator.GREATER_OR_EQUAL, formattedDate)
                .build();
        return this.requestRepository.findAll(filter);
    }

    public boolean hasAcceptedByDateIntervalAndUser(LocalDate startDate,
                                                    LocalDate endDate,
                                                    Long userId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(UrlopiaApplication.DATE_FORMAT);
        String formattedStartDate = formatter.format(startDate);
        String formattedEndDate = formatter.format(endDate);
        Filter filter = Filter.newBuilder()
                .and(REQUESTER_ID, Operator.EQUAL, String.valueOf(userId))
                .and(START_DATE, Operator.GREATER_OR_EQUAL, formattedStartDate)
                .and(END_DATE, Operator.LESS_OR_EQUAL, formattedEndDate)
                .and(STATUS,Operator.EQUAL, Request.Status.ACCEPTED.name())
                .build();
        return !requestRepository.findAll(filter).isEmpty();
    }

    public List<Request> get(Long userId, Integer year) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        return requestRepository.findByRequesterIdAndYear(userId, year);
    }

    public Request getById(Long requestId) {
        return requestRepository
                .findById(requestId)
                .orElseThrow(() -> {
                    log.error(REQUEST_NOT_EXIST_MESSAGE, requestId);
                    return new NoSuchElementException();
                });
    }

    public Request create(Long userId, BaseRequestInput requestInput) {
        var type = requestInput.getType();

        if (type != RequestType.SPECIAL) {
            ensureRequestIsNotTooFarInThePast(requestInput.getStartDate());
        }

        RequestTypeService service = type.getService();
        return service.create(userId, requestInput);
    }

    public List<AbsentUserOutput> getVacations(LocalDate date, Filter filter) {
        var users = userService.get(filter);
        return users.stream()
                .filter(user -> isVacationing(user, date))
                .map(this::createAbsentUserOutput)
                .toList();
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
                .toList();
    }

    public double countTheHoursUsedDuringTheYear(Long userId,
                                                 int year){
        return requestRepository.findByRequesterIdAndYear(userId,year)
                        .stream()
                        .filter(request -> request.getType() == RequestType.NORMAL)
                        .filter(request -> request.getStatus() == Request.Status.ACCEPTED)
                        .mapToDouble(request -> countUsedHoursFromYearAndRequest(year,request))
                        .sum();
    }

    private double countUsedHoursFromYearAndRequest(int year,
                                                    Request request){
        if (requestOverlappingOnLeft(request, year)) {
            return countUsedHoursInOverlappingRequest(year, request,true);
        } else if (requestOverlappingOnRight(request, year)) {
            return countUsedHoursInOverlappingRequest(year, request,false);
        } else {
            return request.getWorkingHours();
        }
    }

    private boolean requestOverlappingOnLeft(Request request, int year) {
        var yearStart = LocalDate.of(year, 1, 1);
        return request.getStartDate().isBefore(yearStart) && !request.getEndDate().isBefore(yearStart);
    }

    private boolean requestOverlappingOnRight(Request request, int year) {
        var yearEnd = LocalDate.of(year, 12, 31);
        return !request.getStartDate().isAfter(yearEnd) && request.getEndDate().isAfter(yearEnd);
    }

    private double countUsedHoursInOverlappingRequest(int year,
                                                      Request request,
                                                      boolean overlappedOnLeft) {
        var requestStartDate = request.getStartDate();
        var requestEndDate = request.getEndDate();
        var yearStart = LocalDate.of(year, 1, 1);
        var yearEnd = LocalDate.of(year, 12, 31);

        Predicate<LocalDate> filter = overlappedOnLeft ? day -> !day.isBefore(yearStart) : day -> !day.isAfter(yearEnd);

        var workingDays = requestStartDate.datesUntil(requestEndDate.plusDays(1))
                .filter(holidayService::isWorkingDay)
                .toList();

        var numOfWorkingDaysBeforeNextYear = workingDays.stream()
                .filter(filter)
                .count();
        var numOfWorkingDays = workingDays.size();

        if (numOfWorkingDays == 0) {
            return 0;
        }

        var hoursUsedPerDay = request.getWorkingHours() / numOfWorkingDays;
        return hoursUsedPerDay * numOfWorkingDaysBeforeNextYear;
    }


    private void ensureRequestIsNotTooFarInThePast(LocalDate startDate) {
        if (startDate.isBefore(LocalDate.now().minusMonths(1))) {
            log.error("Request that starts on {} could not be created, because it's too far in the past.", startDate);
            throw RequestTooFarInThePastException.requestTooFarInThePast();
        }
    }

    private AbsentUserOutput createAbsentUserOutput(User user){
        var userName = user.getFullName();
        var teams = user.getTeams().stream()
                .map(Team::getName)
                .toList();
        return new AbsentUserOutput(userName, teams);
    }

    private VacationDay createVacationDay(List<User> users,
                                          LocalDate date) {
        var namesOfVacationingUsers = users.stream()
                .filter(user -> isVacationing(user, date))
                .map(user -> user.getFirstName() + " " + user.getLastName())
                .toList();
        return new VacationDay(date, namesOfVacationingUsers);
    }

    private List<User> teammatesOf(User user) {
        return user.getTeams().stream()
                .flatMap(team -> team.getUsers().stream())
                .distinct()
                .toList();
    }

    private boolean isWeekend(LocalDate date) {
        var dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    public boolean isVacationing(User user,
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
        var request = requestRepository
                .findById(requestId)
                .orElseThrow(() -> {
                    log.error(REQUEST_NOT_EXIST_MESSAGE, requestId);
                    return new NoSuchElementException();
                });
        var service = request.getType().getService();
        service.accept(request);

        var requesterId = request.getRequester().getId();
        var startDate = request.getStartDate();
        var endDate = request.getEndDate();
        presenceConfirmationService.deletePresenceConfirmations(requesterId, startDate, endDate);

        float workingHours = request.getWorkingDays() * request.getRequester().getWorkTime();
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var term = String.format("%s - %s",
                                 startDate.format(formatter),
                                 endDate.format(formatter));
        historyLogService.create(request, -workingHours, term, request.getRequester().getId(), deciderId);
    }

    public void validateAdminPermissionAndReject(Long requestId) {
        webTokenService.ensureAdmin();

        reject(requestId);
    }

    public void reject(Long requestId) {
        var request = requestRepository
                .findById(requestId)
                .orElseThrow(() -> {
                    log.error(REQUEST_NOT_EXIST_MESSAGE, requestId);
                    return new NoSuchElementException();
                });
        var service = request.getType().getService();
        service.reject(request);
    }

    public void cancel(Long requestId,
                       Long deciderId) {
        var request = requestRepository
                .findById(requestId)
                .orElseThrow(() -> {
                    log.error(REQUEST_NOT_EXIST_MESSAGE, requestId);
                    return new NoSuchElementException();
                });
        var requesterId = request.getRequester().getId();
        var isRequester = requesterId.equals(deciderId);

        try {
            webTokenService.ensureAdmin();
        } catch (UnauthorizedException exception) {
            if (!isRequester) {
                log.error("User with id: {} has no permissions to cancel request with id: {}",
                        requesterId, requestId);
                throw UnauthorizedException.unauthorized();
            }
        }


        var service = request.getType().getService();

        if (service instanceof SpecialAbsenceRequestService) {
            webTokenService.ensureAdmin();
        }

        var previousStatus = request.getStatus();
        service.cancel(request);

        if (previousStatus == Request.Status.ACCEPTED) {
            historyLogService.createReverse(request, cancellationCommentFor(request), deciderId);
        }
    }

    private String cancellationCommentFor(Request request) {
        var startDate = request.getStartDate();
        var endDate = request.getEndDate();

        switch (request.getType()) {
            case NORMAL, OCCASIONAL:
                return "Anulowanie urlopu w dniach: %s - %s".formatted(startDate, endDate);
            case SPECIAL:
                var typeInfo = request.getSpecialTypeInfo();
                var reason = SpecialAbsenceReason.valueOf(typeInfo).getTranslatedReason();
                return "Anulowanie nieobecności w dniach %s - %s (%s)".formatted(startDate, endDate, reason);
            default:
                return "Anulowanie";
        }
    }
}
