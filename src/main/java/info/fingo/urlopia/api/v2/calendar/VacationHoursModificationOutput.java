package info.fingo.urlopia.api.v2.calendar;

import info.fingo.urlopia.history.HistoryLogExcerptProjection;
import lombok.Data;
import org.modelmapper.ModelMapper;

import java.time.LocalTime;

@Data
public class VacationHoursModificationOutput {
    private String comment;
    private String deciderFullName;
    private LocalTime time;
    private Float value;
    private Float hoursRemaining;
    private Float userWorkTime;
    private Integer workTimeNumerator;
    private Integer workTimeDenominator;

    public static VacationHoursModificationOutput fromHistoryLogExcerptProjection(HistoryLogExcerptProjection projection) {
        var modelMapper = new ModelMapper();
        var output = modelMapper.map(projection, VacationHoursModificationOutput.class);
        var time = projection.getCreated().toLocalTime();
        var value = projection.getHours();
        output.setTime(time);
        output.setValue(value);
        return output;
    }
}
