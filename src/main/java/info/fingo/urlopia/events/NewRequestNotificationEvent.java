package info.fingo.urlopia.events;

import org.springframework.context.ApplicationEvent;

/**
 * @author Mateusz Wiśniewski
 */
public class NewRequestNotificationEvent extends ApplicationEvent {

    private final long id;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public NewRequestNotificationEvent(Object source, long id) {
        super(source);
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
