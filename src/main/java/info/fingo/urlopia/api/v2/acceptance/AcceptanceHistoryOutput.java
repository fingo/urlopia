package info.fingo.urlopia.api.v2.acceptance;

import info.fingo.urlopia.acceptance.Acceptance;
import info.fingo.urlopia.request.Request;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class AcceptanceHistoryOutput {
    private Long id;
    private Long requestId;
    private String requesterName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer workingDays;
    private Acceptance.Status status;
    private Request.Status requestStatus;
    private Map<String, Acceptance.Status> leadersAcceptances;

    public static AcceptanceHistoryOutput from(Acceptance acceptance) {
        var request = acceptance.getRequest();
        var output = new AcceptanceHistoryOutput();

        output.setId(acceptance.getId());
        output.setRequestId(request.getId());
        output.setRequesterName(request.getRequester().getFullName());
        output.setStartDate(request.getStartDate());
        output.setEndDate(request.getEndDate());
        output.setWorkingDays(request.getWorkingDays());
        output.setStatus(acceptance.getStatus());
        output.setRequestStatus(request.getStatus());
        output.setLeadersAcceptances(getLeadersAcceptancesFor(request));

        return output;
    }

    private static Map<String, Acceptance.Status> getLeadersAcceptancesFor(Request request) {
        return request.getAcceptances().stream()
                .collect(Collectors.toMap(acc -> acc.getLeader().getFullName(), Acceptance::getStatus));
    }
}
