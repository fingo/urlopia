package info.fingo.urlopia.team;

import info.fingo.urlopia.config.persistance.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamRepository extends BaseRepository<Team>, JpaRepository<Team, Long> {

    Team findFirstByAdName(String adName);

    @Query("SELECT t.adName FROM Team t")
    List<String> findAllAdNames();

}
