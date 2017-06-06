package info.fingo.urlopia.request.acceptance;

import info.fingo.urlopia.user.UserResponse;

/**
 * @author Tomasz Urbas
 */
public class AcceptanceResponse {

    private String id;
    private UserResponse leader;
    private UserResponse decider;
    private Boolean accepted;

    public AcceptanceResponse(AcceptanceDTO acceptance) {
        this.id = String.valueOf(acceptance.getId());
        this.leader = new UserResponse(acceptance.getLeader());
        if (acceptance.getDecider() != null) {
            this.decider = new UserResponse(acceptance.getDecider());
            this.accepted = acceptance.isAccepted();
        } else {
            this.decider = null;
            this.accepted = null;
        }
    }

    public String getId() {
        return id;
    }

    public UserResponse getLeader() {
        return leader;
    }

    public UserResponse getDecider() {
        if (decider == null) {
            return leader;
        } else {
            return decider;
        }
    }

    public Boolean isAccepted() {
        return accepted;
    }
}
