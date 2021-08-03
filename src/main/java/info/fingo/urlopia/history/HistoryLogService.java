package info.fingo.urlopia.history;

import info.fingo.urlopia.UrlopiaApplication;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.config.persistance.filter.Operator;
import info.fingo.urlopia.holidays.WorkingDaysCalculator;
import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HistoryLogService {

    private final HistoryLogRepository historyLogRepository;
    private final UserRepository userRepository;
    private final WorkingDaysCalculator workingDaysCalculator;

    @Autowired
    public HistoryLogService(HistoryLogRepository historyLogRepository,
                             UserRepository userRepository,
                             WorkingDaysCalculator workingDaysCalculator) {
        this.historyLogRepository = historyLogRepository;
        this.userRepository = userRepository;
        this.workingDaysCalculator = workingDaysCalculator;
    }

    public List<HistoryLogExcerptProjection> get(Filter filter) {
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
        var targetUser = userRepository.findById(targetUserId).orElseThrow();
        var decider = userRepository.findById(deciderId).orElseThrow();
        var prevHistoryLog = historyLogRepository.findFirstByUserIdOrderByIdDesc(targetUserId);
        var hoursChange = historyLog.getHours();
        var comment = Optional.ofNullable(historyLog.getComment()).orElse("");
        var history = new HistoryLog(targetUser, decider, hoursChange, comment, prevHistoryLog);
        historyLogRepository.save(history);
    }

    public void createReverse(Request request,
                              String comment,
                              Long deciderId) {
        var reversible = historyLogRepository.findFirstByRequestId(request.getId());
        var hours = -reversible.getHours();
        var targetUserId = reversible.getUser().getId();
        this.create(request, hours, comment, targetUserId, deciderId);
    }

    public void create(Request request,
                       Float hours,
                       String comment,
                       Long targetUserId,
                       Long deciderId) {
        var targetUser = userRepository.findById(targetUserId).orElseThrow();
        var decider = userRepository.findById(deciderId).orElseThrow();
        var prevHistoryLog = historyLogRepository.findFirstByUserIdOrderByIdDesc(targetUserId);
        var historyLog = new HistoryLog(request, targetUser, decider, hours, comment, prevHistoryLog);
        historyLogRepository.save(historyLog);
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
                hours = countRemainingHoursFromNormalRequest(request,year,log,hours);
            }
        }
        return hours;
    }

    private float countRemainingHoursFromNormalRequest(Request request,
                                                       Integer year,
                                                       HistoryLog log,
                                                       float hours){
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

}
