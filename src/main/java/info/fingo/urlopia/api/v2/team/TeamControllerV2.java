package info.fingo.urlopia.api.v2.team;

import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.team.TeamExcerptProjection;
import info.fingo.urlopia.team.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v2/teams")
@RequiredArgsConstructor
public class TeamControllerV2 {

    private final TeamService teamService;


    @RolesAllowed({"ROLES_ADMIN", "ROLES_LEADER", "ROLES_WORKER"})
    @GetMapping(produces= MediaType.APPLICATION_JSON_VALUE)
    public List<TeamOutput> getAll(
            @RequestParam(name = "filter", defaultValue = "") String[] filters,
            Sort sort) {
        var filter = Filter.from(filters);
        var teams = teamService.getAll(filter, sort);
        return mapTeamsProjectionListToTeamOutputList(teams);
    }

    public List<TeamOutput> mapTeamsProjectionListToTeamOutputList(List<TeamExcerptProjection> teamExcerptProjections){
        return teamExcerptProjections.stream()
                .map(TeamOutput::fromTeamExcerptProjection)
                .toList();
    }




}
