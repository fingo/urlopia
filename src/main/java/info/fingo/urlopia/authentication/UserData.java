package info.fingo.urlopia.authentication;

import info.fingo.urlopia.ad.LocalTeam;
import info.fingo.urlopia.user.UserDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jakub Licznerski
 *         Created on 27.07.2016.
 */
//Server response after succesful login (we assume that we create UserData of existing user)
public class UserData {

    private long userId;
    private List<String> userRoles;
    private String name;
    private String surname;
    private String mail;
    private String language;
    private String token;
    private List<LocalTeam> teams;

    public UserData(UserDTO user, String token) {
        this.userId = user.getId();
        this.userRoles = user.getRoles().stream()
                .map(UserDTO.Role::toString)
                .collect(Collectors.toList());
        this.name = user.getFirstName();
        this.surname = user.getLastName();
        this.mail = user.getMail();
        this.language = user.getLang();
        this.token = token;
        this.teams = user.getTeams();
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<String> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<String> userRoles) {
        this.userRoles = userRoles;
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

    public List<LocalTeam> getTeams() {
        return teams;
    }

    public void setTeams(List<LocalTeam> teams) {
        this.teams = teams;
    }

    public String getLanguage() {
        return language;
    }
}

