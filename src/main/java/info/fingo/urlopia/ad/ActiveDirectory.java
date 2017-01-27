package info.fingo.urlopia.ad;

import info.fingo.urlopia.authentication.LDAPConnectionService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides communication with Active Directory
 *
 * @author Tomasz Urbas
 */
@Service
@Scope("prototype")
public class ActiveDirectory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveDirectory.class);

    @Value("${ad.team.identifier}")
    private String privateTeamIdentifier;
    public static String TEAM_IDENTIFIER;

    @Value("${ad.user.container}")
    private String privateUsersContainer;
    public static String USERS_CONTAINER;

    @Value("${ad.user.b2b.group}")
    private String privateB2bEmployeesGroup;
    private static String B2B_EMPLOYEES_GROUP;

    @Value("${ad.user.ec.group}")
    private String privateEcEmployeesGroup;
    private static String EC_EMPLOYEES_GROUP;

    @Value("${ad.team.urlopia.group}")
    private String privateUrlopiaTeamGroup;
    private static String URLOPIA_TEAM_GROUP;

    @Value("${ad.team.main.group}")
    private String privateMainTeamGroup;
    private static String MAIN_TEAM_GROUP;

    @Value("${ad.team.leaders.group}")
    private String privateLeadersGroup;
    private static String LEADERS_GROUP;

    @Value("${ad.user.masterLeader}")
    private String privateMasterLeaderMail;
    private static String MASTER_LEADER_MAIL;

    @Autowired
    private LDAPConnectionService ldapConnectionService;

    @PostConstruct
    public void init(){
        TEAM_IDENTIFIER = privateTeamIdentifier;
        USERS_CONTAINER = privateUsersContainer;
        B2B_EMPLOYEES_GROUP = privateB2bEmployeesGroup;
        EC_EMPLOYEES_GROUP = privateEcEmployeesGroup;
        URLOPIA_TEAM_GROUP = privateUrlopiaTeamGroup;
        MAIN_TEAM_GROUP = privateMainTeamGroup;
        LEADERS_GROUP = privateLeadersGroup;
        MASTER_LEADER_MAIL = privateMasterLeaderMail;
    }

    private List<SearchResult> search(String filter) {
        List<SearchResult> result = new LinkedList<>();

        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        // connecting to AD and getting data
        DirContext ad = null;
        try {
            ad = ldapConnectionService.getContext();
            result = Collections.list(ad.search(USERS_CONTAINER, filter, controls));
        } catch (NamingException e) {
            LOGGER.error("Exception when trying to search in Active Directory", e);
        } finally {
            try {
                if (ad != null) {
                    ad.close();
                }
            } catch (NamingException e) {
                LOGGER.error("Exception when trying to close the LDAP connection", e);
            }
        }

        return result;
    }

    private String getAttribute(Attributes attributes, String attrName) {
        String value = "";
        Attribute attr = attributes.get(attrName);

        if (attr != null) {
            value = attributes.get(attrName).toString();
            value = value.substring(value.indexOf(':') + 2);
        }

        return value;
    }

    private boolean isTeam(String group) {
        return group.contains(StringEscapeUtils.unescapeJava(TEAM_IDENTIFIER)) && !group.equals(MAIN_TEAM_GROUP);
    }

    private boolean isLeader(String leaderOf) {
        return !leaderOf.isEmpty();
    }

    private boolean isB2B(String memberOf) {
        return memberOf.contains(B2B_EMPLOYEES_GROUP);
    }

    private boolean isEC(String memberOf) {
        return memberOf.contains(EC_EMPLOYEES_GROUP);
    }

    private boolean isUrlopiaTeam(String memberOf) {
        return memberOf.contains(URLOPIA_TEAM_GROUP);
    }

    private LocalUser getLeaderOfTeam(String team) {
        LocalUser user = new LocalUser();

        String filter = "(&(objectClass=Person)(managedObjects=" + team + "))";
        List<SearchResult> results = search(filter);

        if (!results.isEmpty()) {
            user = createLocalUser(results.get(0), true);
        }

        return user;
    }

    private List<LocalTeam> getTeams(String memberOf) {
        String[] groups = memberOf.split(", (?=CN=)");

        return Arrays.stream(groups)    // NOSONAR : SONARJAVA-1478
                .filter(this::isTeam)
                .map(team -> new LocalTeam(team, getLeaderOfTeam(team)))
                .collect(Collectors.toList());
    }

    private LocalUser createLocalUser(SearchResult result, boolean leader) {
        LocalUser localUser = new LocalUser();
        Attributes attributes = result.getAttributes();

        localUser.setMail(getAttribute(attributes, "mail"));
        localUser.setPrincipalName(getAttribute(attributes, "userPrincipalName"));
        localUser.setName(getAttribute(attributes, "givenname"));
        localUser.setSurname(getAttribute(attributes, "sn"));
        localUser.setLeader(isLeader(getAttribute(attributes, "managedObjects")));
        localUser.setB2B(isB2B(getAttribute(attributes, "memberOf")));
        localUser.setEC(isEC(getAttribute(attributes, "memberOf")));
        localUser.setUrlopiaTeam(isUrlopiaTeam(getAttribute(attributes, "memberOf")));

        if (!leader) {
            localUser.setTeams(getTeams(getAttribute(attributes, "memberOf")));
            localUser.getTeams().stream()
                    .filter(team -> !MASTER_LEADER_MAIL.equals(localUser.getMail())
                            && team.getLeader().getMail().equals(localUser.getMail()))
                    .forEach(team -> getUser(MASTER_LEADER_MAIL).ifPresent(team::setLeader));
        }

        return localUser;
    }

    private LocalUser createLocalUser(SearchResult result) {
        return createLocalUser(result, false);
    }

    private LocalTeam createLocalTeam(SearchResult result) {    // NOSONAR : SONARJAVA-583
        Attributes attributes = result.getAttributes();
        String name = getAttribute(attributes, "distinguishedName");

        LocalUser leader = getLeaderOfTeam(name);
        return new LocalTeam(name, leader);
    }

    // TODO: Think about keeping mail and principalName in the database
    public Optional<LocalUser> getUser(String principalName) {
        Optional<LocalUser> user = Optional.empty();

        String filter = "(&(objectClass=Person)(|(userPrincipalName=" + principalName + ")(mail=" + principalName + ")))";
        List<SearchResult> results = search(filter);

        if (!results.isEmpty()) {
            user = Optional.ofNullable(createLocalUser(results.get(0)));
        }

        return user;
    }

    public List<LocalUser> getUsers() {
        String filter = "(&(objectClass=Person))";
        List<SearchResult> results = search(filter);

        return results.parallelStream().map(this::createLocalUser)
                .collect(Collectors.toList());
    }

    public List<LocalUser> getUsersFromTeam(LocalTeam team) {
        String filter = "(&(objectClass=Person)(memberOf=" + team.getFullName() + "))";
        List<SearchResult> results = search(filter);

        return results.parallelStream()
                .map(this::createLocalUser)
                .collect(Collectors.toList());
    }

    public List<LocalTeam> getTeams() {
        String filter = "(&(objectClass=Group))";
        List<SearchResult> results = search(filter);

        return results.parallelStream()
                .map(this::createLocalTeam)
                .filter(team -> isTeam(team.getFullName()))
                .collect(Collectors.toList());
    }
}