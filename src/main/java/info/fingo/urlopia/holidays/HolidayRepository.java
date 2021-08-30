package info.fingo.urlopia.holidays;

import info.fingo.urlopia.config.persistance.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository extends BaseRepository<Holiday>, JpaRepository<Holiday, Long> {
    List<Holiday> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Holiday> findByDate(LocalDate date);

    @Query("""
           SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END
           FROM Holiday h
           WHERE h.date = :date
           """)
    Boolean existsByDate(@Param("date") LocalDate date);
}
