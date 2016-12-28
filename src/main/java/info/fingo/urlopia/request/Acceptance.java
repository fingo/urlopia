package info.fingo.urlopia.request;

import info.fingo.urlopia.user.User;

import javax.persistence.*;

/**
 * AcceptanceDTO entity.
 *
 * @author Tomasz Urbas
 */
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

    @OneToOne
    @JoinColumn
    private User decider;

    @Column(nullable = false)
    private boolean accepted;

    protected Acceptance() {
        this.decider = null;
        this.accepted = false;
    }

    public Acceptance(Request request, User leader) {
        this();
        this.request = request;
        this.leader = leader;
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
        return decider;
    }

    public void setDecider(User decider) {
        this.decider = decider;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
