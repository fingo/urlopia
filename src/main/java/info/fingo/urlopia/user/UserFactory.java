package info.fingo.urlopia.user;

import info.fingo.urlopia.ad.ActiveDirectory;
import info.fingo.urlopia.ad.LocalTeam;
import info.fingo.urlopia.ad.LocalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Tomasz Urbas
 */

@Component
public class UserFactory {

    @Autowired
    ActiveDirectory activeDirectory;

    @Value("${ad.user.master.leader}")
    String masterLeader;

    public UserDTO create(User userDB) {
        long id = userDB.getId();
        String mail = userDB.getMail();

        Optional<LocalUser> userAD = activeDirectory.getUser(mail);
        if (userAD.isPresent()) {
            boolean admin = userDB.isAdmin();
            String lang = userDB.getLang();
            String firstName = userAD.get().getName();
            String lastName = userAD.get().getSurname();
            boolean leader = masterLeader.equals(userAD.get().getPrincipalName()) || userAD.get().isLeader();
            boolean B2B = userAD.get().isB2B();
            boolean EC = userAD.get().isEC();
            boolean urlopiaTeam = userAD.get().isUrlopiaTeam();
            float workTime = userDB.getWorkTime();
            List<LocalTeam> teams = userAD.get().getTeams();
            mail = userAD.get().getMail();
            String principalName = userAD.get().getPrincipalName();

            Set<UserDTO.Role> roles = new HashSet<>();
            roles.add(UserDTO.Role.WORKER);
            if (leader) {
                roles.add(UserDTO.Role.LEADER);
            }
            if (admin) {
                roles.add(UserDTO.Role.ADMIN);
            }

            return new UserDTO(id, principalName, mail, firstName, lastName, admin, leader, B2B, EC, urlopiaTeam, lang, workTime, teams, roles);
        } else {
            return new UserDTO(id, mail);
        }
    }
}
