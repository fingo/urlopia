package info.fingo.urlopia.report;

import info.fingo.urlopia.user.User;
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
import java.util.HashMap;
import java.util.Map;

@Controller
public class ReportController {

    private final UserService userService;

    private final ReportView reportView;

    @Autowired
    public ReportController(UserService userService, ReportView reportView) {
        this.userService = userService;
        this.reportView = reportView;
    }

    @RolesAllowed("ROLES_ADMIN")
    @RequestMapping(path = "/report", method = RequestMethod.GET)
    public ModelAndView getExcelReport(HttpServletResponse httpResponse, @RequestParam Long id) {

        Integer year = LocalDate.now().getYear();
        User user = userService.get(id);

        // model data
        Map<String, Object> model = new HashMap<>();
        model.put("currentYear", year);
        model.put("userId", id);

        // set the file name
        String fileName = "ewidencja_czasu_pracy_" + year + "_" + user.getFirstName() + user.getLastName();
        httpResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        httpResponse.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xlsx");

        return new ModelAndView(reportView, model);
    }
}
