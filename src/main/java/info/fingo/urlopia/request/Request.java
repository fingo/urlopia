package info.fingo.urlopia.request;

import info.fingo.urlopia.acceptance.Acceptance;
import info.fingo.urlopia.request.occasional.OccasionalType;
import info.fingo.urlopia.user.User;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "Requests")
public class Request {

    @Id
    @SequenceGenerator(name = "requests_id_seq", sequenceName = "requests_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "requests_id_seq")
    private Long id;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(nullable = false)
    private LocalDateTime modified;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User requester;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer workingDays;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestType type;

    @Column
    private String typeInfo;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "request")
    private Set<Acceptance> acceptances;

    public Request() {
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
    }

    public Request(User requester, LocalDate startDate, LocalDate endDate, Integer workingDays,
                   RequestType type, TypeInfo typeInfo, Status status) {
        this();
        this.requester = requester;
        this.startDate = startDate;
        this.endDate = endDate;
        this.workingDays = workingDays;
        this.type = type;
        this.typeInfo = (typeInfo != null) ? typeInfo.getName() : null;
        this.status = status;
    }

    public Request(User requester, LocalDate startDate, LocalDate endDate,
                   RequestType type, TypeInfo typeInfo, Status status) {
        this();
        this.requester = requester;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.typeInfo = (typeInfo != null) ? typeInfo.getName() : null;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    public User getRequester() {
        return requester;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public TypeInfo getTypeInfo() {
        TypeInfo typeInfo = null;

        TypeInfo typesInfo[] = OccasionalType.values(); // TODO: remove OccasionalType from here
        for(TypeInfo tempTypeInfo : typesInfo) {
            if(tempTypeInfo.getName().equals(this.typeInfo)) {
                typeInfo = tempTypeInfo;
                break;
            }
        }

        return typeInfo;
    }

    public void setTypeInfo(TypeInfo typeInfo) {
        this.typeInfo = typeInfo.getName();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(Integer workingDays) {
        this.workingDays = workingDays;
    }

    public Set<Acceptance> getAcceptances() {
        return acceptances;
    }

    @Transient
    public Set<String> getDeciders() {
        return this.acceptances.stream()
                .map(Acceptance::getLeader)
                .map(user -> String.format("%s %s", user.getFirstName(), user.getLastName()))
                .collect(Collectors.toSet());
    }

    @Transient
    public boolean isAffecting() {
        return this.status == Status.ACCEPTED || this.status == Status.PENDING;
    }

    @Transient
    public boolean isOverlapping(Request request) {
        return !this.startDate.isAfter(request.endDate)
                && !this.endDate.isBefore(request.startDate);
    }

    public boolean isNormal() {
        return this.type == RequestType.NORMAL;
    }

    public interface TypeInfo { // TODO: separate TypeInfo interface
        String getInfo();
        String getName();
    }

    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED,
        CANCELED
    }
}
