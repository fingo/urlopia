package info.fingo.urlopia.request;

import info.fingo.urlopia.authentication.AuthInterceptor;
import info.fingo.urlopia.history.DurationCalculator;
import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.request.acceptance.AcceptanceDTO;
import info.fingo.urlopia.request.acceptance.AcceptanceService;
import info.fingo.urlopia.user.UserDTO;
import info.fingo.urlopia.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.OffsetDateTime;
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

    @Autowired
    private UserService userService;

    @Autowired
    private HolidayService holidayService;


    @RolesAllowed("ROLES_WORKER")
    @RequestMapping(value = "/api/request/worker", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRequestsFromWorkerNew(HttpServletRequest httpRequest, Pageable pageable) {
        Long userId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);
        Page<RequestDTO> requestsPage = requestService.getRequestsFromWorker(userId, pageable);
        List<RequestDTO> requests = requestsPage.getContent();
        List<RequestResponse> response = requests.stream()
                .map(r -> new RequestResponse(r, acceptanceService.getAcceptancesFromRequest(r.getId()),
                        DurationCalculator.calculateDays(r, holidayService)))
                .collect(Collectors.toList());
        Page<RequestResponse> responsePage = new PageImpl<>(response, pageable, requestsPage.getTotalElements());
        return ResponseEntity.ok(responsePage);
    }

    @RolesAllowed({"ROLES_LEADER", "ROLES_ADMIN"})
    @RequestMapping(value = "/api/request/leader", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRequestsFromLeader(HttpServletRequest httpRequest, Pageable pageable) {
        Long userId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);
        Page<AcceptanceDTO> acceptancesPage = acceptanceService.getAcceptancesFromLeader(userId, pageable);
        List<AcceptanceDTO> acceptances = acceptancesPage.getContent();
        List<RequestResponse> response = acceptances.stream()
                .map(a -> new RequestResponse(a, DurationCalculator.calculateDays(a.getRequest(), holidayService)))
                .collect(Collectors.toList());
        Page<RequestResponse> responsePage = new PageImpl<>(response, pageable, acceptancesPage.getTotalElements());
        return ResponseEntity.ok(responsePage);
    }

    @RolesAllowed("ROLES_ADMIN")
    @RequestMapping(value = "/api/request/admin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRequestsFromAdminTest(Pageable pageable) {
        Page<RequestDTO> requestsPage = requestService.getRequestsFromAdmin(pageable);
        List<RequestDTO> requests = requestsPage.getContent();
        List<RequestResponse> response = requests.stream()
                .map(r -> new RequestResponse(r, acceptanceService.getAcceptancesFromRequest(r.getId()),
                        DurationCalculator.calculateDays(r, holidayService)))
                .collect(Collectors.toList());
        Page<RequestResponse> responsePage = new PageImpl<>(response, pageable, requestsPage.getTotalElements());
        return ResponseEntity.ok(responsePage);
    }

    @RolesAllowed({"ROLES_ADMIN"})
    @RequestMapping(value = "/api/request/action", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> requestAction(HttpServletRequest httpRequest, @RequestBody Map<String, String> data) {
        long userId = (Long) httpRequest.getAttribute(AuthInterceptor.USER_ID_ATTRIBUTE);
        long requestId = Long.parseLong(data.get("requestId"));
        String action = data.get("action");

        Map<String, Object> map = new HashMap<>();

        if ("accept".equals(action) && !requestService.isValidRequestByRequest(requestId)) {
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
    @RequestMapping(value = "/api/modal", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public String addRequest(HttpServletRequest request, @RequestBody Map<String, Object> dataObj) {
        long requesterId = (long) request.getAttribute("userId");
        UserDTO requester = userService.getUser(requesterId);
        LocalDate startDate = OffsetDateTime.parse((String) dataObj.get("startDate")).toLocalDate();
        LocalDate endDate = OffsetDateTime.parse((String) dataObj.get("endDate")).toLocalDate();
        int typeIndex = (int) dataObj.get("type");

        // get occasional type of from int value
        // TODO: delete the integer front type index
        Request.Type type = Request.Type.NORMAL;
        Request.OccasionalType occasionalType = Request.OccasionalType.WRONG;
        if(typeIndex != 0) {
            type = Request.Type.OCCASIONAL;
            for (Request.OccasionalType tempOccasionalType : Request.OccasionalType.values()) {
                if (tempOccasionalType.getIndex() == typeIndex) {
                    occasionalType = tempOccasionalType;
                    break;
                }
            }
        }

        boolean success;
        try {
            if (type == Request.Type.NORMAL) {
                success = requestService.insertNormal(requester, startDate, endDate);
            } else {
                success = requestService.insertOccasional(requester, startDate, occasionalType);
            }
        } catch (NotEnoughDaysException e) {
            return e.getCode();
        } catch (RequestOverlappingException e) {
            return e.getCode();
        }

        if(success) {
            return "SUCCESS";
        } else {
            return "FAIL";
        }
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
