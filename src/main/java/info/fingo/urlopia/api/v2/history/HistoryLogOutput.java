package info.fingo.urlopia.api.v2.history;

import com.fasterxml.jackson.annotation.JsonFormat;
import info.fingo.urlopia.history.HistoryLog;
import info.fingo.urlopia.history.HistoryLogExcerptProjection;
import info.fingo.urlopia.history.UserDetailsChangeEvent;
import lombok.Data;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class HistoryLogOutput {
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    private String comment;
    private Float hours;
    private Float hoursRemaining;
    private String deciderFullName;
    private Integer workTimeDenominator;
    private Integer workTimeNumerator;
    private Float userWorkTime;
    private Boolean countForNextYear;
    private UserDetailsChangeEvent userDetailsChangeEvent;

    public static List<HistoryLogOutput> from(List<HistoryLogExcerptProjection> projections) {
        var mapper = new ModelMapper();
        return projections.stream()
                .map(proj -> mapper.map(proj, HistoryLogOutput.class))
                .toList();
    }

    public static HistoryLogOutput from(HistoryLogExcerptProjection projection) {
        var mapper = new ModelMapper();
        return mapper.map(projection, HistoryLogOutput.class);
    }

    public static HistoryLogOutput from(HistoryLog historyLog) {
        var historyLogOutput = new HistoryLogOutput();
        historyLogOutput.setId(historyLog.getId());
        historyLogOutput.setCreated(historyLog.getCreated());
        historyLogOutput.setComment(historyLog.getComment());
        historyLogOutput.setHours(historyLog.getHours());
        historyLogOutput.setHoursRemaining(historyLog.getHoursRemaining());
        var historyLogDeciders = historyLog.getDeciderFullName();
        historyLogOutput.setDeciderFullName(historyLogDeciders.size()>0 ? historyLogDeciders.get(0): "");
        historyLogOutput.setWorkTimeDenominator(Math.round(historyLog.getWorkTimeDenominator()));
        historyLogOutput.setWorkTimeNumerator(Math.round(historyLog.getWorkTimeNumerator()));
        historyLogOutput.setUserWorkTime(historyLog.getUserWorkTime());
        historyLogOutput.setCountForNextYear(historyLog.getCountForNextYear());
        historyLogOutput.setUserDetailsChangeEvent(historyLog.getUserDetailsChangeEvent());
        return historyLogOutput;
    }
}
