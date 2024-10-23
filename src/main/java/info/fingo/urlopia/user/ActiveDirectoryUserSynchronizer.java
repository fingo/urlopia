package info.fingo.urlopia.user;

import info.fingo.urlopia.api.v2.automatic.vacation.days.AutomaticVacationDayService;
import info.fingo.urlopia.api.v2.history.DetailsChangeEventInput;
import info.fingo.urlopia.config.ad.ActiveDirectory;
import info.fingo.urlopia.config.ad.ActiveDirectoryObjectClass;
import info.fingo.urlopia.config.ad.ActiveDirectoryUtils;
import info.fingo.urlopia.config.ad.Attribute;
import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.history.UserDetailsChangeEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.naming.directory.SearchResult;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ad.configuration.enabled", havingValue = "true", matchIfMissing = true)
public class ActiveDirectoryUserSynchronizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveDirectoryUserSynchronizer.class);

    private final UserRepository userRepository;
    private final HistoryLogService historyLogService;
    private final ActiveDirectory activeDirectory;
    private final ActiveDirectoryUserMapperWrapper userMapper;
    private final AutomaticVacationDayService automaticVacationDayService;

    public void addNewUsers() {
        var dbUsers = userRepository.findAllAccountNames();
        LOGGER.info("Existing account names: {}", dbUsers.stream().sorted().collect(Collectors.joining(", ")));

        var adUsers = pickUsersFromActiveDirectory();
        LOGGER.info("AD account names: {}", adUsers.stream().map(user -> ActiveDirectoryUtils.pickAttribute(user, Attribute.ACCOUNT_NAME)).sorted().collect(Collectors.joining(", ")));

        pickUsersFromActiveDirectory().stream()
                .filter(user -> !dbUsers.contains(ActiveDirectoryUtils.pickAttribute(user, Attribute.ACCOUNT_NAME)))
                .map(userMapper::mapNewUser)
                .forEach(this::saveNewUser);
        LOGGER.info("Synchronisation succeed: find new users");
    }

    private void saveNewUser(User user) {
        try {
            userRepository.save(user);
            automaticVacationDayService.addForNewUser(user);
        } catch (Exception exception) {
            LOGGER.error("Exception when saving a new user", exception);
        }
    }

    public void deactivateDeletedUsers() {
        var existingAccountNames = pickUsersFromActiveDirectory().stream()
                .map(user -> ActiveDirectoryUtils.pickAttribute(user, Attribute.ACCOUNT_NAME))
                .toList();

        userRepository.findAll().stream()
                .filter(user -> !existingAccountNames.contains(user.getAccountName()))
                .forEach(this::deactivateUser);
        LOGGER.info("Synchronisation succeed: deactivate deleted users");
    }

    public void deactivateDisabledUsers() {
        var disabledAccountNames = pickDisabledUsersFromActiveDirectory().stream()
                .map(user -> ActiveDirectoryUtils.pickAttribute(user, Attribute.ACCOUNT_NAME))
                .toList();

        userRepository.findAll().stream()
                .filter(user -> disabledAccountNames.contains(user.getAccountName()))
                .forEach(this::deactivateUser);
        LOGGER.info("Synchronisation succeed: deactivate disabled users");
    }

    private void deactivateUser(User user){
        var wasActive = user.isActive();
        user.deactivate();
        if (wasActive){
            var input = new DetailsChangeEventInput(LocalDateTime.now(), user.getId(), UserDetailsChangeEvent.USER_DEACTIVATED);
            historyLogService.addNewDetailsChangeEvent(input);
        }
        userRepository.save(user);
    }

    public void synchronizeFull() {
        var usersToSynchronize = pickUsersFromActiveDirectory().stream();
        synchronize(usersToSynchronize);
        LOGGER.info("Synchronisation succeed: all users");
    }

    private void synchronize(Stream<SearchResult> adUsers) {
        adUsers.forEach(adUser -> {
            var accountName = ActiveDirectoryUtils.pickAttribute(adUser, Attribute.ACCOUNT_NAME);
            userRepository
                    .findFirstByAccountName(accountName)
                    .map(user -> userMapper.updateUser(adUser, user))
                    .ifPresent(userRepository::save);
        });
    }

    private List<SearchResult> pickUsersFromActiveDirectory() {
        return activeDirectory.newSearch()
                .objectClass(ActiveDirectoryObjectClass.PERSON)
                .search();
    }

    private List<SearchResult> pickDisabledUsersFromActiveDirectory() {
        return activeDirectory.newSearch()
                .objectClass(ActiveDirectoryObjectClass.PERSON)
                .isDisabled()
                .search();
    }

}
