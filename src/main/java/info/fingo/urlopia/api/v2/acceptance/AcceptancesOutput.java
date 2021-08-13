package info.fingo.urlopia.api.v2.acceptance;

import info.fingo.urlopia.acceptance.Acceptance;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AcceptancesOutput {
    private Long id;
    private String requesterName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer workingDays;
    private Acceptance.Status status;
}
