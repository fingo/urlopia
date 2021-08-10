package info.fingo.urlopia.user;

import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.team.Team;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserExcerptProjection> get(Filter filter,
                                           Sort sort) {
        return userRepository.findAll(filter, sort, UserExcerptProjection.class);
    }

    public User get(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> NoSuchUserException.invalidId(userId));
    }

    public User get(String userMail) {
        return userRepository
                .findFirstByMail(userMail)
                .orElseThrow(() -> NoSuchUserException.invalidEmail(userMail));
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
                            throw NoSuchUserException.invalidId(userId);
                        });
        var loggerInfo = "Language of user with id: %d has been set to: %s".formatted(userId, language);
        log.info(loggerInfo);
    }

    boolean isEC(Long userId) {
        return userRepository
                .findById(userId)
                .map(User::getEc)
                .orElseThrow(() -> NoSuchUserException.invalidId(userId));
    }


    void setWorkTime(Long userId,
                     String workTimeString) {
        var fullTimeInHours = 8f;
        var workTime = fullTimeInHours * Arrays.stream(workTimeString.split("/"))
                .map(Float::parseFloat)
                .reduce((a, b) -> a / b)
                .orElseThrow(RuntimeException::new);

        userRepository
                .findById(userId)
                .ifPresentOrElse(
                        user -> {
                            user.setWorkTime(workTime);
                            userRepository.save(user);
                        },
                        () -> {
                            throw NoSuchUserException.invalidId(userId);
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
                .orElseThrow(() -> NoSuchUserException.invalidId(userId));
    }

    private Set<User> getLeaders(User user) {
        return user.getTeams().stream()
                .map(Team::getLeader)
                .collect(Collectors.toUnmodifiableSet());
    }

}