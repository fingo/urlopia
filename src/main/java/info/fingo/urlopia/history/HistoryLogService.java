package info.fingo.urlopia.history;

import info.fingo.urlopia.UrlopiaApplication;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.config.persistance.filter.Operator;
import info.fingo.urlopia.holidays.WorkingDaysCalculator;
import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.request.RequestType;
import info.fingo.urlopia.user.User;
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
    public HistoryLogService(HistoryLogRepository historyLogRepository, UserRepository userRepository, WorkingDaysCalculator workingDaysCalculator) {
        this.historyLogRepository = historyLogRepository;
        this.userRepository = userRepository;
        this.workingDaysCalculator = workingDaysCalculator;
    }

    public List<HistoryLogExcerptProjection> get(Filter filter) {
        return historyLogRepository.findAll(filter, HistoryLogExcerptProjection.class);
    }

    public List<HistoryLogExcerptProjection> get(Long userId, Integer year, Filter filter) {
        if (year == null) {
            return historyLogRepository.findByUserId(userId);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(UrlopiaApplication.DATE_TIME_FORMAT);
        String yearStart = LocalDateTime.of(year, 1, 1, 0, 0).format(formatter);
        String yearEnd = LocalDateTime.of(year, 12, 31, 23, 59).format(formatter);

        Filter filterWithRestrictions = filter.toBuilder()
                .and("user.id", Operator.EQUAL, userId.toString())
                .and("created", Operator.GREATER_OR_EQUAL, yearStart)
                .and("created", Operator.LESS_OR_EQUAL, yearEnd)
                .build();
        return this.get(filterWithRestrictions);
    }

    public List<HistoryLog> getFromYear(Long userId, Integer year) {
        LocalDateTime yearStart = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime nextYearStart = LocalDateTime.of(year + 1, 1, 1, 0, 0);
        return historyLogRepository.findLogsByUserIdAndCreatedBetween(userId, yearStart, nextYearStart);
    }

    public void create(HistoryLogInput historyLog, Long targetUserId, Long deciderId) {
        User targetUser = userRepository.findById(targetUserId).orElseThrow();
        User decider = userRepository.findById(deciderId).orElseThrow();
        HistoryLog prevHistoryLog = historyLogRepository.findFirstByUserIdOrderByIdDesc(targetUserId);
        Float hoursChange = historyLog.getHours();
        String comment = Optional.ofNullable(historyLog.getComment()).orElse("");
        HistoryLog history = new HistoryLog(targetUser, decider, hoursChange, comment, prevHistoryLog);
        historyLogRepository.save(history);
    }

    public void createReverse(Request request, String comment, Long deciderId) {
        HistoryLog reversible = historyLogRepository.findFirstByRequestId(request.getId());
        Float hours = -reversible.getHours();
        Long targetUserId = reversible.getUser().getId();
        this.create(request, hours, comment, targetUserId, deciderId);
    }

    public void create(Request request, Float hours, String comment, Long targetUserId, Long deciderId) {
        User targetUser = userRepository.findById(targetUserId).orElseThrow();
        User decider = userRepository.findById(deciderId).orElseThrow();
        HistoryLog prevHistoryLog = historyLogRepository.findFirstByUserIdOrderByIdDesc(targetUserId);
        HistoryLog historyLog = new HistoryLog(request, targetUser, decider, hours, comment, prevHistoryLog);
        historyLogRepository.save(historyLog);
    }

    // *** ACTIONS ***

    public WorkTimeResponse countRemainingDays(Long userId) {
        Float hours = historyLogRepository.sumHours(userId);
        Float workTime = userRepository.findById(userId).orElseThrow().getWorkTime();
        return new WorkTimeResponse(workTime, hours);
    }

    public Float countRemainingHours(Long userId) {
        return historyLogRepository.sumHours(userId);
    }

    public Float countRemainingHoursForYear(Long userId, Integer year) {
        Float hours = 0f;
        List<HistoryLog> logs = historyLogRepository.findLogsByUserId(userId);
        for (HistoryLog log : logs) {
            Request request = log.getRequest();
            if (request == null) {
                if (log.getCreated().getYear() <= year) {
                    hours += log.getHours();
                }
            } else if (request.isNormal()) {
                LocalDate startDate = request.getStartDate();
                LocalDate endDate = request.getEndDate();
                if (startDate.getYear() <= year && endDate.getYear() <= year) {
                    hours += log.getHours();
                } else if (startDate.getYear() <= year && endDate.getYear() > year) {
                    LocalDate lastDateOfYear = LocalDate.of(year, 12, 31);
                    int workingDays = workingDaysCalculator.calculate(startDate, lastDateOfYear);
                    hours += workingDays * log.getUserWorkTime();
                }
            }
        }
        return hours;
    }

    public List<HistoryLogExcerptProjection> getRecent(Long userId) {
        return historyLogRepository.findFirst5ByUserIdOrderByCreatedDesc(userId);
    }

    public Integer getEmploymentYear(Long userId) {
        HistoryLog firstLog = historyLogRepository.findFirstByUserIdOrderById(userId);
        LocalDateTime firstDate = Optional.ofNullable(firstLog)
                .map(HistoryLog::getCreated).orElse(LocalDateTime.now());
        return firstDate.getYear();
    }

}
