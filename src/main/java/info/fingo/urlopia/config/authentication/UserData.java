package info.fingo.urlopia.config.authentication;

import info.fingo.urlopia.user.User;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class UserData {

    private long userId;
    private Set<String> roles;
    private String name;
    private String surname;
    private String mail;
    private String language;
    private Set<Map<String, String>> teams;
    private int employmentYear;
    private boolean isEc;

    private UserData() {
    }

    public static UserData from(User user,
                                Set<Map<String, String>> teams,
                                Set<String> roles) {
        var userData = new UserData();
        userData.setUserId(user.getId());
        userData.setName(user.getFirstName());
        userData.setSurname(user.getLastName());
        userData.setMail(user.getMail());
        userData.setLanguage(user.getLang());
        userData.setTeams(teams);
        userData.setEc(user.getEc());
        userData.setRoles(roles);
        return userData;
    }
}

