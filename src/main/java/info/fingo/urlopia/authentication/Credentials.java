package info.fingo.urlopia.authentication;

/**
 * @author Jakub Licznerski
 *         Created on 27.07.2016.
 */
public class Credentials {
    private String mail;
    private String password;

    public Credentials() {

    }

    public Credentials(String header) {
        String [] split = header.split(" ");
        mail = split[0];
        password = split[1];
    }

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
