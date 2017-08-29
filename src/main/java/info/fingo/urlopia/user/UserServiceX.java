package info.fingo.urlopia.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceX {

    @Autowired
    private final UserRepository userRepository;

    public UserServiceX(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserExcerptProjection> getAll() {
        return userRepository.findAllByOrderByLastName();
    }

    public User get(Long userId) {
        return userRepository.getOne(userId);
    }

    public User get(String userMail) {
        return userRepository.findFirstByMail(userMail);
    }

    // CUSTOM ACTIONS
    public List<User> getAdmins() {
        return userRepository.findAllByAdminTrue();
    }

    public void setLanguage(Long userId, String language) {
        User user = userRepository.findOne(userId);
        user.setLang(language);
        userRepository.save(user);
    }

    public boolean isEC(Long userId) {
        User user = userRepository.findOne(userId);
        return user.getEc();
    }

    public void setAdminByMail(String userMail) {
        User user = userRepository.findFirstByMail(userMail);
        user.setAdmin(true);
        userRepository.save(user);
    }

    public void setWorkTimeByMail(String userMail, float workTime) {
        User user = userRepository.findFirstByMail(userMail);
        user.setWorkTime(workTime);
        userRepository.save(user);
    }

}
