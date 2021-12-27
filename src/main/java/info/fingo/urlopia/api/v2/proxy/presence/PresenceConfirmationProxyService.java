package info.fingo.urlopia.api.v2.proxy.presence;

import info.fingo.urlopia.api.v2.presence.PresenceConfirmationInputOutput;
import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService;
import info.fingo.urlopia.api.v2.presence.Utils;
import info.fingo.urlopia.api.v2.proxy.ProxyException;
import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresenceConfirmationProxyService {
    private static final LocalTime DEFAULT_START_TIME = LocalTime.of(8, 0);

    private final UserService userService;
    private final PresenceConfirmationService presenceConfirmationService;

    public void confirmPresence(PresenceConfirmationProxyInput proxyInput) {
        var user = userService.get(proxyInput.email());

        var startTime = DEFAULT_START_TIME;
        var endTime = startTime.plusMinutes(Math.round(60 * user.getWorkTime()));

        var timeRangeString = proxyInput.hours();
        if (!timeRangeString.isBlank()) {
            List<LocalTime> presenceTime = Utils.getTimeRangeFrom(timeRangeString);

            if (presenceTime.isEmpty()) {
                log.error("Couldn't parse given time range: {}", timeRangeString);
                throw ProxyException.invalidTimeRange();
            }

            startTime = presenceTime.get(0);
            endTime = presenceTime.get(1);
        }

        var dto = new PresenceConfirmationInputOutput();
        dto.setUserId(user.getId());
        dto.setDate(LocalDate.now());
        dto.setStartTime(startTime);
        dto.setEndTime(endTime);

        presenceConfirmationService.confirmPresence(user, dto);
    }
}
