package info.fingo.urlopia.events;

import info.fingo.urlopia.request.RequestDTO;
import info.fingo.urlopia.user.UserDTO;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDate;

/**
 * @author Tomasz Pilarczyk
 */
public class OccasionalInfoEvent extends ApplicationEvent {

    private RequestDTO request;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */

    public OccasionalInfoEvent(Object source, RequestDTO request) {
        super(source);
        this.request = request;
    }

    public RequestDTO getRequest() {
        return request;
    }
}
