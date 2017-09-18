package info.fingo.urlopia.request.acceptance;

import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.user.User;

import javax.persistence.*;

@Entity
@Table(name = "Acceptances")
public class Acceptance {

    @Id
    @SequenceGenerator(name = "acceptances_id_seq", sequenceName = "acceptances_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "acceptances_id_seq")
    private long id;

    @OneToOne
    @JoinColumn(nullable = false)
    private Request request;

    @OneToOne
    @JoinColumn(nullable = false)
    private User leader;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    protected Acceptance() {
        // No-args constructor required by *Hibernate*
    }

    public Acceptance(Request request, User leader) {
        this();
        this.request = request;
        this.leader = leader;
        this.status = Status.PENDING;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public User getLeader() {
        return leader;
    }

    public void setLeader(User leader) {
        this.leader = leader;
    }

    public User getDecider() {
        return null;
    }

    public void setDecider(User decider) {
        // TODO: remove: it
    }

    public boolean isAccepted() {
        return false;
    }

    public void setAccepted(boolean accepted) {
        // TODO: remove it
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED,
        EXPIRED
    }
}
