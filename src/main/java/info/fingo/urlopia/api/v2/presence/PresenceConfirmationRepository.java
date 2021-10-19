package info.fingo.urlopia.api.v2.presence;

import info.fingo.urlopia.config.persistance.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PresenceConfirmationRepository extends BaseRepository<PresenceConfirmation>,
                                                        JpaRepository<PresenceConfirmation, PresenceConfirmationId> {
    Optional<PresenceConfirmation> findTopByPresenceConfirmationIdUserIdOrderByPresenceConfirmationIdDateAsc(Long userId);
}
