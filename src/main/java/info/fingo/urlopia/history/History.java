package info.fingo.urlopia.history;

import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name = "History")
public class History {

    @Id
    @SequenceGenerator(name = "history_id_seq", sequenceName = "history_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "history_id_seq")
    private long id;

    @Column(nullable = false)
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn
    private User decider;

    @ManyToOne
    @JoinColumn
    private Request request;

    @Column(nullable = false)
    private float hours;

    @Column(nullable = false)
    private float hoursRemaining = 0;

    @Column(nullable = false)
    private float workTime;

    private String comment = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private History prevHistory;

    protected History() {
        this.created = LocalDateTime.now();
    }

    public History(Request request, float hours) {
        this();
        this.user = request.getRequester();
        this.request = request;
        this.hours = hours;
        this.hoursRemaining = Optional.ofNullable(prevHistory).map(History::getHours).orElse(0f);
        this.workTime = request.getRequester().getWorkTime();
        this.comment = "";
    }

    public History(Request request, float hours, String comment) {
        this(request, hours);
        this.comment = comment;
    }

    public History(Request request, float hours, String comment, User decider) {
        this(request, hours);
        this.comment = comment;
        this.decider = decider;
    }

    public History(User user, User decider, float hours, String comment) {
        this();
        this.user = user;
        this.decider = decider;
        this.hours = hours;
        this.workTime = user.getWorkTime();
        this.comment = comment;
    }

    public History(User user, User decider, float hours, String comment, History prevHistory) {
        this();
        this.user = user;
        this.decider = decider;
        this.hours = hours;
        this.hoursRemaining = Optional.ofNullable(prevHistory)
                .map(history -> history.getHoursRemaining() + hours).orElse(hours);
        this.workTime = user.getWorkTime();
        this.comment = comment;
        this.prevHistory = prevHistory;
    }

    public float getWorkTimeNominator() {
        float value = this.workTime / 8;

        float denominator = 0;
        float nominator;

        do {
            denominator++;
            nominator = value * denominator;
        } while (Math.floor(nominator) != nominator);

        return nominator;
    }

    public float getWorkTimeDenominator() {
        float value = this.workTime / 8;

        float denominator = 0;
        float nominator;

        do {
            denominator++;
            nominator = value * denominator;
        } while (Math.floor(nominator) != nominator);

        return denominator;
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

    public void setHours(float hours) {
        this.hours = hours;
    }

    public float getWorkTime() {
        return workTime;
    }

    public String getComment() {
        return comment;
    }

    public float getHoursRemaining() {
        return hoursRemaining;
    }

    public History getPrevHistory() {
        return prevHistory;
    }
}
