package info.fingo.urlopia.api.v2.automatic.vacation.days;

import info.fingo.urlopia.api.v2.automatic.vacation.days.model.AutomaticVacationDayDTO;
import info.fingo.urlopia.api.v2.automatic.vacation.days.model.UpdateUserConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    public List<AutomaticVacationDayDTO> getAll(Pageable pageable){
        return automaticVacationDayService.getAll(pageable);
    }

    @PatchMapping()
    @RolesAllowed({"ROLES_ADMIN"})
    public AutomaticVacationDayDTO update(@RequestBody UpdateUserConfig updateUserConfig){
        return automaticVacationDayService.update(updateUserConfig);
    }
}
