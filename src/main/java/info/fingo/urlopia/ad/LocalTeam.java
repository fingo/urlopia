package info.fingo.urlopia.ad;

/**
 * @author Tomasz Urbas
 */
public class LocalTeam {
    private String name;
    private String fullName;
    private LocalUser leader;

    LocalTeam(String name, LocalUser leader) {
        this.fullName = name;
        this.leader = leader;

        int begin = 3;  // every full name of group is beginning with "CN=..."
        int end = fullName.contains(ActiveDirectory.TEAM_IDENTIFIER) ?
                fullName.indexOf(ActiveDirectory.TEAM_IDENTIFIER) : fullName.indexOf(',');
        this.name = fullName.substring(begin, end);
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalUser getLeader() {
        return leader;
    }

    public void setLeader(LocalUser leader) {
        this.leader = leader;
    }
}
