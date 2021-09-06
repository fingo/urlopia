package info.fingo.urlopia.api.v2.presence;

import info.fingo.urlopia.UrlopiaApplication;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.config.persistance.filter.Operator;
import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PresenceConfirmationService {
    private static final String USER_ID_FROM_PRESENCE = "presenceConfirmationId.userId";
    private static final String DATE_FROM_PRESENCE = "presenceConfirmationId.date";

    private final PresenceConfirmationRepository presenceConfirmationRepository;
    private final RequestService requestService;
    private final HolidayService holidayService;
    private final UserService userService;

    public List<PresenceConfirmation> getPresenceConfirmations(Long authenticatedUserId, String[] filters) {
        var authenticatedUser = userService.get(authenticatedUserId);
        var convertedFilters = convertFilters(filters);
        var filter = Filter.from(convertedFilters);

        if (!authenticatedUser.isAdmin()) {
            filter = filter.toBuilder()
                    .and(USER_ID_FROM_PRESENCE, Operator.EQUAL, authenticatedUserId.toString())
                    .build();
        }

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
        var dateTimeFormatter = DateTimeFormatter.ofPattern(UrlopiaApplication.DATE_FORMAT);
        var formattedDate = dateTimeFormatter.format(date);
        var filter = Filter.newBuilder()
                .and(USER_ID_FROM_PRESENCE, Operator.EQUAL, String.valueOf(userId))
                .and(DATE_FROM_PRESENCE, Operator.EQUAL, formattedDate)
                .build();
        return this.presenceConfirmationRepository.findAll(filter);
    }

    private String[] convertFilters(String[] filters) {
        return Arrays.stream(filters)
                .map(filter -> filter.replace("userId", USER_ID_FROM_PRESENCE))
                .map(filter -> filter.replace("date", DATE_FROM_PRESENCE))
                .toArray(String[]::new);
    }

    public PresenceConfirmation confirmPresence(Long authenticatedUserId, PresenceConfirmationInputOutput dto) {
        var authenticatedUser = userService.get(authenticatedUserId);
        var confirmationUserId = dto.getUserId();
        checkIfUserIsAuthorizedToConfirmPresence(authenticatedUser, confirmationUserId);

        var isConfirmingOwnPresence = authenticatedUserId.equals(confirmationUserId);
        var confirmationUser = isConfirmingOwnPresence ? authenticatedUser : userService.get(confirmationUserId);
        checkIfConfirmationIsViable(confirmationUser, dto.getDate());

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
            var logMessage = "User: {} was forbidden to confirm presence of user with id: {}";
            log.info(logMessage, authenticatedUser.getPrincipalName(), confirmationUserId);
            throw ForbiddenConfirmationException.notConfirmingOwnPresence();
        }
    }

    private void checkIfConfirmationIsViable(User user, LocalDate date) {
        var today = LocalDate.now();

        if (date.isAfter(today)) {
            log.error("Could not confirm presence for user with id: {} because date: {} is a future date",
                      user.getId(), date);
            throw PresenceConfirmationException.confirmationInFuture();
        }

        if (!user.isAdmin() && date.isBefore(today.minusWeeks(2))) {
            log.error("Could not confirm presence for user with id: {} because date: {} is more than 2 weeks in the past",
                      user.getId(), date);
            throw PresenceConfirmationException.confirmationInPast();
        }

        if (!holidayService.isWorkingDay(date)) {
            log.error("Could not confirm presence for user with id: {} because date: {} is not a working day",
                      user.getId(), date);
            throw PresenceConfirmationException.nonWorkingDay();
        }

        var userId = user.getId();
        var isUserOnVacation = !requestService.getByUserAndDate(userId, date).isEmpty();
        if (isUserOnVacation) {
            log.error("Could not confirm presence for user with id: {} because user is on vacation", userId);
            throw PresenceConfirmationException.userOnVacation();
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
}
