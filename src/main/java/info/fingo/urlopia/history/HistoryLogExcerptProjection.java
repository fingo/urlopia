package info.fingo.urlopia.history;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface HistoryLogExcerptProjection {

    Long getId();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getCreated();

    @Value("#{target.request != null ? target.request.getDeciders() : target.decider.firstName + ' ' + target.decider.lastName}")
    String[] getDeciders();

    Float getHours();

    Float getHoursRemaining();

    Float getUserWorkTime();

    String getComment();

    Integer getWorkTimeNumerator();

    Integer getWorkTimeDenominator();

    String getDeciderFullName();
}
