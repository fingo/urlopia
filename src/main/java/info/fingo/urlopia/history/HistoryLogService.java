package info.fingo.urlopia.history;

import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HistoryLogService {

    private final HistoryLogRepository historyLogRepository;
    private final UserRepository userRepository;

    public HistoryLogService(HistoryLogRepository historyLogRepository, UserRepository userRepository) {
        this.historyLogRepository = historyLogRepository;
        this.userRepository = userRepository;
    }

    public List<HistoryLogExcerptProjection> get(Long userId, Integer year) {
        if (year == null) {
            return historyLogRepository.findByUserId(userId);
        }
        LocalDateTime yearStart = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime nextYearStart = LocalDateTime.of(year + 1, 1, 1, 0, 0);
        return historyLogRepository.findByUserIdAndCreatedBetween(userId, yearStart, nextYearStart);
    }

    public void create(HistoryLogInput historyLog, Long targetUserId, Long deciderId) {
        User targetUser = userRepository.findOne(targetUserId);
        User decider = userRepository.findOne(deciderId);
        HistoryLog prevHistoryLogLog = historyLogRepository.findFirstByUserIdOrderByCreatedDesc(targetUserId);
        Float hoursChange = historyLog.getHours();
        String comment = Optional.ofNullable(historyLog.getComment()).orElse("");
        HistoryLog history = new HistoryLog(targetUser, decider, hoursChange, comment, prevHistoryLogLog);
        historyLogRepository.save(history);
    }

    public void create(Request request, Float hours, String comment, Long targetUserId, Long deciderId) {
        User targetUser = userRepository.findOne(targetUserId);
        User decider = userRepository.findOne(deciderId);
        HistoryLog prevHistoryLogLog = historyLogRepository.findFirstByUserIdOrderByCreatedDesc(targetUserId);
        HistoryLog historyLog = new HistoryLog(request, targetUser, decider, hours, comment, prevHistoryLogLog);
        historyLogRepository.save(historyLog);
    }

    public void createReverse(Request request, String comment, Long deciderId) {
        HistoryLog reversible = historyLogRepository.findFirstByRequestId(request.getId());
        Float hours = -reversible.getHours();
        Long targetUserId = reversible.getUser().getId();
        this.create(request, hours, comment, targetUserId, deciderId);
    }

    // *** ACTIONS ***

    public WorkTimeResponse countRemainingDays(Long userId) {
        Float hours = historyLogRepository.sumHours(userId);
        Float workTime = userRepository.findOne(userId).getWorkTime();
        return new WorkTimeResponse(workTime, hours);
    }

    public Float countRemainingHours(Long userId) {
        return historyLogRepository.sumHours(userId);
    }

    public List<HistoryLogExcerptProjection> getRecent(Long userId) {
        return historyLogRepository.findFirst5ByUserIdOrderByCreatedDesc(userId);
    }

    public Integer getEmploymentYear(Long userId) {
        HistoryLog firstLog = historyLogRepository.findFirstByUserIdOrderByCreated(userId);
        LocalDateTime firstDate = Optional.ofNullable(firstLog)
                .map(HistoryLog::getCreated).orElse(LocalDateTime.now());
        return firstDate.getYear();
    }

}
