package info.fingo.urlopia.user;

import info.fingo.urlopia.ad.LocalTeam;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Tomasz Urbas
 */
public class UserDTO implements Serializable {
    private long id;
    private String principalName;
    private String mail;
    private String firstName;
    private String lastName;
    private boolean admin;
    private boolean leader;
    private boolean B2B;
    private boolean EC;
    private String lang;
    private float workTime;
    private transient List<LocalTeam> teams;
    private Set<Role> roles;

    public UserDTO(long id, String mail) {
        this.id = id;
        this.mail = mail;
        this.teams = new LinkedList<>();
    }

    public UserDTO(long id, String principalName, String mail, String firstName, String lastName, boolean admin, boolean leader, boolean B2B, boolean EC, boolean urlopiaTeam, String lang, float workTime, List<LocalTeam> teams, Set<Role> roles) {
        this(id, mail);
        this.firstName = firstName;
        this.lastName = lastName;
        this.admin = admin;
        this.leader = leader;
        this.B2B = B2B;
        this.EC = EC;
        this.lang = lang;
        this.workTime = workTime;
        this.teams = teams;
        this.roles = roles;
        this.principalName = principalName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDTO userDTO = (UserDTO) o;

        if (id != userDTO.id) return false;
        if (admin != userDTO.admin) return false;
        if (leader != userDTO.leader) return false;
        if (B2B != userDTO.B2B) return false;
        if (EC != userDTO.EC) return false;
        if (mail != null ? !mail.equals(userDTO.mail) : userDTO.mail != null) return false;
        if (firstName != null ? !firstName.equals(userDTO.firstName) : userDTO.firstName != null) return false;
        if (lastName != null ? !lastName.equals(userDTO.lastName) : userDTO.lastName != null) return false;
        if (lang != null ? !lang.equals(userDTO.lang) : userDTO.lang != null) return false;
        if (teams != null ? !teams.equals(userDTO.teams) : userDTO.teams != null) return false;
        return roles != null ? roles.equals(userDTO.roles) : userDTO.roles == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (mail != null ? mail.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (admin ? 1 : 0);
        result = 31 * result + (leader ? 1 : 0);
        result = 31 * result + (B2B ? 1 : 0);
        result = 31 * result + (EC ? 1 : 0);
        result = 31 * result + (lang != null ? lang.hashCode() : 0);
        result = 31 * result + (teams != null ? teams.hashCode() : 0);
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        return result;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getName() {
        return this.firstName + " " + this.lastName;
    }

    public long getId() {
        return id;
    }

    public String getMail() {
        return mail;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isLeader() {
        return leader;
    }

    public boolean isB2B() {
        return B2B;
    }

    public void setB2B(boolean B2B) {
        this.B2B = B2B;
    }

    public boolean isEC() {
        return EC;
    }

    public void setEC(boolean EC) {
        this.EC = EC;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public float getWorkTime() {
        return workTime;
    }

    public void setWorkTime(float workTime) {
        this.workTime = workTime;
    }

    public List<LocalTeam> getTeams() {
        return teams;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public enum Role {
        WORKER("ROLES_WORKER"),
        LEADER("ROLES_LEADER"),
        ADMIN("ROLES_ADMIN");

        private final String name;

        Role(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
