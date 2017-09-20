package info.fingo.urlopia.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Team findFirstByAdName(String adName);

    List<TeamExcerptProjection> findAllByOrderByName();

    @Query("SELECT t.adName FROM Team t")
    List<String> findAllAdNames();

}
