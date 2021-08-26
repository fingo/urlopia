package info.fingo.urlopia.api.v2.history;

import com.fasterxml.jackson.annotation.JsonFormat;
import info.fingo.urlopia.history.HistoryLogExcerptProjection;
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

    public static List<HistoryLogOutput> from(List<HistoryLogExcerptProjection> projections) {
        var modelMapper = new ModelMapper();

        return projections.stream()
                    .map(x->modelMapper.map(x, HistoryLogOutput.class))
                    .toList();
    }
}
