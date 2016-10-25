package info.fingo.urlopia.ad;

import java.util.List;

/**
 * @author Tomasz Urbas
 */
public class LocalUser {
    private String principalName = "";
    private String mail = "";
    private String name = "";
    private String surname = "";
    private boolean leader = false;
    private boolean B2B = false;
    private boolean EC = false;
    private boolean urlopiaTeam = false;

    private List<LocalTeam> teams;

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
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

    public String getFullName() {
        return name + ' ' + surname;
    }

    public boolean isLeader() {
        return leader;
    }

    public void setLeader(boolean leader) {
        this.leader = leader;
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

    public boolean isUrlopiaTeam() {
        return urlopiaTeam;
    }

    public void setUrlopiaTeam(boolean urlopiaTeam) {
        this.urlopiaTeam = urlopiaTeam;
    }

    public List<LocalTeam> getTeams() {
        return teams;
    }

    public void setTeams(List<LocalTeam> teams) {
        this.teams = teams;
    }
}
