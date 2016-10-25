package info.fingo.urlopia.user;

import info.fingo.urlopia.ad.ActiveDirectory;
import info.fingo.urlopia.ad.LocalTeam;
import info.fingo.urlopia.ad.LocalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Tomasz Pilarczyk
 */
@Service
@Transactional
public class EmployeeService {

    @Autowired
    public ActiveDirectory activeDirectory;

    public List<LocalUser> getEmployees() {

        return activeDirectory.getUsers();
    }

    public List<LocalUser> getUsersFromTeam(LocalTeam team) {

        return activeDirectory.getUsersFromTeam(team);
    }


    public List<LocalTeam> getTeams() {

        return activeDirectory.getTeams();
    }

}
