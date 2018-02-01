package info.fingo.urlopia.user;

import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.team.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private Float FULL_TIME_IN_HOURS = 8f; // TODO: Think about moving it somewhere

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserExcerptProjection> get(Filter filter, Sort sort) {
        return userRepository.findAll(filter, sort, UserExcerptProjection.class);
    }

    public User get(Long userId) {
        return userRepository.findOne(userId);
    }

    public User get(String userMail) {
        return userRepository.findFirstByMail(userMail);
    }

    // *** ACTIONS ***

    void setLanguage(Long userId, String language) {
        User user = userRepository.findOne(userId);
        user.setLang(language);
        userRepository.save(user);
    }

    boolean isEC(Long userId) {
        User user = userRepository.findOne(userId);
        return user.getEc();
    }

    void setWorkTime(Long userId, String workTimeString) {
        Float workTime = FULL_TIME_IN_HOURS * Arrays.stream(workTimeString.split("/"))
                .map(Float::parseFloat)
                .reduce((a, b) -> a / b)
                .orElse(1f);    // TODO: thing about throwing runtime exception
        User user = userRepository.findOne(userId);
        user.setWorkTime(workTime);
        userRepository.save(user);
    }

    public Set<User> getAdmins() {
        List<User> admins = userRepository.findAdmins();
        return new HashSet<>(admins);
    }

    public Set<User> getLeaders(Long userId) {
        User user = userRepository.findOne(userId);
        return user.getTeams().stream()
                .map(Team::getLeader)
                .collect(Collectors.toSet());
    }

}
