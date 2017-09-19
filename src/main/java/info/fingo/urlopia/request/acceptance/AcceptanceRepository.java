package info.fingo.urlopia.request.acceptance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcceptanceRepository extends JpaRepository<Acceptance, Long> {

    Page<AcceptanceExcerptProjection> findByLeaderId(long leaderId, Pageable pageable);

    List<Acceptance> findByRequestId(long requestId);

}
