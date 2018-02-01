package info.fingo.urlopia.history;

import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name = "history_logs")
public class HistoryLog {   // TODO: Think about removing all relations from log entity

    @Id
    @SequenceGenerator(name = "history_logs_id_seq", sequenceName = "history_logs_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "history_logs_id_seq")
    private Long id;

    @Column(nullable = false)
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    private User decider;       // TODO: change to list of deciders

    @ManyToOne
    private Request request;

    @Column(nullable = false)
    private float hours;

    @Column(nullable = false)
    private float hoursRemaining;

    @Column(nullable = false)
    private float userWorkTime;

    private String comment = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private HistoryLog prevHistoryLog;

    public HistoryLog() {
        this.created = LocalDateTime.now();
    }

    HistoryLog(User user, User decider, float hours, String comment, HistoryLog prevHistoryLog) {
        this();
        this.user = user;
        this.decider = decider;
        this.hours = hours;
        this.hoursRemaining = Optional.ofNullable(prevHistoryLog)
                .map(historyLog -> historyLog.hoursRemaining + hours).orElse(hours);
        this.userWorkTime = user.getWorkTime();
        this.comment = comment;
        this.prevHistoryLog = prevHistoryLog;
    }

    HistoryLog(Request request, User user, User decider, float hours, String comment, HistoryLog prevHistoryLog) {
        this(user, decider, hours, comment, prevHistoryLog);
        this.request = request;
    }

    @Transient
    public float getWorkTimeNumerator() {       // TODO: Think how to remove these methods (maybe store more informations in database)
        float value = this.userWorkTime / 8;

        float denominator = 0;
        float numerator;

        do {
            denominator++;
            numerator = value * denominator;
        } while (Math.floor(numerator) != numerator);

        return numerator;
    }

    @Transient
    public float getWorkTimeDenominator() {
        float value = this.userWorkTime / 8;

        float denominator = 0;
        float numerator;

        do {
            denominator++;
            numerator = value * denominator;
        } while (Math.floor(numerator) != numerator);

        return denominator;
    }

    public Long getId() {
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

    public float getUserWorkTime() {
        return userWorkTime;
    }

    public String getComment() {
        return comment;
    }

    public float getHoursRemaining() {
        return hoursRemaining;
    }

    public HistoryLog getPrevHistoryLog() {
        return prevHistoryLog;
    }
}
