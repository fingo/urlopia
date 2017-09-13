package info.fingo.urlopia.request.acceptance;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;

public interface AcceptanceExcerptProjection {

    Long getId();

    @Value("#{target.request.requester.firstName} #{target.request.requester.lastName}")
    String getRequesterName();

    @Value("#{target.leader.firstName} #{target.leader.lastName}")
    String getLeaderName();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Value("#{target.request.startDate}")
    LocalDate getStartDate();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Value("#{target.request.endDate}")
    LocalDate getEndDate();

    @Value("#{target.request.workingDays}")
    Integer getWorkingDays();

    Acceptance.Status getStatus();

}
