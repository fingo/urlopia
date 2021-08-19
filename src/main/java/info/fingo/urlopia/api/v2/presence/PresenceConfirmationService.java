package info.fingo.urlopia.api.v2.presence;

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
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PresenceConfirmationService {
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
                    .and("presenceConfirmationId.userId", Operator.EQUAL, authenticatedUserId.toString())
                    .build();
        }

        return presenceConfirmationRepository.findAll(filter);
    }

    private String[] convertFilters(String[] filters) {
        return Arrays.stream(filters)
                .map(filter -> filter.replace("userId", "presenceConfirmationId.userId"))
                .map(filter -> filter.replace("date", "presenceConfirmationId.date"))
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
            throw PresenceConfirmationException.forbiddenConfirmation();
        }
    }

    private void checkIfConfirmationIsViable(User user, LocalDate date) {
        if (!holidayService.isWorkingDay(date)) {
            throw PresenceConfirmationException.nonWorkingDay();
        }
        var userId = user.getId();
        var isUserOnVacation = !requestService.getByUserAndDate(userId, date).isEmpty();
        if (isUserOnVacation) {
            throw PresenceConfirmationException.userOnVacation();
        }
    }
}
