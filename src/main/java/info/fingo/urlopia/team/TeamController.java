package info.fingo.urlopia.team;

import info.fingo.urlopia.config.persistance.filter.Filter;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List> getAll(@RequestParam(name = "filter", defaultValue = "") String[] filters,
                                       Sort sort) {
        Filter filter = Filter.from(filters);
        List<TeamExcerptProjection> teams = teamService.getAll(filter, sort);
        return ResponseEntity.ok(teams);
    }

}
