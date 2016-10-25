package info.fingo.urlopia.request;

import info.fingo.urlopia.authentication.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Tomasz Urbas
 */
@RestController
public class RequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private AcceptanceService acceptanceService;

    @RolesAllowed("ROLES_WORKER")
    @RequestMapping(value = "/api/request/worker", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RequestResponse> getRequestsFromWorker(HttpServletRequest httpRequest, @RequestParam(required = false) Long time) {
        Long userId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);

        List<RequestDTO> requests;
        if (time == null) {
            requests = requestService.getRequestsFromWorker(userId);
        } else {
            Timestamp timestamp = new Timestamp(time);
            LocalDateTime lastUpdate = timestamp.toLocalDateTime();

            requests = requestService.getRequestsFromWorker(userId, lastUpdate);
        }

        Comparator<RequestDTO> comparator = (r1, r2) -> r2.getStartDate().compareTo(r1.getStartDate());
        return requests.stream()
                .sorted(comparator)
                .map(r -> new RequestResponse(r, acceptanceService.getAcceptancesFromRequest(r.getId())))
                .collect(Collectors.toList());
    }

    @RolesAllowed({"ROLES_LEADER", "ROLES_ADMIN"})
    @RequestMapping(value = "/api/request/leader", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RequestResponse> getRequestsFromLeader(HttpServletRequest httpRequest, @RequestParam(required = false) Long time) {
        Long userId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);

        List<AcceptanceDTO> acceptances;
        if (time == null) {
            acceptances = acceptanceService.getAcceptancesFromLeader(userId);
        } else {
            Timestamp timestamp = new Timestamp(time);
            LocalDateTime lastUpdate = timestamp.toLocalDateTime();

            acceptances = acceptanceService.getAcceptancesFromLeader(userId, lastUpdate);
        }

        Comparator<AcceptanceDTO> comparator = (r1, r2) -> r2.getRequest().getStartDate().compareTo(r1.getRequest().getStartDate());
        return acceptances.stream()
                .sorted(comparator)
                .map(RequestResponse::new)
                .collect(Collectors.toList());
    }

    @RolesAllowed("ROLES_ADMIN")
    @RequestMapping(value = "/api/request/admin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RequestResponse> getRequestsFromAdmin(@RequestParam(required = false) Long time) {
        List<RequestDTO> requests;
        if (time == null) {
            requests = requestService.getRequestsFromAdmin();
        } else {
            Timestamp timestamp = new Timestamp(time);
            LocalDateTime lastUpdate = timestamp.toLocalDateTime();

            requests = requestService.getRequestsFromAdmin(lastUpdate);
        }

        Comparator<RequestDTO> comparator = (r1, r2) -> r2.getCreated().compareTo(r1.getCreated());
        return requests.stream()
                .sorted(comparator)
                .map(r -> new RequestResponse(r, acceptanceService.getAcceptancesFromRequest(r.getId())))
                .collect(Collectors.toList());
    }

    @RolesAllowed({"ROLES_ADMIN"})
    @RequestMapping(value = "/api/request/action", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> requestAction(HttpServletRequest httpRequest, @RequestBody Map<String, String> data) {
        long userId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);
        long requestId = Long.parseLong(data.get("requestId"));
        String action = data.get("action");

        Map<String, Object> map = new HashMap<>();

        if ("accept".equals(action) && !requestService.isValidRequest(requestId)) {
            requestService.reject(requestId, userId);
            map.put("value", false);
            return map;
        }

        if ("accept".equals(action)) {
            requestService.accept(requestId, userId);
        } else if ("reject".equals(action)) {
            requestService.reject(requestId, userId);
        } else if ("cancel".equals(action)) {
            requestService.cancel(requestId);
        }
        map.put("value", true);
        return map;
    }

    @RolesAllowed({"ROLES_LEADER", "ROLES_WORKER"})
    @RequestMapping(value = "/api/modal", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean addRequest(HttpServletRequest request, @RequestBody Map<String, Object> dataObj) {
        long requesterId = (long) request.getAttribute("userId");
        LocalDate startDate = OffsetDateTime.parse((String) dataObj.get("startDate")).toLocalDate();
        LocalDate endDate = OffsetDateTime.parse((String) dataObj.get("endDate")).toLocalDate();
        int type = (int) dataObj.get("type");

        return requestService.insert(requesterId, startDate, endDate, null, type);

    }

    @RolesAllowed({"ROLES_WORKER"})
    @RequestMapping(value = "/api/request/cancelRequest", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void cancelRequest(@RequestBody Map<String, String> data) {
        long requestId = Long.valueOf(data.get("requestID"));
        String action = data.get("action");

        if ("cancelRequest".equals(action)) {
            requestService.cancel(requestId);
        }
    }
}
