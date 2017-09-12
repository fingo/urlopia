package info.fingo.urlopia.history;

import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HistoryLogService {

    private final HistoryLogRepository historyRepository;
    private final UserRepository userRepository;

    public HistoryLogService(HistoryLogRepository historyRepository, UserRepository userRepository) {
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
    }

    public List<HistoryLogExcerptProjection> getHistoryLogs(Long userId, Integer year) {
        if (year == null) {
            return historyRepository.findByUserId(userId);
        }
        LocalDateTime yearStart = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime nextYearStart = LocalDateTime.of(year + 1, 1, 1, 0, 0);
        return historyRepository.findByUserIdAndCreatedBetween(userId, yearStart, nextYearStart);
    }

    public void addLog(HistoryLogInput historyLog, Long targetUserId, Long deciderId) {
        User targetUser = userRepository.findOne(targetUserId);
        User decider = userRepository.findOne(deciderId);
        History prevHistoryLog = historyRepository.findFirstByUserIdOrderByCreatedDesc(targetUserId);
        Float hoursChange = historyLog.getHours();
        String comment = Optional.ofNullable(historyLog.getComment()).orElse("");
        History history = new History(targetUser, decider, hoursChange, comment, prevHistoryLog);
        historyRepository.save(history);
    }

    public WorkTimeResponse countRemainingDays(Long userId) {
        Float hours = historyRepository.sumHours(userId);
        Float workTime = userRepository.findOne(userId).getWorkTime();
        return new WorkTimeResponse(workTime, hours);
    }

    public List<HistoryLogExcerptProjection> getRecent(Long userId) {
        return historyRepository.findFirst5ByUserIdOrderByCreatedDesc(userId);
    }

    public Integer getEmploymentYear(Long userId) {
        History firstLog = historyRepository.findFirstByUserIdOrderByCreated(userId);
        LocalDateTime firstDate = Optional.ofNullable(firstLog)
                .map(History::getCreated).orElse(LocalDateTime.now());
        return firstDate.getYear();
    }

}
