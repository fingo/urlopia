package info.fingo.urlopia.request;

import info.fingo.urlopia.user.User;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @OneToOne
    @JoinColumn(nullable = false)
    private User requester;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column
    private String mailContent;

    /**
     * Default constructor only exists for the sake of JPA
     */
    protected Request() {
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
    }

    public Request(User requester, LocalDate startDate, LocalDate endDate, String mailContent) {
        this();
        this.requester = requester;
        this.startDate = startDate;
        this.endDate = endDate;
        this.mailContent = mailContent;
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

    public String getMailContent() {
        return mailContent;
    }
}
