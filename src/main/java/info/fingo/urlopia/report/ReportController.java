package info.fingo.urlopia.report;

import info.fingo.urlopia.history.HistoryService;
import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.request.AcceptanceService;
import info.fingo.urlopia.request.RequestDTO;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.user.UserDTO;
import info.fingo.urlopia.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for .xlsx file creation.
 *
 * @author Mateusz Wiśniewski
 */
@Controller
public class ReportController {

    @Autowired
    private UserService userService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private AcceptanceService acceptanceService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ReportView reportView;

    @RolesAllowed("ROLES_ADMIN")
    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public ModelAndView getExcelReport(HttpServletResponse httpResponse, @RequestParam String mail) {

        Map<String, Object> model = new HashMap<>();

        LocalDateTime currentYear = LocalDateTime.of(LocalDate.now().getYear(), 1, 1, 0, 0);

        // get user info from services
        UserDTO userDTO = userService.getUser(mail);
        List<RequestDTO> requestDTO = requestService.getRequestsFromWorker(userDTO.getId(), currentYear);

        // model data
        model.put("userDTO", userDTO);
        model.put("requestDTO", requestDTO);
        model.put("holidayService", holidayService);
        model.put("historyService", historyService);
        model.put("acceptanceService", acceptanceService);
        model.put("currentYear", currentYear.getYear());

        // set the file name
        String fileName = "ewidencja_czasu_pracy_" + currentYear.getYear() + "_" + userDTO.getFirstName() + userDTO.getLastName();
        httpResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        httpResponse.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xlsx");

        return new ModelAndView(reportView, model);
    }
}
