package info.fingo.urlopia.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findFirstByMail(String mail);

    User findFirstByPrincipalName(String principalName);

    User findFirstByAdName(String adName);

    List<UserExcerptProjection> findAllByOrderByLastName();

    @Query("SELECT u.principalName FROM User u")
    List<String> findAllPrincipalNames();

}
