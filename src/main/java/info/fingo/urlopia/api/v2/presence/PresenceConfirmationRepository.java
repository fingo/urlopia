package info.fingo.urlopia.api.v2.presence;

import info.fingo.urlopia.config.persistance.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface PresenceConfirmationRepository extends BaseRepository<PresenceConfirmation>,
                                                        JpaRepository<PresenceConfirmation, PresenceConfirmationId> {
    Optional<PresenceConfirmation> findTopByPresenceConfirmationIdUserIdOrderByPresenceConfirmationIdDateAsc(Long userId);

    @Query(value = """
        SELECT *
        FROM presence_confirmations
        WHERE user_id = :id AND date >= :startDate
        ORDER BY date ASC
        LIMIT 1""",
            nativeQuery = true)
    Optional<PresenceConfirmation> findFirstUserConfirmationFromStartDate (@Param("id") Long userId,
                                                                           @Param("startDate") LocalDate startDate);

}
