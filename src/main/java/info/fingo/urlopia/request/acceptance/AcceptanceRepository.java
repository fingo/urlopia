package info.fingo.urlopia.request.acceptance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for {@link Acceptance}.
 *
 * @author Tomasz Urbas
 */
public interface AcceptanceRepository extends JpaRepository<Acceptance, Long> {
    List<Acceptance> findByRequestId(long requestId);

    List<Acceptance> findByLeaderId(long leaderId);
    Page<Acceptance> findByLeaderId(long leaderId, Pageable pageable);

    Integer countByLeaderIdAndRequestModifiedAfter(Long leaderId, LocalDateTime time);
}
