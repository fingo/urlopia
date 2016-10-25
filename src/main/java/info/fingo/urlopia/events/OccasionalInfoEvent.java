package info.fingo.urlopia.events;

import info.fingo.urlopia.user.UserDTO;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDate;

/**
 * @author Tomasz Pilarczyk
 */
public class OccasionalInfoEvent extends ApplicationEvent {

    private UserDTO user;
    private int type;
    private float hours;
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */

    public OccasionalInfoEvent(Object source, UserDTO user, int type, float hours, LocalDate startDate, LocalDate endDate) {
        super(source);
        this.user = user;
        this.type = type;
        this.hours = hours;
        this.startDate = startDate;
        this.endDate = endDate;

    }

    public UserDTO getUser() {
        return user;
    }

    public float getHours() {
        return hours;
    }

    public int getType() {
        return type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
