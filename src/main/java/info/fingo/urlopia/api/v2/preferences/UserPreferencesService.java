package info.fingo.urlopia.api.v2.preferences;

import info.fingo.urlopia.api.v2.preferences.working.hours.*;
import info.fingo.urlopia.api.v2.presence.PresenceConfirmation;
import info.fingo.urlopia.api.v2.presence.PresenceConfirmationRepository;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPreferencesService {
    private final UserWorkingHoursPreferenceRepository userWorkingHoursPreferenceRepository;
    private final PresenceConfirmationRepository presenceConfirmationRepository;
    private final UserService userService;
    private final ModelMapper modelMapper = new ModelMapper();

    public Map<Long, UserWorkingHoursPreference> getWorkingHoursPreference(Long authenticatedUserId) {
        var user = userService.get(authenticatedUserId);
        var filter = Filter.empty();

        if (!user.isAdmin()) {
            return Map.of(authenticatedUserId, getWorkingHoursPreferenceOf(authenticatedUserId));
        }

        return userWorkingHoursPreferenceRepository.findAll(filter).stream()
                .collect(Collectors.toMap(UserWorkingHoursPreference::getUserId, pref -> pref));
    }

    // if unspecified absence endpoint still be slow this is probably the place to change
    public UserWorkingHoursPreference getWorkingHoursPreferenceOf(Long userId) {
        var userPreferences = userWorkingHoursPreferenceRepository.findById(userId);
        return userPreferences.orElse(initUserPreferences(userId));
    }


    private UserWorkingHoursPreference initUserPreferences(Long userId){
        userWorkingHoursPreferenceRepository.save(UserWorkingHoursPreference.getDefault(userId));
        return userWorkingHoursPreferenceRepository.getById(userId);
    }

    public UserWorkingHoursPreferenceDTO changeWorkingHoursPreference(Long authenticatedUserId,
                                                                      UserWorkingHoursPreferenceDTO dto) {
        var newPreference = userWorkingHoursPreferenceRepository.findById(authenticatedUserId)
                .map(preference -> {
                    fixPresenceConfirmations(preference);
                    return updateExistingPreference(preference, dto);
                })
                .orElseGet(() -> createNewPreferenceUsing(authenticatedUserId, dto));
        return UserWorkingHoursPreferenceDTO.from(userWorkingHoursPreferenceRepository.save(newPreference));
    }

    private UserWorkingHoursPreference updateExistingPreference(UserWorkingHoursPreference preference,
                                                                UserWorkingHoursPreferenceDTO dto) {

        for (var dayOfWeek : DayOfWeek.values()) {
            preference.getDayPreferenceBy(dayOfWeek)
                    .ifPresent(singleDayPref -> dto.getDayPreferenceBy(dayOfWeek)
                            .ifPresent(singleDayDto -> updateSingleDayPreferenceByDto(singleDayPref, singleDayDto)));
        }

        preference.setChanged(LocalDateTime.now());

        return preference;
    }

    private UserWorkingHoursPreference createNewPreferenceUsing(Long authenticatedUserId,
                                                                UserWorkingHoursPreferenceDTO dto) {
        var preference = new UserWorkingHoursPreference();

        preference.setUserId(authenticatedUserId);

        for (var dayOfWeek : DayOfWeek.values()) {
            dto.getDayPreferenceBy(dayOfWeek)
                    .ifPresent(singleDayDto -> preference.setDayPreferenceBy(dayOfWeek, singleDayHourPreferenceFromDto(singleDayDto)));
        }

        return preference;
    }

    private void updateSingleDayPreferenceByDto(SingleDayHourPreference singleDayPreference,
                                                SingleDayHourPreferenceDTO dto) {
        modelMapper.map(dto, singleDayPreference);
    }

    private SingleDayHourPreference singleDayHourPreferenceFromDto(SingleDayHourPreferenceDTO dto) {
        var singleDayHours = new SingleDayHourPreference();
        modelMapper.map(dto, singleDayHours);
        return singleDayHours;
    }

    private void fixPresenceConfirmations(UserWorkingHoursPreference previous) {
        var previousPreferenceChangeDate = previous.getChanged().toLocalDate();
        var today = LocalDate.now();

        var previousNonWorkingDays = getPreviousNonWorkingDays(previous);
        var user = userService.get(previous.getUserId());

        previousPreferenceChangeDate.datesUntil(today)
                .filter(day -> previousNonWorkingDays.contains(day.getDayOfWeek()))
                .forEach(day -> presenceConfirmationRepository.save(PresenceConfirmation.empty(user, day)));
    }

    private Set<DayOfWeek> getPreviousNonWorkingDays(UserWorkingHoursPreference previous) {
        return Arrays.stream(DayOfWeek.values())
                .filter(previous::isNonWorkingOn)
                .collect(Collectors.toSet());
    }
}
