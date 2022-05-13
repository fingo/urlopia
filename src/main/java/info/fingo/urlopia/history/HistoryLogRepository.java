package info.fingo.urlopia.history;

import info.fingo.urlopia.config.persistance.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface HistoryLogRepository extends BaseRepository<HistoryLog>, JpaRepository<HistoryLog, Long> {

    HistoryLog findFirstByUserIdOrderById(Long userId);

    HistoryLog findFirstByUserIdOrderByIdDesc(Long userId);

    HistoryLog findFirstByRequestId(Long requestId);

    List<HistoryLogExcerptProjection> findByUserId(long userId);

    Page<HistoryLogExcerptProjection> findByUserId(long userId, Pageable pageable);

    List<HistoryLogExcerptProjection> findFirst5ByUserIdOrderByCreatedDesc(Long userId);

    List<HistoryLog> findLogsByUserIdAndCreatedBetween(long userId,
                                                       LocalDateTime startDate,
                                                       LocalDateTime endDate);

    List<HistoryLog> findLogsByUserId(long userId);

    HistoryLogExcerptProjection findById(long id);

    @Query("""
            SELECT COALESCE(SUM(h.hours), 0)
            FROM HistoryLog h
            WHERE h.user.id = :userId
            """)
    Float sumHours(@Param("userId") Long userId);

}