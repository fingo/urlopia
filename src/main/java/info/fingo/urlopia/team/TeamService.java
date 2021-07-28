package info.fingo.urlopia.team;

import info.fingo.urlopia.config.persistance.filter.Filter;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public List<TeamExcerptProjection> getAll(Filter filter,
                                              Sort sort) {
        return teamRepository.findAll(filter, sort, TeamExcerptProjection.class);
    }
}
