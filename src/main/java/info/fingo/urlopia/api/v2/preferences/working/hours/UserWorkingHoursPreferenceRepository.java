package info.fingo.urlopia.api.v2.preferences.working.hours;

import info.fingo.urlopia.config.persistance.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWorkingHoursPreferenceRepository extends BaseRepository<UserWorkingHoursPreference>,
                                                              JpaRepository<UserWorkingHoursPreference, Long> {

}
