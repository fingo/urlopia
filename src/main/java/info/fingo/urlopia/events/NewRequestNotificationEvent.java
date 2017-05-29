package info.fingo.urlopia.events;

import info.fingo.urlopia.request.AcceptanceDTO;
import org.springframework.context.ApplicationEvent;

/**
 * @author Mateusz Wiśniewski
 */
public class NewRequestNotificationEvent extends ApplicationEvent {

    private final AcceptanceDTO acceptance;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public NewRequestNotificationEvent(Object source, AcceptanceDTO acceptance) {
        super(source);
        this.acceptance = acceptance;
    }

    public AcceptanceDTO getAcceptance() {
        return acceptance;
    }
}
