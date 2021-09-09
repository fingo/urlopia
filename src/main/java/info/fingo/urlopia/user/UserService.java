package info.fingo.urlopia.user;

import info.fingo.urlopia.api.v2.anonymizer.Anonymizer;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.team.Team;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private static final String NO_USER_WITH_ID_MESSAGE = "There is no user with id: {}";

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserExcerptProjection> get(Filter filter,
                                           Sort sort) {
        return userRepository.findAll(filter, sort, UserExcerptProjection.class);
    }

    public List<User> get(Filter filter) {
        return userRepository.findAll(filter);
    }

    public User get(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() ->{
                    log.error(NO_USER_WITH_ID_MESSAGE, userId);
                    return NoSuchUserException.invalidId();
                });
    }

    public User get(String userMail) {
        return userRepository
                .findFirstByMail(userMail)
                .orElseThrow(() -> {
                    log.error("There is no user with email: {}", Anonymizer.anonymizeMail(userMail));
                    return NoSuchUserException.invalidEmail();
                });
    }

    // *** ACTIONS ***

    void setLanguage(Long userId,
                     String language) {
        userRepository
                .findById(userId)
                .ifPresentOrElse(
                        user -> {
                            user.setLang(language);
                            userRepository.save(user);
                        },
                        () -> {
                            log.error(NO_USER_WITH_ID_MESSAGE, userId);
                            throw NoSuchUserException.invalidId();
                        });
        var loggerInfo = "Language of user with id: %d has been set to: %s".formatted(userId, language);
        log.info(loggerInfo);
    }

    boolean isEC(Long userId) {
        return userRepository
                .findById(userId)
                .map(User::getEc)
                .orElseThrow(() ->
                {
                    log.error(NO_USER_WITH_ID_MESSAGE, userId);
                    return NoSuchUserException.invalidId();
                });
    }


    public void setWorkTime(Long userId,
                     String workTimeString) {
        var fullTimeInHours = 8f;
        var workTime = fullTimeInHours * Arrays.stream(workTimeString.split("/"))
                .map(Float::parseFloat)
                .reduce((a, b) -> a / b)
                .orElseThrow(() -> {
                    log.error("Something went wrong while setting work time for user with id: {} from: {}",
                            userId, workTimeString);
                    return new RuntimeException();
                });

        userRepository
                .findById(userId)
                .ifPresentOrElse(
                        user -> {
                            user.setWorkTime(workTime);
                            userRepository.save(user);
                        },
                        () -> {
                            log.error(NO_USER_WITH_ID_MESSAGE, userId);
                            throw NoSuchUserException.invalidId();
                        });
        var loggerInfo = "Work time of user with id: %d has been set to: %f".formatted(userId, workTime);
        log.info(loggerInfo);
    }

    public Set<User> getAdmins() {
        var admins = userRepository.findAdmins();
        return Set.copyOf(admins);
    }

    public Set<User> getLeaders(Long userId) {
        return userRepository
                .findById(userId)
                .map(this::getLeaders)
                .map(Set::copyOf)
                .orElseThrow(() -> {
                    log.error(NO_USER_WITH_ID_MESSAGE, userId);
                    return NoSuchUserException.invalidId();
                });
    }

    private Set<User> getLeaders(User user) {
        return user.getTeams().stream()
                .map(Team::getLeader)
                .collect(Collectors.toUnmodifiableSet());
    }

}