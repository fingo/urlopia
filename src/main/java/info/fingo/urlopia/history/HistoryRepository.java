package info.fingo.urlopia.history;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Józef Grodzicki
 */
public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByUserIdAndCreatedAfter(long userId, LocalDateTime date);

    List<History> findByUserIdAndCreatedBetween(long userId, LocalDateTime startDate, LocalDateTime endDate);

    List<History> findByUserId(long userId);

    List<History> findFirst5ByUserMailOrderByCreatedDesc(String userMail);
}