package info.fingo.urlopia.user;

import info.fingo.urlopia.ad.ActiveDirectoryUtils;
import info.fingo.urlopia.ad.Attribute;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.directory.SearchResult;

@Component
class ActiveDirectoryUserMapper {

    @Value("${ad.groups.b2b}")
    private String b2bGroup;

    @Value("${ad.groups.ec}")
    private String ecGroup;

    User mapToUser(SearchResult searchResult) {
        User newUser = new User();
        return this.mapToUser(searchResult, newUser);
    }

    User mapToUser(SearchResult searchResult, User user) {
        user.setPrincipalName(ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.PRINCIPAL_NAME));
        user.setAdName(ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.DISTINGUISHED_NAME));
        user.setMail(ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.MAIL));
        user.setFirstName(ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.FIRST_NAME));
        user.setLastName(ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.LAST_NAME));
        user.setLeader(this.isLeader(ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.MANAGED_OBJECTS)));
        user.setB2b(this.isB2B(ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.MEMBER_OF)));
        user.setEc(this.isEC(ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.MEMBER_OF)));
        return user;
    }

    private boolean isLeader(String leaderOf) {
        return !leaderOf.isEmpty();
    }

    private boolean isB2B(String memberOf) {
        return memberOf.contains(b2bGroup);
    }

    private boolean isEC(String memberOf) {
        return memberOf.contains(ecGroup);
    }
}
