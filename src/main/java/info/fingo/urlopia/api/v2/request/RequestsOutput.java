package info.fingo.urlopia.api.v2.request;

import info.fingo.urlopia.acceptance.Acceptance;
import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.request.RequestExcerptProjection;
import info.fingo.urlopia.request.RequestType;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.time.LocalDate;
import java.util.List;

@Data
public class RequestsOutput {
    private Long id;
    private String requesterName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer workingDays;
    private RequestType type;
    private Request.Status status;
    private List<AcceptanceInfoOutput> acceptances;

    public static RequestsOutput fromRequestExcerptProjection(RequestExcerptProjection projection) {
        var modelMapper = new ModelMapper();
        var output = modelMapper.map(projection, RequestsOutput.class);
        output.setId(projection.getId());
        return output;
    }

    public static RequestsOutput fromRequest(Request projection,
                                             List<Acceptance> acceptances) {
        var modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        var requestsOutput = modelMapper.map(projection, RequestsOutput.class);
        var fullName = projection.getRequester().getFullName();
        requestsOutput.setRequesterName(fullName);

        var acceptancesOutput = acceptances.stream()
                .map(s -> {
                    var output = new AcceptanceInfoOutput();
                    output.setRequesterName(s.getRequest().getRequester().getFullName());
                    output.setLeaderName(s.getLeader().getFullName());
                    output.setStatus(s.getStatus());
                    return output;
                })
                .toList();

        requestsOutput.setAcceptances(acceptancesOutput);
        return requestsOutput;
    }
}
