package info.fingo.urlopia.user;

import info.fingo.urlopia.config.persistance.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends BaseRepository<User>, JpaRepository<User, Long> {

    User findFirstByMail(String mail);

    User findFirstByPrincipalName(String principalName);

    User findFirstByAdName(String adName);

    @Query("SELECT u.principalName FROM User u")
    List<String> findAllPrincipalNames();

    @Query("SELECT u FROM User u WHERE u.admin = true")
    List<User> findAdmins();

}
