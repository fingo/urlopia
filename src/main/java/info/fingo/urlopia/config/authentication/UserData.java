package info.fingo.urlopia.config.authentication;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserData {

    private final long userId;
    protected List<String> userRoles;
    private String name;
    private String surname;
    private String mail;
    private String language;
    private String token;
    private Set<Map<String, String>> teams;

    UserData(long userId, 
             List<String> userRoles) {
        this.userId = userId;
        this.userRoles = userRoles;
    }

    public long getUserId() {
        return userId;
    }

    public List<String> getUserRoles() {
        return userRoles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Set<Map<String, String>> getTeams() {
        return teams;
    }

    public void setTeams(Set<Map<String, String>> teams) {
        this.teams = teams;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }
}

