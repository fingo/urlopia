package info.fingo.urlopia.user;

import info.fingo.urlopia.UrlopiaApplication;

import javax.persistence.*;

/**
 * UserDTO entity.
 *
 * @author Mateusz Wiśniewski
 */
@Entity
@Table(name = "Users")
public class User {

    @Id
    @SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "users_id_seq")
    private long id;

    @Column(nullable = false)
    private String mail;

    @Column(nullable = false)
    private boolean admin;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private String lang;

    @Column
    private float workTime;

    /**
     * Default constructor only exists for the sake of JPA
     */
    protected User() {
    }

    public User(String mail) {
        this.mail = mail;
        this.admin = false;
        this.active = true;
        this.lang = UrlopiaApplication.DEFAULT_LANGUAGE;
        this.workTime = 8;
    }

    public long getId() {
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
}
