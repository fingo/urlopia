package info.fingo.urlopia.user;

import info.fingo.urlopia.ad.ActiveDirectoryUtils;
import info.fingo.urlopia.ad.ActiveDirectoryX;
import info.fingo.urlopia.ad.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.directory.SearchResult;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class UserSynchronizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserSynchronizer.class);

    @Value("${ad.groups.users}")
    private String usersGroup;

    private final UserRepository userRepository;

    private final ActiveDirectoryX activeDirectory;

    private final ActiveDirectoryUserMapper userMapper;

    private LocalDateTime lastModificationsCheck;

    @Autowired
    public UserSynchronizer(UserRepository userRepository, ActiveDirectoryX activeDirectory, ActiveDirectoryUserMapper userMapper) {
        this.userRepository = userRepository;
        this.activeDirectory = activeDirectory;
        this.userMapper = userMapper;
        this.lastModificationsCheck = LocalDateTime.now();
    }

    public void findNewUsers() {
        List<String> dbUsers = this.pickUsersFromDatabase().stream()
                .map(User::getPrincipalName)
                .collect(Collectors.toList());
        this.pickUsersFromActiveDirectory().stream()
                .filter(user -> !dbUsers.contains(ActiveDirectoryUtils.pickAttribute(user, Attribute.PRINCIPAL_NAME)))
                .map(userMapper::mapToUser)
                .forEach(userRepository::save);
        LOGGER.info("Synchronisation succeed: find new users");
    }

    public void deactivateDeletedUsers() {
        List<String> adUsers = this.pickUsersFromActiveDirectory().stream()
                .map(user -> ActiveDirectoryUtils.pickAttribute(user, Attribute.PRINCIPAL_NAME))
                .collect(Collectors.toList());
        this.pickUsersFromDatabase().stream()
                .filter(user -> !adUsers.contains(user.getPrincipalName()))
                .forEach(User::deactivate);
        LOGGER.info("Synchronisation succeed: deactivate deleted users");
    }

    public void checkModifications() {
        LocalDateTime checkTime = LocalDateTime.now();
        Stream<SearchResult> usersToSynchronize = this.pickUsersFromActiveDirectory().stream()
                .filter(user -> {
                    String changed = ActiveDirectoryUtils.pickAttribute(user, Attribute.CHANGED_TIME);
                    LocalDateTime changedTime = ActiveDirectoryUtils.convertToLocalDateTime(changed);
                    return changedTime.isAfter(this.lastModificationsCheck);
                });
        this.synchronize(usersToSynchronize);
        this.lastModificationsCheck = checkTime;
        LOGGER.info("Synchronisation succeed: last modified users");
    }

    public void fullSynchronize() {
        Stream<SearchResult> usersToSynchronize = this.pickUsersFromActiveDirectory().stream();
        this.synchronize(usersToSynchronize);
        LOGGER.info("Synchronisation succeed: all users");
    }

    private void synchronize(Stream<SearchResult> adUsers) {
        adUsers.forEach(adUser -> {
            String principalName = ActiveDirectoryUtils.pickAttribute(adUser, Attribute.PRINCIPAL_NAME);
            User user = userRepository.findFirstByPrincipalName(principalName);
            if (user != null) {
                user = userMapper.mapToUser(adUser, user);
                userRepository.save(user);
            }
        });
    }

    private List<SearchResult> pickUsersFromActiveDirectory() {
        return activeDirectory.newSearch()
                .objectClass(ActiveDirectoryX.ObjectClass.Person)
                .memberOf(usersGroup)
                .search();
    }

    private List<User> pickUsersFromDatabase() {
        return userRepository.findAll();
    }

}
