package info.fingo.urlopia.request;

import info.fingo.urlopia.user.UserResponse;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tomasz Urbas
 */
public class RequestResponse {

    private String id;
    private String term;
    private List<AcceptanceResponse> acceptances;
    private Boolean accepted;
    private boolean cancelled;
    private String status;
    private UserResponse requester;
    private String type;

    public RequestResponse(RequestDTO request, List<AcceptanceDTO> acceptances) {
        this.id = String.valueOf(request.getId());
        this.term = request.getTerm();
        this.acceptances = new LinkedList<>();
        this.acceptances.addAll(acceptances.stream()
                .map(AcceptanceResponse::new)
                .collect(Collectors.toList()));
        this.accepted = isAccepted(acceptances);
        this.cancelled = isCancelled(acceptances, request);
        this.status = countStatus(acceptances);
        this.requester = new UserResponse(request.getRequester());
        this.type = request.getType().toString();
    }

    public RequestResponse(AcceptanceDTO acceptance) {
        this(acceptance.getRequest(), Arrays.asList(acceptance));
        this.id = String.valueOf(acceptance.getId());
    }

    private String countStatus(List<AcceptanceDTO> acceptances) {
        int counter = 0;
        for (AcceptanceDTO acceptance : acceptances) {
            if (acceptance.getDecider() != null) {
                counter++;
            }
        }

        return counter + "/" + acceptances.size();
    }

    private Boolean isAccepted(List<AcceptanceDTO> acceptances) {
        Boolean accepted = true;

        for (AcceptanceDTO acceptance : acceptances) {
            if (acceptance.getDecider() == null) {
                accepted = null;
                break;
            } else if (!acceptance.isAccepted()) {
                accepted = false;
                break;
            }
        }

        return accepted;
    }

    private boolean isCancelled(List<AcceptanceDTO> acceptances, RequestDTO requestDTO) {
        for (AcceptanceDTO acceptance : acceptances) {
            if (acceptance.getDecider() != null)
                if (acceptance.getDecider().getId() == requestDTO.getRequester().getId()) {
                    return true;
                }
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public String getTerm() {
        return term;
    }

    public List<AcceptanceResponse> getAcceptances() {
        return acceptances;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public UserResponse getRequester() {
        return requester;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }
}
