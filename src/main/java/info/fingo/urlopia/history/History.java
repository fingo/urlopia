package info.fingo.urlopia.history;

import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Józef Grodzicki
 */
@Entity
@Table(name = "History")
public class History {
    @Id
    @SequenceGenerator(name = "history_id_seq", sequenceName = "history_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "history_id_seq")
    private long id;

    @Column(nullable = false)
    private LocalDateTime created;

    @OneToOne
    @JoinColumn(nullable = false)
    private User user;

    @OneToOne
    @JoinColumn
    private User decider;

    @OneToOne
    @JoinColumn
    private Request request;

    @Column(nullable = false)
    private float hours;

    @Column(nullable = false)
    private int type;

    @Column
    private String comment;

    /**
     * Default constructor only exists for the sake of JPA
     */
    protected History() {
    }

    public History(Request request, float hours, int type) {
        this.created = LocalDateTime.now();
        this.user = request.getRequester();
        this.request = request;
        this.hours = hours;
        this.comment = "";
        this.type = type;
    }

    public History(User user, User decider, float hours, String comment, int type) {
        this.created = LocalDateTime.now();
        this.user = user;
        this.decider = decider;
        this.hours = hours;
        this.comment = comment;
        this.type = type;
    }

    public History(Request request, User user, User decider, float hours, String comment, int type) {
        this.created = LocalDateTime.now();
        this.request = request;
        this.user = user;
        this.decider = decider;
        this.hours = hours;
        this.comment = comment;
        this.type = type;
    }

    @Override
    public String toString() {

        return "History{" +
                "id=" + id +
                ", created='" + created + '\'' +
                ", user='" + user + '\'' +
                ", decider='" + decider + '\'' +
                ", request=" + request + '\'' +
                ", hours='" + hours + '\'' +
                '}';
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public User getUser() {
        return user;
    }

    public User getDecider() {
        return decider;
    }

    public Request getRequest() {
        return request;
    }

    public float getHours() {
        return hours;
    }

    public String getComment() {
        return comment;
    }
}
