package info.fingo.urlopia.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class UserServiceX {

    @Autowired
    private final UserRepository userRepository;

    public UserServiceX(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserExcerptProjection> get() {
        return userRepository.findAllByOrderByLastName();
    }

    public User get(Long userId) {
        return userRepository.getOne(userId);
    }

    public User get(String userMail) {
        return userRepository.findFirstByMail(userMail);
    }

    // *** ACTIONS ***

    public void setLanguage(Long userId, String language) {
        User user = userRepository.findOne(userId);
        user.setLang(language);
        userRepository.save(user);
    }

    public boolean isEC(Long userId) {
        User user = userRepository.findOne(userId);
        return user.getEc();
    }

    public void setWorkTime(Long userId, String workTimeString) {
        Float workTime = 8f * Arrays.stream(workTimeString.split("/")) // 8 hours lasts full-time
                .map(Float::parseFloat)
                .reduce((a, b) -> a / b)
                .orElse(1f);    // TODO: thing about throwing runtime exception
        User user = userRepository.findOne(userId);
        user.setWorkTime(workTime);
        userRepository.save(user);
    }

}
