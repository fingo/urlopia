package info.fingo.urlopia.user;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for {@link User} entity.
 *
 * @author Mateusz Wiśniewski
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findFirstByMail(String mail);
}
