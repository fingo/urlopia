package info.fingo.urlopia.request;

import info.fingo.urlopia.config.persistance.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RequestRepository extends BaseRepository<Request>, JpaRepository<Request, Long> {

    List<Request> findByRequesterId(Long requesterId);

    @Query("""
            SELECT r FROM Request r
            WHERE r.requester.id = :requesterId
            AND (YEAR(r.startDate) = :year OR YEAR(r.endDate) = :year)
            """)
    List<Request> findByRequesterIdAndYear(@Param("requesterId") Long requesterId,
                                           @Param("year") Integer year);

    @Query(value = """
            SELECT *
            FROM requests r
            WHERE r.requester_id = :requesterId AND
            r.status = :status AND
            (r.start_date, r.end_date) overlaps (:startDate, :endDate);
            """, nativeQuery = true)
    List<Request> findByRequesterAndDateIntervalAndStatus(@Param("requesterId") Long requesterId,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate,
                                                          @Param("status") String status);
//

}
