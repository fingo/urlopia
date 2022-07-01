package info.fingo.urlopia.api.v2.presence;

import info.fingo.urlopia.UrlopiaApplication;
import info.fingo.urlopia.api.v2.preferences.UserPreferencesService;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.config.persistance.filter.Operator;
import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PresenceConfirmationService {
    public static final String USER_ID_FROM_PRESENCE = "presenceConfirmationId.userId";
    public static final String DATE_FROM_PRESENCE = "presenceConfirmationId.date";

    private final PresenceConfirmationRepository presenceConfirmationRepository;
    private final RequestService requestService;
    private final HolidayService holidayService;
    private final UserService userService;
    private final UserPreferencesService userPreferencesService;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(UrlopiaApplication.DATE_FORMAT);

    public List<PresenceConfirmation> getPresenceConfirmations(Long authenticatedUserId, String[] filters) {
        var authenticatedUser = userService.get(authenticatedUserId);
        var convertedFilters = convertFilters(filters);
        var filter = Filter.from(convertedFilters);

        if (!authenticatedUser.isAdmin()) {
            filter = filter.toBuilder()
                    .and(USER_ID_FROM_PRESENCE, Operator.EQUAL, authenticatedUserId.toString())
                    .build();
        }

        return getAll(filter);
    }
    public List<PresenceConfirmation> getAll(Filter filter) {
        return presenceConfirmationRepository.findAll(filter);
    }

    public Optional<PresenceConfirmation> getPresenceConfirmation(Long userId, LocalDate date) {
        var filter = Filter.newBuilder()
                .and(USER_ID_FROM_PRESENCE, Operator.EQUAL, String.valueOf(userId))
                .and(DATE_FROM_PRESENCE, Operator.EQUAL, String.valueOf(date))
                .build();
        var output = presenceConfirmationRepository.findAll(filter);
        return output.isEmpty() ? Optional.empty() : Optional.of(output.get(0));
    }

    public List<PresenceConfirmation> getByUserAndDate(Long userId, LocalDate date) {
        var formattedDate = dateTimeFormatter.format(date);
        var filter = Filter.newBuilder()
                .and(USER_ID_FROM_PRESENCE, Operator.EQUAL, String.valueOf(userId))
                .and(DATE_FROM_PRESENCE, Operator.EQUAL, formattedDate)
                .build();
        return this.presenceConfirmationRepository.findAll(filter);
    }

    public boolean hasPresenceByUserAndDateInterval(LocalDate startDate,
                                                    LocalDate endDate,
                                                    Long userId) {
        var formattedStartDate = dateTimeFormatter.format(startDate);
        var formattedEndDate = dateTimeFormatter.format(endDate);
        var filter = Filter.newBuilder()
                .and(USER_ID_FROM_PRESENCE, Operator.EQUAL, String.valueOf(userId))
                .and(DATE_FROM_PRESENCE, Operator.GREATER_OR_EQUAL, formattedStartDate)
                .and(DATE_FROM_PRESENCE, Operator.LESS_OR_EQUAL, formattedEndDate)
                .build();
        var presenceConfirmations = presenceConfirmationRepository.findAll(filter);
        return !presenceConfirmations.isEmpty();
    }

    private String[] convertFilters(String[] filters) {
        return Arrays.stream(filters)
                .map(filter -> filter.replace("userId", USER_ID_FROM_PRESENCE))
                .map(filter -> filter.replace("date", DATE_FROM_PRESENCE))
                .toArray(String[]::new);
    }

    public PresenceConfirmation confirmPresence(Long authenticatedUserId, PresenceConfirmationInputOutput dto) {
        return confirmPresence(userService.get(authenticatedUserId), dto);
    }

    public PresenceConfirmation confirmPresence(User authenticatedUser, PresenceConfirmationInputOutput dto) {
        var confirmationUserId = dto.getUserId();
        checkIfUserIsAuthorizedToConfirmPresence(authenticatedUser, confirmationUserId);

        var isConfirmingOwnPresence = authenticatedUser.getId().equals(confirmationUserId);
        var confirmationUser = isConfirmingOwnPresence ? authenticatedUser : userService.get(confirmationUserId);
        checkIfConfirmationIsViable(authenticatedUser, confirmationUser, dto.getDate());

        return presenceConfirmationRepository.save(presenceConfirmationFromDto(dto, confirmationUser));
    }

    private PresenceConfirmation presenceConfirmationFromDto(PresenceConfirmationInputOutput dto, User user) {
        var date = dto.getDate();
        var startTime = dto.getStartTime();
        var endTime = dto.getEndTime();
        return new PresenceConfirmation(user, date, startTime, endTime);
    }

    private void checkIfUserIsAuthorizedToConfirmPresence(User authenticatedUser, Long confirmationUserId) {
        var isConfirmingOwnPresence = authenticatedUser.getId().equals(confirmationUserId);
        if (!isConfirmingOwnPresence && !authenticatedUser.isAdmin()) {
            var logMessage = "User with id: {} was forbidden to confirm presence of user with id: {}";
            log.info(logMessage, authenticatedUser.getId(), confirmationUserId);
            throw ForbiddenConfirmationException.notConfirmingOwnPresence();
        }
    }

    private void checkIfConfirmationIsViable(User authenticatedUser, User confirmationUser, LocalDate date) {
        var today = LocalDate.now();

        if (date.isAfter(today)) {
            log.error("Could not confirm presence for user with id: {} because date: {} is a future date",
                      confirmationUser.getId(), date);
            throw PresenceConfirmationException.confirmationInFuture();
        }

        if (!authenticatedUser.isAdmin() && date.isBefore(today.minusWeeks(2))) {
            log.error("Could not confirm presence for user with id: {} because date: {} is more than 2 weeks in the past",
                      confirmationUser.getId(), date);
            throw PresenceConfirmationException.confirmationInPast();
        }

        if (!holidayService.isWorkingDay(date)) {
            log.error("Could not confirm presence for user with id: {} because date: {} is not a working day",
                      confirmationUser.getId(), date);
            throw PresenceConfirmationException.nonWorkingDay();
        }

        if (requestService.isVacationing(confirmationUser, date)) {
            log.error("Could not confirm presence for user with id: {} because user is on vacation", confirmationUser.getId());
            throw PresenceConfirmationException.userOnVacation();
        }

        var userWorkingHoursPreference = userPreferencesService.getWorkingHoursPreferenceOf(confirmationUser.getId());
        if (userWorkingHoursPreference.isNonWorkingOn(date.getDayOfWeek())) {
            log.error("Could not confirm presence for user with id: {} on date: {} because user is not working on: {}",
                      confirmationUser.getId(), date, date.getDayOfWeek());
            throw PresenceConfirmationException.userNonWorkingDay();
        }
    }

    public void deletePresenceConfirmations(Long userId, LocalDate startDate, LocalDate endDate) {
        var filter = Filter.newBuilder()
                .and(USER_ID_FROM_PRESENCE, Operator.EQUAL, userId.toString())
                .and(DATE_FROM_PRESENCE, Operator.GREATER_OR_EQUAL, startDate.toString())
                .and(DATE_FROM_PRESENCE, Operator.LESS_OR_EQUAL, endDate.toString())
                .build();
        var presenceConfirmationsToDelete = presenceConfirmationRepository.findAll(filter);
        presenceConfirmationRepository.deleteAll(presenceConfirmationsToDelete);
    }

    public Optional<PresenceConfirmation> getFirstUserConfirmation(Long userId) {
        return presenceConfirmationRepository.findTopByPresenceConfirmationIdUserIdOrderByPresenceConfirmationIdDateAsc(userId);
    }

    public Optional<PresenceConfirmation> getFirstUserConfirmationFromStartDate(Long userId,
                                                                                LocalDate date) {
        return presenceConfirmationRepository.findFirstUserConfirmationFromStartDate(userId, date);
    }

    public double countWorkingHoursInDay(PresenceConfirmation presenceConfirmation){
        var startTime = presenceConfirmation.getStartTime();
        var endTime = presenceConfirmation.getEndTime();
        var workTime = Duration.between(startTime,endTime);
        return  workTime.toHours();
    }
}
