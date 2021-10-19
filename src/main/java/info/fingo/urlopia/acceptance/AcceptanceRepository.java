package info.fingo.urlopia.acceptance;

import info.fingo.urlopia.config.persistance.BaseRepository;
import info.fingo.urlopia.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AcceptanceRepository extends BaseRepository<Acceptance>, JpaRepository<Acceptance, Long> {

    List<Acceptance> findByRequestId(long requestId);

    @Query("""
            SELECT CASE WHEN COUNT(acc) > 0 THEN true ELSE false END
            FROM Acceptance acc
            WHERE acc.leader = :id
            AND acc.status = 'PENDING'
            """)
    Boolean checkIsExistActiveAcceptanceByLeaderId(@Param("id") User user);

}
