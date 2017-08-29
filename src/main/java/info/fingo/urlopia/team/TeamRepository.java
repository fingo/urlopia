package info.fingo.urlopia.team;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Team findFirstByName(String name);

    Team findFirstByAdName(String adName);

    List<TeamExcerptProjection> findAllByOrderByName();

}
