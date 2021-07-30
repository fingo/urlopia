package info.fingo.urlopia.user;

import info.fingo.urlopia.config.ad.ActiveDirectory;
import info.fingo.urlopia.config.ad.ActiveDirectoryObjectClass;
import info.fingo.urlopia.config.ad.ActiveDirectoryUtils;
import info.fingo.urlopia.config.ad.Attribute;
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
public class ActiveDirectoryUserSynchronizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveDirectoryUserSynchronizer.class);

    @Value("${ad.groups.users}")
    private String usersGroup;

    private final UserRepository userRepository;

    private final ActiveDirectory activeDirectory;

    private final ActiveDirectoryUserMapper userMapper;

    private LocalDateTime lastModificationsCheck;

    @Autowired
    public ActiveDirectoryUserSynchronizer(UserRepository userRepository, ActiveDirectory activeDirectory, ActiveDirectoryUserMapper userMapper) {
        this.userRepository = userRepository;
        this.activeDirectory = activeDirectory;
        this.userMapper = userMapper;
        this.lastModificationsCheck = LocalDateTime.now();
    }

    public void addNewUsers() {
        List<String> dbUsers = userRepository.findAllPrincipalNames();
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
        userRepository.findAll().stream()
                .filter(user -> !adUsers.contains(user.getPrincipalName()))
                .forEach(user -> {
                    user.deactivate();
                    userRepository.save(user);
                });
        LOGGER.info("Synchronisation succeed: deactivate deleted users");
    }

    public void synchronizeIncremental() {
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

    public void synchronizeFull() {
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
                .objectClass(ActiveDirectoryObjectClass.Person)
                .memberOf(usersGroup)
                .search();
    }

}
