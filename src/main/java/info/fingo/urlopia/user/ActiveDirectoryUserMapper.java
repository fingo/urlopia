package info.fingo.urlopia.user;

import info.fingo.urlopia.api.v2.history.DetailsChangeEventInput;
import info.fingo.urlopia.config.ad.ActiveDirectoryUtils;
import info.fingo.urlopia.config.ad.Attribute;
import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.history.UserDetailsChangeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.naming.directory.SearchResult;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
class ActiveDirectoryUserMapper {

    @Value("${ad.groups.b2b}")
    private String b2bGroup;

    @Value("${ad.groups.ec}")
    private String ecGroup;

    private final HistoryLogService historyLogService;

    User mapNewUser(SearchResult searchResult) {
        return mapToUser(searchResult, new User());
    }

    User updateUser(SearchResult searchResult,
                   User user) {
        var wasB2BBefore = user.getB2b();
        var wasECBefore = user.getEc();
        var wasActiveBefore = user.getActive();

        var updatedUser = mapToUser(searchResult, user);
        addNeededEventsAfterUpdate(updatedUser, wasB2BBefore, wasECBefore, wasActiveBefore);

        return user;
    }

    private void addNeededEventsAfterUpdate(User updatedUser,
                                            boolean wasB2BBefore,
                                            boolean wasECBefore,
                                            boolean wasActiveBefore){
        addECEventIfNeeded(updatedUser, wasECBefore);
        addB2BEventIfNeeded(updatedUser, wasB2BBefore);
        addActivationEventIfNeeded(updatedUser, wasActiveBefore);
        addDisActivationEventIfNeeded(updatedUser, wasActiveBefore);
    }

    private void addB2BEventIfNeeded(User updatedUser,
                                    boolean wasB2BBefore){
        var becomeB2B = !wasB2BBefore && updatedUser.getB2b();
        if (becomeB2B){
            saveEvent(UserDetailsChangeEvent.USER_CHANGE_TO_B2B, updatedUser);
        }
    }

    private void addECEventIfNeeded(User updatedUser,
                                    boolean wasECBefore){
        var becomeEC = !wasECBefore && updatedUser.getEc();
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

    public void addInitUserEvents(User user){
        if (user.isActive()){
           saveEvent(UserDetailsChangeEvent.USER_ACTIVATED, user);
        }
        if (user.getEc()){
            saveEvent(UserDetailsChangeEvent.USER_CHANGE_TO_EC, user);
        }
        if (user.getB2b()){
            saveEvent(UserDetailsChangeEvent.USER_CHANGE_TO_B2B, user);
        }
    }

    private void saveEvent(UserDetailsChangeEvent event,
                           User user){
        var input = new DetailsChangeEventInput(LocalDateTime.now(), user.getId(), event);
        historyLogService.addNewDetailsChangeEvent(input);
    }

    private User mapToUser(SearchResult searchResult,
                           User user) {
        user.setPrincipalName(
                ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.PRINCIPAL_NAME));
        user.setAdName(
                ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.DISTINGUISHED_NAME));
        user.setMail(
                ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.MAIL));
        user.setFirstName(
                ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.FIRST_NAME));
        user.setLastName(
                ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.LAST_NAME));
        user.setLeader(
                isLeader(searchResult));
        user.setB2b(
                isB2B(searchResult));
        user.setEc(
                isEC(searchResult));
        user.setActive(
                !ActiveDirectoryUtils.isDisabled(searchResult));

        return user;
    }

    private boolean isLeader(SearchResult searchResult) {
        var leaderOf = ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.MANAGED_OBJECTS);
        return !leaderOf.isEmpty();
    }

    private boolean isB2B(SearchResult searchResult) {
        var memberOf = ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.MEMBER_OF);
        return memberOf.contains(b2bGroup);
    }

    private boolean isEC(SearchResult searchResult) {
        var memberOf = ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.MEMBER_OF);
        return memberOf.contains(ecGroup);
    }
}
