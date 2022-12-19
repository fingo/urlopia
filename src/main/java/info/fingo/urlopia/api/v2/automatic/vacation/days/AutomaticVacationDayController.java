package info.fingo.urlopia.api.v2.automatic.vacation.days;

import info.fingo.urlopia.api.v2.automatic.vacation.days.model.AutomaticVacationDayDTO;
import info.fingo.urlopia.api.v2.automatic.vacation.days.model.UpdateUserConfig;
import info.fingo.urlopia.config.persistance.filter.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v2/automatic-vacation-days")
public class AutomaticVacationDayController {
    private final AutomaticVacationDayService automaticVacationDayService;

    @GetMapping()
    @RolesAllowed({"ROLES_ADMIN"})
    public List<AutomaticVacationDayDTO> getAll(@RequestParam(name = "filter", defaultValue = "") String[] filters){
        var filter = Filter.from(filters);
        return automaticVacationDayService.getFiltered(filter);
    }

    @PatchMapping()
    @RolesAllowed({"ROLES_ADMIN"})
    public AutomaticVacationDayDTO update(@RequestBody UpdateUserConfig updateUserConfig){
        return automaticVacationDayService.update(updateUserConfig);
    }
}
