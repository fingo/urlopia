package info.fingo.urlopia.user;

import info.fingo.urlopia.api.v2.anonymizer.Anonymizer;
import info.fingo.urlopia.api.v2.automatic.vacation.days.AutomaticVacationDayService;
import info.fingo.urlopia.api.v2.exceptions.UnauthorizedException;
import info.fingo.urlopia.api.v2.history.DetailsChangeEventInput;
import info.fingo.urlopia.config.authentication.UserAuthoritiesProvider;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.history.UserDetailsChangeEvent;
import info.fingo.urlopia.team.AllUsersLeaderProvider;
import info.fingo.urlopia.team.Team;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private static final String NO_USER_WITH_ID_MESSAGE = "There is no user with id: {}";
    private static final String ADMIN_AUTHORITY_STRING = UserAuthoritiesProvider.ROLE_PREFIX + User.Role.ADMIN;
    private static final SimpleGrantedAuthority ADMIN_AUTHORITY = new SimpleGrantedAuthority(ADMIN_AUTHORITY_STRING);

    private final UserRepository userRepository;
    private final HistoryLogService historyLogService;
    private final AutomaticVacationDayService automaticVacationDayService;
    private final AllUsersLeaderProvider allUsersLeaderProvider;



    public List<UserExcerptProjection> get(Filter filter,
                                           Sort sort) {
        return userRepository.findAll(filter, sort, UserExcerptProjection.class);
    }

    public List<User> get(Filter filter) {
        return userRepository.findAll(filter);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User get(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> {
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

    public User getByPrincipal(String principal) {
        return userRepository
                .findFirstByPrincipalName(principal)
                .orElseThrow(() -> {
                    log.error("There is no user with principal: {}", Anonymizer.anonymizeMail(principal));
                    return NoSuchUserException.invalidEmail();
                });
    }
    public User getByFirstNameAndLastName(String firstName,
                                          String lastName) {
        return userRepository
                .findFirstByFirstNameAndLastName(firstName, lastName)
                .orElseThrow(() -> {
                    log.error("There is no user with firstName: {} lastName: {}", firstName, lastName);
                    return NoSuchUserException.invalidEmail();
                });
    }

    public User getAllUsersLeader() {
        return allUsersLeaderProvider.getAllUsersLeader();
    }

    // *** ACTIONS ***

    public void setWorkTime(Long userId,
                            String workTimeString) {

        var workTime = workTimeFrom(workTimeString, userId);
        userRepository.findById(userId)
                .ifPresentOrElse(
                        user -> updateUserWithEvent(user, workTime),
                        () -> {
                            log.error(NO_USER_WITH_ID_MESSAGE, userId);
                            throw NoSuchUserException.invalidId();
                        });
        var loggerInfo = "Work time of user with id: %d has been set to: %f".formatted(userId, workTime);
        log.info(loggerInfo);
    }

    private Float workTimeFrom(String workTime,
                               Long userId){
        var fullTimeInHours = 8f;
       return fullTimeInHours * Arrays.stream(workTime.split("/"))
                .map(Float::parseFloat)
                .reduce((a, b) -> a / b)
                .orElseThrow(() -> {
                    log.error("Something went wrong while setting work time for user with id: {} from: {}",
                            userId, workTime);
                    return new InvalidWorkTimeFormatException();
                });
    }

    private void updateUserWithEvent(User user,
                                     Float newWorkTime){
        var input = prepareWorkTimeChangeEvent(user, newWorkTime);
        historyLogService.addNewDetailsChangeEvent(input);
        automaticVacationDayService.resetPropositionFor(user.getId());
        user.setWorkTime(newWorkTime);
        userRepository.save(user);
    }

    private DetailsChangeEventInput prepareWorkTimeChangeEvent(User user,
                                                               Float newWorkTime){
        var oldWorkTime = user.getWorkTime();
        return new DetailsChangeEventInput(LocalDateTime.now(), user.getId(),
                UserDetailsChangeEvent.USER_CHANGE_WORK_TIME, formatWorkTime(oldWorkTime), formatWorkTime(newWorkTime));
    }

    private Float formatWorkTime(Float workTime){
        return workTime / 8.0f;
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

    public Long getCurrentUserId(){
        var principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Temporary fix while domain migration is on
        var split = principal.split(";");
        var firstName = split[0];
        var lastName = split.length > 1 ? split[1] : "";

        var user = userRepository.findFirstByFirstNameAndLastName(firstName, lastName);
        if (user.isPresent()){
            return user.get().getId();
        }

        throw UnauthorizedException.unauthorized();
    }

    public boolean isCurrentUserAdmin() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var authorities = authentication.getAuthorities();
        return authorities.contains(ADMIN_AUTHORITY);
    }

    private Set<User> getLeaders(User user) {
        return user.getTeams().stream()
                .map(Team::getLeader)
                .collect(Collectors.toUnmodifiableSet());
    }
}