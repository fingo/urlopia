package info.fingo.urlopia.api.v2.automatic.vacation.days;

import info.fingo.urlopia.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class AutomaticVacationDaysService {

    private final AutomaticVacationDaysRepository automaticVacationDaysRepository;

    public void addForNewUser(User user){
        var automaticVacationDays = new AutomaticVacationDays(user, LocalDateTime.now());
        automaticVacationDaysRepository.save(automaticVacationDays);
    }

}
