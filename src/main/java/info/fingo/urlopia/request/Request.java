package info.fingo.urlopia.request;

import info.fingo.urlopia.request.acceptance.Acceptance;
import info.fingo.urlopia.user.User;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * RequestDTO entity.
 *
 * @author Mateusz Wiśniewski
 */
@Entity
@Table(name = "Requests")
public class Request {

    @Id
    @SequenceGenerator(name = "requests_id_seq", sequenceName = "requests_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "requests_id_seq")
    private long id;

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
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column
    private String typeInfo;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "request")
    private Set<Acceptance> acceptances;

    /**
     * Default constructor only exists for the sake of JPA
     */
    public Request() {
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
    }

    public Request(User requester, LocalDate startDate, LocalDate endDate, Type type, TypeInfo typeInfo, Status status) {
        this();
        this.requester = requester;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.typeInfo = (typeInfo != null) ? typeInfo.getName() : null;
        this.status = status;
    }

    public long getId() {
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public TypeInfo getTypeInfo() {
        TypeInfo typeInfo = null;

        TypeInfo typesInfo[] = OccasionalType.values();
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

    public Set<String> getDeciders() {
        Set<String> users = this.acceptances.stream()
                .map(acceptance -> acceptance.getDecider() != null ? acceptance.getDecider() : acceptance.getLeader())
                .map(user -> String.format("%s %s", user.getFirstName(), user.getLastName()))
                .collect(Collectors.toSet());
        return users;
    }

    public enum Type {
        NORMAL,
        OCCASIONAL
    }

    public interface TypeInfo {
        String getInfo();
        String getName();
    }

    public enum OccasionalType implements TypeInfo {
        // TODO: Localize it!
        // TODO: Check days before write in database
        WRONG (0, "Niepoprawny typ okazjonalny", 0),
        D2_BIRTH (1, "Narodziny dziecka", 2),
        D2_FUNERAL (2, "Zgon/pogrzeb osoby z najbliższej rodziny", 2),
        D2_WEDDING (3, "Ślub", 2),
        D1_FUNERAL (4, "Zgon/pogrzeb osoby bliskiej", 1),
        D1_WEDDING (5, "Ślub dziecka", 1);

        private int index;
        private String info;
        private int durationDays;

        OccasionalType(int index, String info, int durationDays) {
            this.index = index;
            this.info = info;
            this.durationDays = durationDays;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return this.name();
        }

        public String getInfo() {
            return info;
        }

        public int getDurationDays() {
            return durationDays;
        }
    }

    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED,
        CANCELLED
    }
}
