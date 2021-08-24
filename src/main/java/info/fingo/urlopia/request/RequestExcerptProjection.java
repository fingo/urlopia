package info.fingo.urlopia.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import info.fingo.urlopia.acceptance.AcceptanceExcerptProjection;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.Set;

public interface RequestExcerptProjection {

    Long getId();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate getStartDate();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate getEndDate();

    Integer getWorkingDays();

    Request.Status getStatus();

    @Value("#{target.requester.firstName} #{target.requester.lastName}")
    String getRequesterName();

    RequestType getType();

    String getSpecialTypeInfo();

    Set<AcceptanceExcerptProjection> getAcceptances();

}
