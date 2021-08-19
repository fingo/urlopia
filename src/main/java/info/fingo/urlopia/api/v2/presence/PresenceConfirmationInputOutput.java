package info.fingo.urlopia.api.v2.presence;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor // Required for jackson
public class PresenceConfirmationInputOutput {
    private static final String TIME_PATTERN = "HH:mm";

    private LocalDate date;
    @JsonFormat(pattern = TIME_PATTERN)
    private LocalTime startTime;
    @JsonFormat(pattern = TIME_PATTERN)
    private LocalTime endTime;
    private Long userId;

    public static PresenceConfirmationInputOutput from(PresenceConfirmation presenceConfirmation) {
        var modelMapper = new ModelMapper();
        return modelMapper.map(presenceConfirmation, PresenceConfirmationInputOutput.class);
    }

    public static List<PresenceConfirmationInputOutput> listFrom(List<PresenceConfirmation> presenceConfirmations) {
        return presenceConfirmations.stream()
                .map(PresenceConfirmationInputOutput::from)
                .toList();
    }
}
