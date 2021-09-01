package info.fingo.urlopia.history;

import info.fingo.urlopia.UrlopiaApplication;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.config.persistance.filter.Operator;
import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.holidays.WorkingDaysCalculator;
import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.user.NoSuchUserException;
import info.fingo.urlopia.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class HistoryLogService {

    private final HistoryLogRepository historyLogRepository;
    private final UserRepository userRepository;
    private final WorkingDaysCalculator workingDaysCalculator;
    private final HolidayService holidayService;


    public List<HistoryLogExcerptProjection> get(Filter filter) {
        return historyLogRepository.findAll(filter, HistoryLogExcerptProjection.class);
    }

    public List<HistoryLogExcerptProjection> get(LocalDate date, Long userId) {
        var startOfDay = LocalDateTime.of(date, LocalTime.MIN);
        var endOfDay = LocalDateTime.of(date, LocalTime.MAX);
        var formatter = DateTimeFormatter.ofPattern(UrlopiaApplication.DATE_TIME_FORMAT);
        var formattedStartOfDay = formatter.format(startOfDay);
        var formattedEndOfDay = formatter.format(endOfDay);
        var filter = Filter.newBuilder()
                .and("user.id", Operator.EQUAL, String.valueOf(userId))
                .and("created", Operator.GREATER_OR_EQUAL, formattedStartOfDay)
                .and("created", Operator.LESS_OR_EQUAL, formattedEndOfDay)
                .build();
        return historyLogRepository.findAll(filter, HistoryLogExcerptProjection.class);
    }

    public List<HistoryLogExcerptProjection> get(Long userId,
                                                 Integer year,
                                                 Filter filter) {
        if (year == null) {
            return historyLogRepository.findByUserId(userId);
        }
        var formatter = DateTimeFormatter.ofPattern(UrlopiaApplication.DATE_TIME_FORMAT);
        var yearStart = LocalDateTime.of(year, 1, 1, 0, 0).format(formatter);
        var yearEnd = LocalDateTime.of(year, 12, 31, 23, 59).format(formatter);

        Filter filterWithRestrictions = filter.toBuilder()
                .and("user.id", Operator.EQUAL, userId.toString())
                .and("created", Operator.GREATER_OR_EQUAL, yearStart)
                .and("created", Operator.LESS_OR_EQUAL, yearEnd)
                .build();
        return this.get(filterWithRestrictions);
    }

    public List<HistoryLog> getFromYear(Long userId,
                                        Integer year) {
        var yearStart = LocalDateTime.of(year, 1, 1, 0, 0);
        var nextYearStart = LocalDateTime.of(year + 1, 1, 1, 0, 0);
        return historyLogRepository.findLogsByUserIdAndCreatedBetween(userId, yearStart, nextYearStart);
    }

    public void create(HistoryLogInput historyLog,
                       Long targetUserId,
                       Long deciderId) {
        var targetUser = userRepository
                .findById(targetUserId)
                .orElseThrow(() -> {
                    log.error("Could not create new history log for nonexistent user with id: {}", targetUserId);
                    return NoSuchUserException.invalidId();
                });
        var decider = userRepository
                .findById(deciderId)
                .orElseThrow(() -> {
                    log.error(("Could not create new history log for user with id: {}" +
                            " because decider with id: {} does not exist"), targetUserId, deciderId);
                    return NoSuchUserException.invalidId();
                });
        var prevHistoryLog = historyLogRepository.findFirstByUserIdOrderByIdDesc(targetUserId);
        var hoursChange = historyLog.getHours();
        var comment = Optional.ofNullable(historyLog.getComment()).orElse("");
        var history = new HistoryLog(targetUser, decider, hoursChange, comment, prevHistoryLog);
        historyLogRepository.save(history);
        var loggerInfo = "A new history log with id: %d has been added for user with id: %d"
                .formatted(history.getId(), targetUser.getId());
        log.info(loggerInfo);
    }

    public void createReverse(Request request,
                              String comment,
                              Long deciderId) {
        var reversible = historyLogRepository.findFirstByRequestId(request.getId());
        var hours = -reversible.getHours();
        var targetUserId = reversible.getUser().getId();
        this.create(request, hours, comment, targetUserId, deciderId);
        var loggerInfo = "A history log with id: %d has been removed from user with id: %d"
                .formatted(reversible.getId(), targetUserId);
        log.info(loggerInfo);
    }

    public void create(Request request,
                       Float hours,
                       String comment,
                       Long targetUserId,
                       Long deciderId) {
        var targetUser = userRepository
                .findById(targetUserId)
                .orElseThrow(() -> {
                    log.error("Could not create new history log for nonexistent user with id: {}", targetUserId);
                    return NoSuchUserException.invalidId();
                });
        var decider = userRepository
                .findById(deciderId)
                .orElseThrow(() -> {
                    log.error(("Could not create new history log for user with id: {}" +
                            " because decider with id: {} does not exist"), targetUserId, deciderId);
                    return NoSuchUserException.invalidId();
                });

        var prevHistoryLog = historyLogRepository.findFirstByUserIdOrderByIdDesc(targetUserId);
        var historyLog = new HistoryLog(request, targetUser, decider, hours, comment, prevHistoryLog);
        historyLogRepository.save(historyLog);
        var loggerInfo = "A new history log with id: %d has been added for user with id: %d"
                .formatted(historyLog.getId(), targetUser.getId());
        log.info(loggerInfo);
    }

    // *** ACTIONS ***

    public WorkTimeResponse countRemainingDays(Long userId) {
        var hours = historyLogRepository.sumHours(userId);
        var workTime = userRepository.findById(userId).orElseThrow().getWorkTime();
        return new WorkTimeResponse(workTime, hours);
    }

    public Float countRemainingHours(Long userId) {
        return historyLogRepository.sumHours(userId);
    }

    public float countRemainingHoursForYear(Long userId,
                                            Integer year) {
        var hours = 0f;
        var logs = historyLogRepository.findLogsByUserId(userId);
        for (var log : logs) {
            var request = log.getRequest();
            if (request == null) {
                if (log.getCreated().getYear() <= year) {
                    hours += log.getHours();
                }
            } else if (request.isNormal()) {
                hours = countRemainingHoursFromNormalRequest(request, year, log, hours);
            }
        }
        return hours;
    }

    private float countRemainingHoursFromNormalRequest(Request request,
                                                       Integer year,
                                                       HistoryLog log,
                                                       float hours) {
        var startDate = request.getStartDate();
        var endDate = request.getEndDate();
        if (startDate.getYear() <= year && endDate.getYear() <= year) {
            hours += log.getHours();
        } else if (startDate.getYear() <= year) {
            var lastDateOfYear = LocalDate.of(year, 12, 31);
            var workingDays = workingDaysCalculator.calculate(startDate, lastDateOfYear);
            hours += workingDays * log.getUserWorkTime();
        }
        return hours;
    }

    public List<HistoryLogExcerptProjection> getRecent(Long userId) {
        return historyLogRepository.findFirst5ByUserIdOrderByCreatedDesc(userId);
    }

    public Integer getEmploymentYear(Long userId) {
        var firstLog = historyLogRepository.findFirstByUserIdOrderById(userId);
        var firstDate = Optional.ofNullable(firstLog)
                .map(HistoryLog::getCreated).orElse(LocalDateTime.now());
        return firstDate.getYear();
    }

    public boolean checkIfWorkedFullTimeForTheWholeYear(Long userId,
                                                        int year) {
        var historyLogFromYear = getFromYear(userId, year);
        return historyLogFromYear.stream()
                .allMatch(historyLog -> historyLog.getUserWorkTime() == 8.0);
    }

    public double countTheHoursUsedDuringTheYear(Long userId,
                                                 int year) {
        var historyLogs = historyLogRepository.findLogsByUserId(userId).stream()
                .filter(historyLog -> historyLog.getRequest() != null)
                .filter(historyLog -> historyLog.getHours() < 0)
                .toList();

        double usedHours = 0.0;
        for (var historyLog : historyLogs) {
            var request = historyLog.getRequest();
            if (isRequestActiveInYear(request, year)) {
                usedHours += countUsedHoursFromYearAndRequest(year, request, historyLog);
            }
        }

        return usedHours;
    }

    private boolean isRequestActiveInYear(Request request, int year) {
        var yearStart = LocalDate.of(year, 1, 1);
        var yearEnd = LocalDate.of(year, 12, 31);

        var overlappingOnLeft = requestOverlappingOnLeft(request, year);
        var isFullyIncluded = !request.getStartDate().isBefore(yearStart) && !request.getEndDate().isAfter(yearEnd);
        var overlappingOnRight = requestOverlappingOnRight(request, year);

        return overlappingOnLeft || isFullyIncluded || overlappingOnRight;
    }

    private boolean requestOverlappingOnLeft(Request request, int year) {
        var yearStart = LocalDate.of(year, 1, 1);
        return request.getStartDate().isBefore(yearStart) && !request.getEndDate().isBefore(yearStart);
    }

    private boolean requestOverlappingOnRight(Request request, int year) {
        var yearEnd = LocalDate.of(year, 12, 31);
        return !request.getStartDate().isAfter(yearEnd) && request.getEndDate().isAfter(yearEnd);
    }

    private double countUsedHoursFromYearAndRequest(int year,
                                                    Request request,
                                                    HistoryLog historyLog){
        if (requestOverlappingOnLeft(request, year)) {
            return -countUsedHoursInOverlappingRequest(year, request, historyLog, true);
        } else if (requestOverlappingOnRight(request, year)) {
            return -countUsedHoursInOverlappingRequest(year, request, historyLog, false);
        } else {
            return -historyLog.getHours();
        }
    }

    private double countUsedHoursInOverlappingRequest(int year,
                                                      Request request,
                                                      HistoryLog historyLog,
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

        var hoursUsedPerDay = (historyLog.getHours()) / numOfWorkingDays;
        return hoursUsedPerDay * numOfWorkingDaysBeforeNextYear;
    }
}
