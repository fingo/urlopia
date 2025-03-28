package info.fingo.urlopia.user;

import info.fingo.urlopia.UrlopiaApplication;
import info.fingo.urlopia.team.Team;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "users_id_seq")
    private Long id;

    @Column(nullable = false, unique = true)
    private String accountName;

    @Column(nullable = false)
    private String principalName;

    @Column(nullable = false)
    private String adName;

    @Column(nullable = false)
    private String mail;

    private String firstName;

    private String lastName;

    @Column(nullable = false)
    private Boolean admin = false;

    @Column(nullable = false)
    private Boolean leader = false;

    @Column(nullable = false)
    private Boolean b2b = false;

    @Column(nullable = false)
    private Boolean ec = true;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private String lang = "pl";

    @Column(nullable = false)
    private Float workTime = 8.0f;

    @ManyToMany(mappedBy = "users")
    private Set<Team> teams;

    public User() {
        // no-args constructor is needed by *hibernate*
    }

    public User(String mail) {
        this.mail = mail;
        this.admin = false;
        this.active = true;
        this.lang = UrlopiaApplication.DEFAULT_LANGUAGE;
        this.workTime = 8f;
    }

    public Long getId() {
        return id;
    }

    public String getMail() {
        return mail;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public void setLeader(Boolean leader) {
        this.leader = leader;
    }

    public void setB2b(Boolean b2b) {
        this.b2b = b2b;
    }

    public void setEc(Boolean ec) {
        this.ec = ec;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void setWorkTime(Float workTime) {
        this.workTime = workTime;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public Boolean getLeader() {
        return leader;
    }

    public Boolean getB2b() {
        return b2b;
    }

    public Boolean getEc() {
        return ec;
    }

    public Boolean getActive() {
        return active;
    }

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public Set<Team> getTeams() {
        return Set.copyOf(teams);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || super.getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
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
