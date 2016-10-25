package info.fingo.urlopia.request;

import info.fingo.urlopia.user.UserDTO;
import info.fingo.urlopia.user.UserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Tomasz Urbas
 */

@Component
public class AcceptanceFactory {

    @Autowired
    UserFactory userFactory;

    @Autowired
    RequestFactory requestFactory;

    public AcceptanceDTO create(Acceptance acceptanceDB) {
        long id = acceptanceDB.getId();
        UserDTO leader = userFactory.create(acceptanceDB.getLeader());
        RequestDTO request = requestFactory.create(acceptanceDB.getRequest());

        if (acceptanceDB.getDecider() != null) {
            UserDTO decider = userFactory.create(acceptanceDB.getDecider());
            boolean accepted = acceptanceDB.isAccepted();

            return new AcceptanceDTO(id, request, leader, decider, accepted);
        } else {
            return new AcceptanceDTO(id, request, leader);
        }
    }
}
