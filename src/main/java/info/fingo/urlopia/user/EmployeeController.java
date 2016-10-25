package info.fingo.urlopia.user;

import info.fingo.urlopia.ad.LocalTeam;
import info.fingo.urlopia.ad.LocalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.naming.NamingException;
import java.util.List;

/**
 * @author Tomasz Pilarczyk
 */
@RestController
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    @RolesAllowed("ROLES_ADMIN")
    @RequestMapping(value = "/api/request/employees", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<LocalUser> getEmployees() throws NamingException {
        return employeeService.getEmployees();

    }

    @RolesAllowed("ROLES_ADMIN")
    @RequestMapping(value = "/api/request/teams", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<LocalTeam> getTeams() throws NamingException {
        List<LocalTeam> localTeams = employeeService.getTeams();

        return localTeams;
    }

}
