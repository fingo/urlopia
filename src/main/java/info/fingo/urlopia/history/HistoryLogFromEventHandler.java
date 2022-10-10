package info.fingo.urlopia.history;

import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class HistoryLogFromEventHandler {

    private final static String USER_DEACTIVATED_EVENT_COMMENT = "Użytkownik został dezaktywowany";
    private final static String USER_ACTIVATED_EVENT_COMMENT = "Użytkownik został aktywowany";
    private final static String CHANGE_TO_EC_EVENT_COMMENT = "Użytkownik zmienił rodzaj umowy na pracownik";
    private final static String CHANGE_TO_B2B_EVENT_COMMENT = "Użytkownik zmienił rodzaj umowy na współpracownik";
    private final static String CHANGE_WORK_TIME_EVENT_COMMENT_TEMPLATE = "Zmieniono etat z: %s na %s";


    private final HistoryLogRepository historyLogRepository;

    public HistoryLog addUserDeactivatedEvent(User user,
                                              LocalDateTime create){
        var prevHistoryLog = historyLogRepository.findFirstByUserIdOrderByIdDesc(user.getId());
        var historyLog = new HistoryLog(user, create,0, USER_DEACTIVATED_EVENT_COMMENT, prevHistoryLog, UserDetailsChangeEvent.USER_DEACTIVATED);
        historyLogRepository.save(historyLog);
        return historyLog;
    }

    public HistoryLog addUserActivationEvent(User user,
                                             LocalDateTime create){
        var prevHistoryLog = historyLogRepository.findFirstByUserIdOrderByIdDesc(user.getId());
        var historyLog = new HistoryLog(user, create, 0, USER_ACTIVATED_EVENT_COMMENT, prevHistoryLog, UserDetailsChangeEvent.USER_ACTIVATED);
        historyLogRepository.save(historyLog);
        return historyLog;
    }

    public HistoryLog addChangeToECEvent(User user,
                                         LocalDateTime create){
        var prevHistoryLog = historyLogRepository.findFirstByUserIdOrderByIdDesc(user.getId());
        var historyLog = new HistoryLog(user, create, 0, CHANGE_TO_EC_EVENT_COMMENT, prevHistoryLog, UserDetailsChangeEvent.USER_CHANGE_TO_EC);
        historyLogRepository.save(historyLog);
        return historyLog;
    }

    public HistoryLog addChangeToB2BEvent(User user,
                                          LocalDateTime create){
        var prevHistoryLog = historyLogRepository.findFirstByUserIdOrderByIdDesc(user.getId());
        var historyLog = new HistoryLog(user,create, 0, CHANGE_TO_B2B_EVENT_COMMENT, prevHistoryLog, UserDetailsChangeEvent.USER_CHANGE_TO_B2B);
        historyLogRepository.save(historyLog);
        return historyLog;
    }

    public HistoryLog addChangeWorkTime(User user,
                                        LocalDateTime create,
                                        float oldWorkTime,
                                        float newWorkTime){
        var prevHistoryLog = historyLogRepository.findFirstByUserIdOrderByIdDesc(user.getId());
        var comment = String.format(CHANGE_WORK_TIME_EVENT_COMMENT_TEMPLATE, oldWorkTime, newWorkTime);
        var historyLog = new HistoryLog(user, create, 0, comment, prevHistoryLog, UserDetailsChangeEvent.USER_CHANGE_WORK_TIME);
        historyLogRepository.save(historyLog);
        return historyLog;
    }
}
