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

    @Value("${mails.master.leader}")
    private String masterLeaderMail;

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
        user.setLeader(this.isLeader(searchResult));
        user.setB2b(this.isB2B(searchResult));
        user.setEc(this.isEC(searchResult));
        return user;
    }

    private boolean isLeader(SearchResult searchResult) {
        String leaderOf = ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.MANAGED_OBJECTS);
        String mail = ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.MAIL);
        return !leaderOf.isEmpty() || masterLeaderMail.equals(mail);
    }

    private boolean isB2B(SearchResult searchResult) {
        String memberOf = ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.MEMBER_OF);
        return memberOf.contains(b2bGroup);
    }

    private boolean isEC(SearchResult searchResult) {
        String memberOf = ActiveDirectoryUtils.pickAttribute(searchResult, Attribute.MEMBER_OF);
        return memberOf.contains(ecGroup);
    }
}
