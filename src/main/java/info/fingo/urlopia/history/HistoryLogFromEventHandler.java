package info.fingo.urlopia.history;

import info.fingo.urlopia.user.User;
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
        return saveLogWithEvent(user, create, USER_DEACTIVATED_EVENT_COMMENT, UserDetailsChangeEvent.USER_DEACTIVATED);
    }

    public HistoryLog addUserActivationEvent(User user,
                                             LocalDateTime create){
        return saveLogWithEvent(user, create, USER_ACTIVATED_EVENT_COMMENT, UserDetailsChangeEvent.USER_ACTIVATED);
    }

    public HistoryLog addChangeToECEvent(User user,
                                         LocalDateTime create){
        return saveLogWithEvent(user, create, CHANGE_TO_EC_EVENT_COMMENT, UserDetailsChangeEvent.USER_CHANGE_TO_EC);
    }

    public HistoryLog addChangeToB2BEvent(User user,
                                          LocalDateTime create){
        return saveLogWithEvent(user, create, CHANGE_TO_B2B_EVENT_COMMENT, UserDetailsChangeEvent.USER_CHANGE_TO_B2B);
    }

    public HistoryLog addChangeWorkTime(User user,
                                        LocalDateTime create,
                                        float oldWorkTime,
                                        float newWorkTime){

        var comment = String.format(CHANGE_WORK_TIME_EVENT_COMMENT_TEMPLATE, oldWorkTime, newWorkTime);
        return saveLogWithEvent(user, create, comment, UserDetailsChangeEvent.USER_CHANGE_WORK_TIME);
    }

    private HistoryLog saveLogWithEvent(User user,
                                        LocalDateTime create,
                                        String comment,
                                        UserDetailsChangeEvent event){
        var prevHistoryLog = historyLogRepository.findFirstByUserIdOrderByIdDesc(user.getId());
        var historyLog = new HistoryLog(user,create, 0, comment, prevHistoryLog, event);
        historyLogRepository.save(historyLog);
        return historyLog;
    }


}
