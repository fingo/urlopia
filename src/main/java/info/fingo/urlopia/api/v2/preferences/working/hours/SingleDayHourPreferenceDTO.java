package info.fingo.urlopia.api.v2.preferences.working.hours;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalTime;

@Data
@NoArgsConstructor // Required for jackson
public class SingleDayHourPreferenceDTO {
    private static final String TIME_PATTERN = "HH:mm";

    private Boolean nonWorking;

    @JsonFormat(pattern = TIME_PATTERN)
    private LocalTime startTime;

    @JsonFormat(pattern = TIME_PATTERN)
    private LocalTime endTime;

    public static SingleDayHourPreferenceDTO from(SingleDayHourPreference singleDayHourPreference) {
        var modelMapper = new ModelMapper();
        var dto = new SingleDayHourPreferenceDTO();
        modelMapper.map(singleDayHourPreference, dto);
        return dto;
    }
}
