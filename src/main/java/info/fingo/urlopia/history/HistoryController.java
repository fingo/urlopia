package info.fingo.urlopia.history;

import info.fingo.urlopia.user.UserDTO;
import info.fingo.urlopia.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Tomasz Pilarczyk
 */
@RestController
public class HistoryController {

    private final String [] WORK_TIMES = {"1", "7/8", "4/5", "3/4", "3/5", "1/2", "2/5", "1/4", "1/5", "1/8", "1/16"};

    @Autowired
    private UserService userService;

    @Autowired
    private HistoryService historyService;

    @RolesAllowed({"ROLES_LEADER", "ROLES_WORKER"})
    @RequestMapping(value = "/api/history", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public WorkTimeResponse getHolidaysPool(@RequestParam(required = false) Long userId, @RequestParam(required = false) String mail) {
        Float pool = historyService.getHolidaysPool(userId, mail);
        Float workTime;
        if (userId == null) {
            workTime = userService.getUser(mail).getWorkTime();
        } else {
            workTime = userService.getUser(userId).getWorkTime();
        }

        return new WorkTimeResponse(workTime, pool);
    }

    @RolesAllowed({"ROLES_LEADER", "ROLES_WORKER"})
    @RequestMapping(value = "/api/firstHistory", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getFirstHistoryYear(@RequestParam(required = false) Long userId, @RequestParam(required = false) String mail) {
        if (userId == null) {
            userId = userService.getUser(mail).getId();
        }
        Map<String, Object> map = new HashMap<>();
        List<HistoryDTO> histories = historyService.getHistories(userId);
        int year;
        if (!histories.isEmpty()) {
            year = histories.get(0).getCreated().getYear();
        } else {
            year = LocalDateTime.now().getYear();
        }

        map.put("year", year);
        return map;
    }

    @RolesAllowed({"ROLES_LEADER", "ROLES_WORKER"})
    @RequestMapping(value = "/api/workerHistory", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getHolidayHistories(@RequestParam(required = false) Long userId, @RequestParam(required = false) String mail, int year) {
        if (userId == null) {
            userId = userService.getUser(mail).getId();
        }
        List<HistoryDTO> histories = historyService.getHistoriesFromYear(userId, year);
        List<HistoryResponse> historyResponses = new ArrayList<>();

        Float workTime;
        workTime = userService.getUser(userId).getWorkTime();

        getResponses(histories, historyResponses);

        Map<String, Object> map = new HashMap<>();
        //sorting histories by id
        histories.sort((t1, t2) -> {
            if (t1.getId() == t2.getId())
                return 0;
            return t1.getId() < t2.getId() ? -1 : 1;
        });
        map.put("list", historyResponses);
        map.put("workTime", workTime);

        return map;

    }

    @RolesAllowed({"ROLES_ADMIN"})
    @RequestMapping(value = "/api/userHistory/recent", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getRecentHistoryFromUser(String userMail) {
        List<HistoryDTO> histories = historyService.getRecentHistories(userMail);
        List<HistoryResponse> historyResponses = new ArrayList<>();

        getResponses(histories, historyResponses);

        Map<String, Object> map = new HashMap<>();

        map.put("list", historyResponses);

        return map;
    }

    @RolesAllowed({"ROLES_ADMIN"})
    @RequestMapping(value = "/api/history", method = RequestMethod.POST)
    public HttpStatus addDays(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        String mail = (String) body.get("mail");
        Number hours = (Number) body.get("hours");
        String comment = (String) body.get("comment");
        HttpStatus status = HttpStatus.OK;

        UserDTO user = userService.getUser(mail);
        if (user == null)
            status = HttpStatus.BAD_REQUEST;
        else {
            historyService.addHistory(user.getId(), (long) request.getAttribute("userId"), hours.floatValue(), comment);
        }

        return status;
    }


    @RolesAllowed("ROLES_ADMIN")
    @RequestMapping(value ="/api/history/setWorkTime", method = RequestMethod.POST)
    public HttpStatus setWorkTime(@RequestBody Map<String, Object> data) {
        String workTime = (String)data.get("workTime");
        //check if the workTime provided is allowed
        if (!Arrays.asList(WORK_TIMES).contains(workTime))
            return HttpStatus.EXPECTATION_FAILED;

        String [] fraction = workTime.split("/");

        if(fraction.length == 1)
            userService.setWorkTime((String)data.get("mail"), Float.parseFloat(fraction[0]) * 8F);
        else
            userService.setWorkTime((String)data.get("mail"), Float.parseFloat(fraction[0])/Float.parseFloat(fraction[1]) * 8F);

        return HttpStatus.OK;
    }


    private void getResponses(List<HistoryDTO> histories, List<HistoryResponse> historyResponses) {
        for (int i = 0; i < histories.size(); i++) {
            if (i == 0) historyResponses.add(new HistoryResponse(histories.get(i), histories.get(i).getHours()));
            else {
                if (histories.get(i).getType() == 0) {
                    historyResponses.add(new HistoryResponse(histories.get(i), historyResponses.get(i - 1).getHoursLeft() + histories.get(i).getHours()));
                } else {
                    historyResponses.add(new HistoryResponse(histories.get(i), historyResponses.get(i - 1).getHoursLeft()));
                }

            }
        }
    }
}