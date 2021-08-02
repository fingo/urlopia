package info.fingo.urlopia.user;

import info.fingo.urlopia.config.persistance.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends BaseRepository<User>, JpaRepository<User, Long> {

    Optional<User> findFirstByMail(String mail);

    Optional<User> findFirstByPrincipalName(String principalName);

    Optional<User> findFirstByAdName(String adName);

    @Query("SELECT u.principalName FROM User u")
    List<String> findAllPrincipalNames();

    @Query("SELECT u FROM User u WHERE u.admin = true")
    List<User> findAdmins();

}
