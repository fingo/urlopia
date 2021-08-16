package info.fingo.urlopia.api.v2.presence;

import info.fingo.urlopia.config.persistance.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresenceConfirmationRepository extends BaseRepository<PresenceConfirmation>,
                                                        JpaRepository<PresenceConfirmation, PresenceConfirmationId> {

}
