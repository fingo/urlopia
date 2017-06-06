package info.fingo.urlopia.request;

import info.fingo.urlopia.request.acceptance.AcceptanceService;
import info.fingo.urlopia.user.UserDTO;
import info.fingo.urlopia.user.UserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Tomasz Urbas
 */

@Component
public class RequestFactory {

    @Autowired
    UserFactory userFactory;

    @Autowired
    AcceptanceService acceptanceService;

    public RequestDTO create(Request requestDB) {
        long id = requestDB.getId();
        LocalDateTime created = requestDB.getCreated();
        LocalDateTime modified = requestDB.getModified();
        UserDTO requester = userFactory.create(requestDB.getRequester());
        LocalDate startDate = requestDB.getStartDate();
        LocalDate endDate = requestDB.getEndDate();
        Request.Type type = requestDB.getType();
        Request.TypeInfo typeInfo = requestDB.getTypeInfo();
        Request.Status status = requestDB.getStatus();

        return new RequestDTO(id, created, modified, requester, startDate, endDate, type, typeInfo, status);
    }
}
