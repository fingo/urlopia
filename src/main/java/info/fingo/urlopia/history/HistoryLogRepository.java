package info.fingo.urlopia.history;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface HistoryLogRepository extends JpaRepository<HistoryLog, Long> {

    HistoryLog findFirstByUserIdOrderById(Long userId);

    HistoryLog findFirstByUserIdOrderByIdDesc(Long userId);

    HistoryLog findFirstByRequestId(Long requestId);

    List<HistoryLogExcerptProjection> findByUserIdAndCreatedBetween(long userId, LocalDateTime startDate, LocalDateTime endDate);

    List<HistoryLogExcerptProjection> findByUserId(long userId);

    List<HistoryLogExcerptProjection> findFirst5ByUserIdOrderByCreatedDesc(Long userId);

    @Query("SELECT COALESCE(SUM(h.hours), 0) " +
            "FROM HistoryLog h " +
            "WHERE h.user.id = :userId")
    Float sumHours(@Param("userId") Long userId);

}