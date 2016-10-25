package info.fingo.urlopia.user;

import info.fingo.urlopia.ad.ActiveDirectory;
import info.fingo.urlopia.ad.LocalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Tomasz Urbas
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActiveDirectory activeDirectory;

    @Autowired
    private UserFactory userFactory;

    public UserDTO getUser(Long id) {
        User userDB = userRepository.findOne(id);

        UserDTO user = null;
        if (userDB != null) {
            user = userFactory.create(userDB);
        }

        return user;
    }

    public UserDTO getUser(String mail) {
        User userDB = userRepository.findFirstByMail(mail);

        UserDTO user = null;
        if (userDB != null) {
            user = userFactory.create(userDB);
        }

        return user;
    }

    public List<UserDTO> getUsers() {
        List<User> usersDB = userRepository.findAll();

        List<UserDTO> usersDTO = new ArrayList<>();
        usersDB.stream().filter(userDB -> userDB != null).forEach(userDB -> {
            UserDTO userDTO = userFactory.create(userDB);
            usersDTO.add(userDTO);
        });

        return usersDTO;
    }

    public void setLanguage(long userId, String language) {
        User userDB = userRepository.findOne(userId);
        userDB.setLang(language);
    }

    public void synchronize() {
        // getting users from AD and DB
        List<LocalUser> adUsers = activeDirectory.getUsers();
        List<User> dbUsers = userRepository.findAll();

        // sorting
        Collections.sort(adUsers, (u1, u2) -> u1.getPrincipalName().compareTo(u2.getPrincipalName()));
        Collections.sort(dbUsers, (u1, u2) -> u1.getMail().compareTo(u2.getMail()));

        // synchronize users from AD to DB
        int adId = 0;
        int bdId = 0;

        while (adId < adUsers.size() && bdId < dbUsers.size() + 1) {
            LocalUser adUser = adUsers.get(adId);

            if (adUser.getPrincipalName().isEmpty()) {   // if exists user without mail in AD
                adId++;
                continue;
            }

            if (bdId >= dbUsers.size()) {   // if there is no more users in DB
                adId++;
                userRepository.save(new User(adUser.getPrincipalName()));
            } else {
                User bdUser = dbUsers.get(bdId);

                if (adUser.getPrincipalName().equals(bdUser.getMail())) { // if this user exists in AD and DB
                    adId++;
                    bdId++;
                } else if (adUser.getPrincipalName().compareTo(bdUser.getMail()) > 0) {  // if DB user don't exists in AD
                    bdId++;
                    bdUser.setActive(false);
                    userRepository.save(bdUser);
                } else if (adUser.getPrincipalName().compareTo(bdUser.getMail()) < 0) {  // if AD user don't exists in DB
                    adId++;
                    userRepository.save(new User(adUser.getPrincipalName()));
                }
            }
        }
    }

    public List<UserDTO> getAdmins() {
        List<User> allUsers = userRepository.findAll();
        List<UserDTO> admins = new LinkedList<>();

        allUsers.stream()
                .filter(User::isAdmin)
                .forEach(user -> {
                    UserDTO admin = userFactory.create(user);
                    admins.add(admin);
                });
        return admins;
    }

    public void setAdmin(String adminMail) {
        User user = userRepository.findFirstByMail(adminMail);
        user.setAdmin(true);
        userRepository.save(user);
    }
}
