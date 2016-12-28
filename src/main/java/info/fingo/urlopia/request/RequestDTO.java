package info.fingo.urlopia.request;

import info.fingo.urlopia.user.UserDTO;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Tomasz Urbas
 */
public class RequestDTO implements Serializable {
    private long id;
    private LocalDateTime created;
    private LocalDateTime modified;
    private UserDTO requester;
    private LocalDate startDate;
    private LocalDate endDate;
    private Request.Type type;
    private Request.TypeInfo typeInfo;
    private Request.Status status;

    public RequestDTO(long id, LocalDateTime created, LocalDateTime modified, UserDTO requester,
                      LocalDate startDate, LocalDate endDate, Request.Type type, Request.TypeInfo typeInfo, Request.Status status) {

        this.id = id;
        this.created = created;
        this.modified = modified;
        this.requester = requester;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.typeInfo = typeInfo;
        this.status = status;
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

    public Request.Type getType() {
        return type;
    }

    public Request.TypeInfo getTypeInfo() {
        return typeInfo;
    }

    public Request.Status getStatus() {
        return status;
    }
}
