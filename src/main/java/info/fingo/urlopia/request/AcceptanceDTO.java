package info.fingo.urlopia.request;

import info.fingo.urlopia.user.UserDTO;

import java.io.Serializable;

/**
 * @author Tomasz Urbas
 */
public class AcceptanceDTO implements Serializable {
    private long id;
    private RequestDTO request;
    private UserDTO leader;
    private UserDTO decider;
    private boolean accepted;

    AcceptanceDTO(long id, RequestDTO request, UserDTO leader) {
        this.id = id;
        this.request = request;
        this.leader = leader;
    }

    public AcceptanceDTO(long id, RequestDTO request, UserDTO leader, UserDTO decider, boolean accepted) {
        this(id, request, leader);
        this.decider = decider;
        this.accepted = accepted;
    }

    public Boolean getDecision() {
        Boolean decision = null;

        if (decider != null) {
            decision = accepted;
        }

        return decision;
    }

    public long getId() {
        return id;
    }

    public RequestDTO getRequest() {
        return request;
    }

    public UserDTO getLeader() {
        return leader;
    }

    public UserDTO getDecider() {
        return decider;
    }

    public boolean isAccepted() {
        return accepted;
    }
}
