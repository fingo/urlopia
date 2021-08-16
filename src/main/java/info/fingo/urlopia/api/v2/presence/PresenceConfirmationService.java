package info.fingo.urlopia.api.v2.presence;

import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PresenceConfirmationService {
    private final PresenceConfirmationRepository presenceConfirmationRepository;
    private final RequestService requestService;
    private final HolidayService holidayService;
    private final UserService userService;

    public PresenceConfirmation confirmPresence(Long authenticatedUserId, PresenceConfirmationInputOutput dto) {
        var authenticatedUser = userService.get(authenticatedUserId);
        var confirmationUserId = dto.getUserId();
        checkIfUserIsAuthorized(authenticatedUser, confirmationUserId);

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

    private void checkIfUserIsAuthorized(User authenticatedUser, Long confirmationUserId) {
        var isConfirmingOwnPresence = authenticatedUser.getId().equals(confirmationUserId);
        if (!isConfirmingOwnPresence && !authenticatedUser.isAdmin()) {
            var format = "User: %s was forbidden to confirm presence of user with id: %d";
            var logMessage = String.format(format, authenticatedUser.getPrincipalName(), confirmationUserId);
            log.info(logMessage);
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
