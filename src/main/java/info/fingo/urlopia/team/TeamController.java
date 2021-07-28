package info.fingo.urlopia.team;

import info.fingo.urlopia.config.persistance.filter.Filter;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping(path = "/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @RolesAllowed("ROLES_ADMIN")
    @GetMapping(produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TeamExcerptProjection>> getAll(
            @RequestParam(name = "filter", defaultValue = "") String[] filters,
            Sort sort) {
        var filter = Filter.from(filters);
        var teams = teamService.getAll(filter, sort);
        return ResponseEntity.ok(teams);
    }

}
