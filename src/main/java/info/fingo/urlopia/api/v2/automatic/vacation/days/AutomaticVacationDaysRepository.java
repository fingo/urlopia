package info.fingo.urlopia.api.v2.automatic.vacation.days;

import info.fingo.urlopia.api.v2.automatic.vacation.days.model.AutomaticVacationDay;
import info.fingo.urlopia.config.persistance.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AutomaticVacationDaysRepository extends BaseRepository<AutomaticVacationDay>, JpaRepository<AutomaticVacationDay, Long> {

    Optional<AutomaticVacationDay> findByUserId(Long userId);

}
