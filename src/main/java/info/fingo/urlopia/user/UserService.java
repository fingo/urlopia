package info.fingo.urlopia.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
