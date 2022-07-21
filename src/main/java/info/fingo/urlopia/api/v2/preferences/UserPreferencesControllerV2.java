package info.fingo.urlopia.api.v2.preferences;

import info.fingo.urlopia.api.v2.preferences.working.hours.UserWorkingHoursPreference;
import info.fingo.urlopia.api.v2.preferences.working.hours.UserWorkingHoursPreferenceDTO;
import info.fingo.urlopia.config.authentication.UserIdInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/users/preferences")
@RequiredArgsConstructor
public class UserPreferencesControllerV2 {
    private final UserPreferencesService userPreferencesService;

    @RolesAllowed({"ROLES_WORKER", "ROLES_ADMIN"})
    @GetMapping(path = "/working-hours", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Long, UserWorkingHoursPreferenceDTO> getUserWorkingHoursPreference(HttpServletRequest httpRequest) {
        var authenticatedUserId = (Long) httpRequest.getAttribute(UserIdInterceptor.USER_ID_ATTRIBUTE);
        var preferences = userPreferencesService.getWorkingHoursPreference(authenticatedUserId);
        return mapPreferencesToOutput(preferences);
    }

    private Map<Long, UserWorkingHoursPreferenceDTO> mapPreferencesToOutput(Map<Long, UserWorkingHoursPreference> preferences) {
        return preferences.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                                          entry -> UserWorkingHoursPreferenceDTO.from(entry.getValue())));
    }

    @RolesAllowed("ROLES_WORKER")
    @PutMapping(path = "/working-hours", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserWorkingHoursPreferenceDTO changeUserWorkingHoursPreference(@RequestBody UserWorkingHoursPreferenceDTO dto,
                                                                          HttpServletRequest httpRequest) {
        var authenticatedUserId = (Long) httpRequest.getAttribute(UserIdInterceptor.USER_ID_ATTRIBUTE);
        return userPreferencesService.changeWorkingHoursPreference(authenticatedUserId, dto);
    }
}
