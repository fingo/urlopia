package info.fingo.urlopia.team;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public List<TeamExcerptProjection> getAll() {
        return teamRepository.findAllByOrderByName();
    }
}
