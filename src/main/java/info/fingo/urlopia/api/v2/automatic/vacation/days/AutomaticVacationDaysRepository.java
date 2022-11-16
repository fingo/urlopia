package info.fingo.urlopia.api.v2.automatic.vacation.days;

import info.fingo.urlopia.config.persistance.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutomaticVacationDaysRepository extends BaseRepository<AutomaticVacationDays>, JpaRepository<AutomaticVacationDays, Long> {

}
