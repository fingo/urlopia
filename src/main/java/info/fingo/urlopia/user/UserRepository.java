package info.fingo.urlopia.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findFirstByMail(String mail);

    User findFirstByPrincipalName(String principalName);

    User findFirstByAdName(String adName);

}
