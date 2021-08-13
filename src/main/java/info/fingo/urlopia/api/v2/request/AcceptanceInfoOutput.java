package info.fingo.urlopia.api.v2.request;

import info.fingo.urlopia.acceptance.Acceptance;
import lombok.Data;

@Data
public class AcceptanceInfoOutput {
    private String requesterName;
    private String leaderName;
    private Acceptance.Status status;
}
