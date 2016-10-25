package info.fingo.urlopia.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for {@link Request} entity.
 *
 * @author Mateusz Wiśniewski
 * @author Tomasz Urbas
 */
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequesterId(Long id);

    Integer countByRequesterIdAndModifiedAfter(Long id, LocalDateTime time);

    Integer countByModifiedAfter(LocalDateTime time);
}
