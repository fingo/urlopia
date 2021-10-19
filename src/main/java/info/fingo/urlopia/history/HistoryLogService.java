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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class HistoryLogService {

    private final HistoryLogRepository historyLogRepository;
    private final UserRepository userRepository;
    private final WorkingDaysCalculator workingDaysCalculator;

    private static final String CREATED_FILTER = "created";


    public List<HistoryLogExcerptProjection> get(Filter filter) {
        return historyLogRepository.findAll(filter, HistoryLogExcerptProjection.class);
    }

    public Page<HistoryLogExcerptProjection> get(Filter filter, Pageable pageable) {
        return historyLogRepository.findAll(filter, pageable, HistoryLogExcerptProjection.class);
    }

    public List<HistoryLogExcerptProjection> get(LocalDate date, Long userId) {
        var startOfDay = LocalDateTime.of(date, LocalTime.MIN);
        var endOfDay = LocalDateTime.of(date, LocalTime.MAX);
        var formatter = DateTimeFormatter.ofPattern(UrlopiaApplication.DATE_TIME_FORMAT);
        var formattedStartOfDay = formatter.format(startOfDay);
        var formattedEndOfDay = formatter.format(endOfDay);
        var filter = Filter.newBuilder()
                .and("user.id", Operator.EQUAL, String.valueOf(userId))
                .and(CREATED_FILTER, Operator.GREATER_OR_EQUAL, formattedStartOfDay)
                .and(CREATED_FILTER, Operator.LESS_OR_EQUAL, formattedEndOfDay)
                .build();
        return historyLogRepository.findAll(filter, HistoryLogExcerptProjection.class);
    }

    public List<HistoryLogExcerptProjection> get(Long userId,
                                                 Integer year,
                                                 Filter filter) {
        return get(userId, year, filter, Pageable.unpaged()).getContent();
    }

    public Page<HistoryLogExcerptProjection> get(Long userId,
                                                 Integer year,
                                                 Filter filter,
                                                 Pageable pageable) {
        if (year == null) {
            return historyLogRepository.findByUserId(userId, pageable);
        }
        var formatter = DateTimeFormatter.ofPattern(UrlopiaApplication.DATE_TIME_FORMAT);
        var yearStart = LocalDateTime.of(year, 1, 1, 0, 0).format(formatter);
        var yearEnd = LocalDateTime.of(year, 12, 31, 23, 59).format(formatter);

        Filter filterWithRestrictions = filter.toBuilder()
                .and("user.id", Operator.EQUAL, userId.toString())
                .and(CREATED_FILTER, Operator.GREATER_OR_EQUAL, yearStart)
                .and(CREATED_FILTER, Operator.LESS_OR_EQUAL, yearEnd)
                .build();
        return get(filterWithRestrictions, pageable);
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
}
