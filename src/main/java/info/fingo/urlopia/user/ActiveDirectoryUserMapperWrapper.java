package info.fingo.urlopia.user;

import info.fingo.urlopia.api.v2.history.DetailsChangeEventInput;
import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.history.UserDetailsChangeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import javax.naming.directory.SearchResult;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ad.configuration.enabled", havingValue = "true", matchIfMissing = true)
class ActiveDirectoryUserMapperWrapper {

    private final HistoryLogService historyLogService;
    private final ActiveDirectoryUserMapper activeDirectoryUserMapper;

    User mapNewUser(SearchResult searchResult) {
        return activeDirectoryUserMapper.mapToUser(searchResult, new User());
    }

    User updateUser(SearchResult searchResult,
                    User user) {
        var wasB2BBefore = user.getB2b();
        var wasECBefore = user.getEc();
        var wasActiveBefore = user.isActive();

        var updatedUser = activeDirectoryUserMapper.mapToUser(searchResult, user);
        addNeededEventsAfterUpdate(updatedUser, wasB2BBefore, wasECBefore, wasActiveBefore);

        return user;
    }

    private void addNeededEventsAfterUpdate(User updatedUser,
                                            boolean wasB2BBefore,
                                            boolean wasECBefore,
                                            boolean wasActiveBefore){
        addECEventIfNeeded(updatedUser, wasB2BBefore, wasECBefore);
        addB2BEventIfNeeded(updatedUser, wasB2BBefore, wasECBefore);
        addActivationEventIfNeeded(updatedUser, wasActiveBefore);
        addDisActivationEventIfNeeded(updatedUser, wasActiveBefore);
    }

    private void addB2BEventIfNeeded(User updatedUser,
                                    boolean wasB2BBefore,
                                    boolean wasECBefore){
        var becomeB2B = !wasB2BBefore && updatedUser.getB2b() && wasECBefore;
        if (becomeB2B){
            saveEvent(UserDetailsChangeEvent.USER_CHANGE_TO_B2B, updatedUser);
        }
    }

    private void addECEventIfNeeded(User updatedUser,
                                    boolean wasB2BBefore,
                                    boolean wasECBefore){
        var becomeEC = !wasECBefore && updatedUser.getEc() && wasB2BBefore;
        if (becomeEC){
            saveEvent(UserDetailsChangeEvent.USER_CHANGE_TO_EC, updatedUser);
        }
    }

    private void addActivationEventIfNeeded(User updatedUser,
                                            boolean wasActiveBefore){
        var becomeActive = !wasActiveBefore && updatedUser.isActive();
        if (becomeActive){
            saveEvent(UserDetailsChangeEvent.USER_ACTIVATED, updatedUser);
        }
    }

    private void addDisActivationEventIfNeeded(User updatedUser,
                                                boolean wasActiveBefore){
        var becomeDisActive = wasActiveBefore && !updatedUser.isActive();
        if (becomeDisActive){
            saveEvent(UserDetailsChangeEvent.USER_DEACTIVATED, updatedUser);
        }
    }

    private void saveEvent(UserDetailsChangeEvent event,
                           User user){
        var input = new DetailsChangeEventInput(LocalDateTime.now(), user.getId(), event);
        historyLogService.addNewDetailsChangeEvent(input);
    }

}
