package info.fingo.urlopia.request;

import info.fingo.urlopia.user.UserDTO;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Tomasz Urbas
 */
public class RequestDTO implements Serializable {
    public static final int USUAL = 0;
    public static final int OCC_BIRTH_2 = 1;
    public static final int OCC_FUNERAL_2 = 2;
    public static final int OCC_WEDDING_2 = 3;
    public static final int OCC_FUNERAL_1 = 4;
    public static final int OCC_WEDDING_1 = 5;
    private long id;
    private LocalDateTime created;
    private LocalDateTime modified;
    private UserDTO requester;
    private LocalDate startDate;
    private LocalDate endDate;
    private String mailContent;

    public RequestDTO(long id, LocalDateTime created, LocalDateTime modified, UserDTO requester,
                      LocalDate startDate, LocalDate endDate, String mailContent) {

        this.id = id;
        this.created = created;
        this.modified = modified;
        this.requester = requester;
        this.startDate = startDate;
        this.endDate = endDate;
        this.mailContent = mailContent;
    }

    public String getTerm() {
        return startDate + " - " + endDate;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public UserDTO getRequester() {
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
