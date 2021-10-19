package info.fingo.urlopia.config.authentication;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
public class UserData {

    private final long userId;
    protected List<String> userRoles;
    private String name;
    private String surname;
    private String mail;
    private String language;
    private String token;
    private Set<Map<String, String>> teams;
    private int employmentYear;
    private boolean isEc;

    UserData(long userId, List<String> userRoles) {
        this.userId = userId;
        this.userRoles = userRoles;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setTeams(Set<Map<String, String>> teams) {
        this.teams = teams;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setEmploymentYear(int employmentYear) {
        this.employmentYear = employmentYear;
    }

    public void setIsEc(boolean isEc) {
        this.isEc = isEc;
    }
}

