package info.fingo.urlopia.team;

import info.fingo.urlopia.user.User;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "Teams")
public class Team {

    @Id
    private String name;

    @Column(nullable = false)
    private String adName;

    @ManyToOne
    private User leader;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Users_Teams",
            joinColumns = { @JoinColumn(name = "team_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") })
    private Set<User> users;

    public Team() {
        // no-args constructor is needed by *hibernate*
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public User getLeader() {
        return leader;
    }

    public void setLeader(User leader) {
        this.leader = leader;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
