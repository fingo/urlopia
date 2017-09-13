package info.fingo.urlopia.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<Request> findAll(Pageable pageable);

    Page<Request> findByRequesterId(Long id, Pageable pageable);

    Integer countByRequesterIdAndModifiedAfter(Long id, LocalDateTime time);

    Integer countByModifiedAfter(LocalDateTime time);
}
