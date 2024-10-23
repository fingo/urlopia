package info.fingo.urlopia.history;

import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.request.RequestType;
import info.fingo.urlopia.user.User;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
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

    @Column(nullable = false)
    private Boolean countForNextYear = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private HistoryLog prevHistoryLog;

    @Column
    @Enumerated(EnumType.STRING)
    private UserDetailsChangeEvent userDetailsChangeEvent;

    public HistoryLog() {
        this.created = LocalDateTime.now();
    }

    HistoryLog(User user,
               User decider,
               float hours,
               String comment,
               HistoryLog prevHistoryLog) {
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

    HistoryLog(User user,
               User decider,
               float hours,
               String comment,
               HistoryLog prevHistoryLog,
               Boolean countForNextYear) {
        this();
        this.user = user;
        this.decider = decider;
        this.hours = hours;
        this.hoursRemaining = Optional.ofNullable(prevHistoryLog)
                .map(historyLog -> historyLog.hoursRemaining + hours).orElse(hours);
        this.userWorkTime = user.getWorkTime();
        this.comment = comment;
        this.prevHistoryLog = prevHistoryLog;
        this.countForNextYear = countForNextYear;
    }

    HistoryLog(User user,
               float hours,
               String comment,
               HistoryLog prevHistoryLog,
               UserDetailsChangeEvent event) {
        this();
        this.user = user;
        this.hours = hours;
        this.hoursRemaining = Optional.ofNullable(prevHistoryLog)
                .map(historyLog -> historyLog.hoursRemaining + hours).orElse(hours);
        this.userWorkTime = user.getWorkTime();
        this.comment = comment;
        this.prevHistoryLog = prevHistoryLog;
        this.countForNextYear = false;
        this.userDetailsChangeEvent = event;
    }

    HistoryLog(User user,
               LocalDateTime created,
               float hours,
               String comment,
               HistoryLog prevHistoryLog,
               UserDetailsChangeEvent event) {
        this.created = created;
        this.user = user;
        this.hours = hours;
        this.hoursRemaining = Optional.ofNullable(prevHistoryLog)
                .map(historyLog -> historyLog.hoursRemaining + hours).orElse(hours);
        this.userWorkTime = user.getWorkTime();
        this.comment = comment;
        this.prevHistoryLog = prevHistoryLog;
        this.countForNextYear = false;
        this.userDetailsChangeEvent = event;
    }

    HistoryLog(Request request,
               User user,
               User decider,
               float hours,
               String comment,
               HistoryLog prevHistoryLog) {
        this(user, decider, hours, comment, prevHistoryLog);
        this.request = request;
    }

    @Transient
    public float getWorkTimeNumerator() {       // TODO: Think how to remove these methods (maybe store more informations in database)
        return countWorkTimeFraction().numerator();
    }

    @Transient
    public float getWorkTimeDenominator() {
        return countWorkTimeFraction().denominator();
    }

    public List<String> getDeciderFullName() {
        if (checkIsDecidersFromRequest()){
            return request.getDeciders().stream()
                    .sorted()
                    .toList();
        }
        return decider != null ? List.of(decider.getFullName()) : new ArrayList<>();
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

    public float getHoursRemaining() {
        return hoursRemaining;
    }

    public float getUserWorkTime() {
        return userWorkTime;
    }

    public String getComment() {
        return comment;
    }

    public Boolean getCountForNextYear() {
        return countForNextYear;
    }

    public HistoryLog getPrevHistoryLog() {
        return prevHistoryLog;
    }

    public void setCountForNextYear(Boolean countForNextYear) {
        this.countForNextYear = countForNextYear;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDecider(User decider) {
        this.decider = decider;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void setHours(float hours) {
        this.hours = hours;
    }

    public void setHoursRemaining(float hoursRemaining) {
        this.hoursRemaining = hoursRemaining;
    }

    public void setUserWorkTime(float userWorkTime) {
        this.userWorkTime = userWorkTime;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setPrevHistoryLog(HistoryLog prevHistoryLog) {
        this.prevHistoryLog = prevHistoryLog;
    }

    private boolean checkIsDecidersFromRequest(){
        return request != null && request.getType() != RequestType.SPECIAL;
    }

    public UserDetailsChangeEvent getUserDetailsChangeEvent() {
        return userDetailsChangeEvent;
    }

    public void setUserDetailsChangeEvent(UserDetailsChangeEvent userDetailsChangeEvent) {
        this.userDetailsChangeEvent = userDetailsChangeEvent;
    }

    private Fraction countWorkTimeFraction(){
        float value = this.userWorkTime / 8;

        float denominator = 0;
        float numerator;

        do {
            denominator++;
            numerator = value * denominator;
        } while (Math.floor(numerator) != numerator);

        return new Fraction(numerator, denominator);
    }

    record Fraction(float numerator,
                    float denominator) {
    }


}
