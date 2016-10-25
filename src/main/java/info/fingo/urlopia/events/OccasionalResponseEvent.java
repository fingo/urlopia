package info.fingo.urlopia.events;

import info.fingo.urlopia.user.UserDTO;
import org.springframework.context.ApplicationEvent;

/**
 * @author Tomasz Pilarczyk
 */
public class OccasionalResponseEvent extends ApplicationEvent {


    private UserDTO user;
    private int type;
    private float hours;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */

    public OccasionalResponseEvent(Object source, UserDTO user, int type, float hours) {
        super(source);
        this.user = user;
        this.type = type;
        this.hours = hours;
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

}
