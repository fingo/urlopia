package info.fingo.urlopia.acceptance;

import info.fingo.urlopia.config.persistance.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcceptanceRepository extends BaseRepository<Acceptance>, JpaRepository<Acceptance, Long> {

    List<Acceptance> findByRequestId(long requestId);

}
