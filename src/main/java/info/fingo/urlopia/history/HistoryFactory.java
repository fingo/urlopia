package info.fingo.urlopia.history;

import info.fingo.urlopia.request.AcceptanceDTO;
import info.fingo.urlopia.request.AcceptanceService;
import info.fingo.urlopia.request.RequestDTO;
import info.fingo.urlopia.request.RequestFactory;
import info.fingo.urlopia.user.UserDTO;
import info.fingo.urlopia.user.UserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Tomasz Pilarczyk
 */
@Component
public class HistoryFactory {

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private RequestFactory requestFactory;

    @Autowired
    private AcceptanceService acceptanceService;

    public HistoryDTO create(History history) {
        long id = history.getId();
        float hours = history.getHours();
        LocalDateTime created = history.getCreated();
        RequestDTO request;
        List<AcceptanceDTO> acceptances;
        UserDTO decider;
        if (history.getDecider() != null) {
            decider = userFactory.create(history.getDecider());
        } else {
            decider = null;
        }
        if (history.getRequest() != null) {
            request = requestFactory.create(history.getRequest());
            acceptances = acceptanceService.getAcceptancesFromRequest(request.getId());
        } else {
            request = null;
            acceptances = null;
        }
        UserDTO user = userFactory.create(history.getUser());
        String comment = history.getComment();

        return new HistoryDTO(id, hours, created, decider, request, user, acceptances, comment);
    }
}
