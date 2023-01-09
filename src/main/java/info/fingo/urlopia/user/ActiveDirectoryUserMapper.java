package info.fingo.urlopia.user;

import info.fingo.urlopia.config.ad.ActiveDirectoryUtils;
import info.fingo.urlopia.config.ad.Attribute;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.naming.directory.SearchResult;

@Component
@ConditionalOnProperty(name = "ad.configuration.enabled", havingValue = "true", matchIfMissing = true)
public class ActiveDirectoryUserMapper {
    @Value("${ad.groups.b2b}")
    private String b2bGroup;

    @Value("${ad.groups.ec}")
    private String ecGroup;

    @Value("${ad.groups.admin}")
    private String adminGroup;

    public User mapToUser(SearchResult searchResult,
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
        user.setAdmin(
                isAdmin(searchResult));

        return user;
    }

    private boolean isLeader(SearchResult searchResult) {
        var leaderOf = ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.MANAGED_OBJECTS);
        return !leaderOf.isEmpty();
    }

    private boolean isInGroup(SearchResult searchResult,
                              String groupName){
        var memberOf = ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.MEMBER_OF);
        return memberOf.contains(groupName);
    }

    private boolean isB2B(SearchResult searchResult) {
        return isInGroup(searchResult, b2bGroup);
    }

    private boolean isEC(SearchResult searchResult) {
        return isInGroup(searchResult, ecGroup);
    }

    private boolean isAdmin(SearchResult searchResult) {
        return isInGroup(searchResult, adminGroup);
    }
}
