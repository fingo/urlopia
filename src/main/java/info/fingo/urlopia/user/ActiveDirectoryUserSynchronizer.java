package info.fingo.urlopia.user;

import info.fingo.urlopia.config.ad.ActiveDirectory;
import info.fingo.urlopia.config.ad.ActiveDirectoryObjectClass;
import info.fingo.urlopia.config.ad.ActiveDirectoryUtils;
import info.fingo.urlopia.config.ad.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public ActiveDirectoryUserSynchronizer(UserRepository userRepository,
                                           ActiveDirectory activeDirectory,
                                           ActiveDirectoryUserMapper userMapper) {
        this.userRepository = userRepository;
        this.activeDirectory = activeDirectory;
        this.userMapper = userMapper;
        this.lastModificationsCheck = LocalDateTime.now();
    }

    public void addNewUsers() {
        var dbUsers = userRepository.findAllPrincipalNames();
        pickUsersFromActiveDirectory().stream()
                .filter(user ->
                        !dbUsers.contains(ActiveDirectoryUtils.pickAttribute(user, Attribute.PRINCIPAL_NAME)))
                .map(userMapper::mapToUser)
                .forEach(userRepository::save);
        LOGGER.info("Synchronisation succeed: find new users");
    }

    public void deactivateDeletedUsers() {
        var adUsers = pickUsersFromActiveDirectory().stream()
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
        var usersToSynchronize = pickUsersFromActiveDirectory().stream()
                .filter(user -> {
                    var changed = ActiveDirectoryUtils.pickAttribute(user, Attribute.CHANGED_TIME);
                    var changedTime = ActiveDirectoryUtils.convertToLocalDateTime(changed);
                    return changedTime.isAfter(lastModificationsCheck);
                });
        synchronize(usersToSynchronize);
        lastModificationsCheck = LocalDateTime.now();
        LOGGER.info("Synchronisation succeed: last modified users");
    }

    public void synchronizeFull() {
        var usersToSynchronize = pickUsersFromActiveDirectory().stream();
        synchronize(usersToSynchronize);
        LOGGER.info("Synchronisation succeed: all users");
    }

    private void synchronize(Stream<SearchResult> adUsers) {
        adUsers.forEach(adUser -> {
            var principalName = ActiveDirectoryUtils.pickAttribute(adUser, Attribute.PRINCIPAL_NAME);
            userRepository
                    .findFirstByPrincipalName(principalName)
                    .map(user -> userMapper.mapToUser(adUser, user))
                    .ifPresent(userRepository::save);
        });
    }

    private List<SearchResult> pickUsersFromActiveDirectory() {
        return activeDirectory.newSearch()
                .objectClass(ActiveDirectoryObjectClass.Person)
                .memberOf(usersGroup)
                .search();
    }

}
