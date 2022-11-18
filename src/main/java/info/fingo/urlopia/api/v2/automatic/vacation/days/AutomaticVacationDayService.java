package info.fingo.urlopia.api.v2.automatic.vacation.days;

import info.fingo.urlopia.api.v2.automatic.vacation.days.model.AutomaticVacationDay;
import info.fingo.urlopia.api.v2.automatic.vacation.days.model.AutomaticVacationDayDTO;
import info.fingo.urlopia.api.v2.automatic.vacation.days.model.UpdateUserConfig;
import info.fingo.urlopia.history.HistoryLogInput;
import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class AutomaticVacationDayService {

    private final AutomaticVacationDaysRepository automaticVacationDaysRepository;
    private final HistoryLogService historyLogService;

    public void addForNewUser(User user) {
        var automaticVacationDays = new AutomaticVacationDay(user);
        automaticVacationDaysRepository.save(automaticVacationDays);
    }

    public AutomaticVacationDayDTO update(UpdateUserConfig updateUserConfig) {
        var automaticVacationDay = getAutomaticVacationDayFrom(updateUserConfig.userId());
        var updated = updateExisted(automaticVacationDay, updateUserConfig);
        return AutomaticVacationDayDTO.from(updated);
    }

    public List<AutomaticVacationDayDTO> getAll() {
        return automaticVacationDaysRepository.findAll()
                .stream()
                .map(this::countProposition)
                .map(AutomaticVacationDayDTO::from)
                .toList();
    }

    public void addHoursForNewYear(){
        automaticVacationDaysRepository.findAll()
                .stream()
                .map(this::countProposition)
                .filter(this::hasNotEmptyProposition)
                .forEach(this::addHours);
    }

    private boolean hasNotEmptyProposition(AutomaticVacationDay automaticVacationDay){
        return automaticVacationDay.getNextYearHoursProposition() != 0;
    }

    private AutomaticVacationDay getAutomaticVacationDayFrom(Long userId) {
        var optionalAutomaticVacationDay = automaticVacationDaysRepository.findByUserId(userId);
        if (optionalAutomaticVacationDay.isEmpty()) {
            throw new AutomaticVacationDaysNotFoundException();
        }
        return optionalAutomaticVacationDay.get();
    }

    private void addHours(AutomaticVacationDay automaticVacationDay) {
        var historyLogInput = HistoryLogInput.from(automaticVacationDay);
        historyLogService.createBySystem(historyLogInput, automaticVacationDay.getUser().getId());
    }

    private AutomaticVacationDay updateExisted(AutomaticVacationDay automaticVacationDay,
                                               UpdateUserConfig updateUserConfig) {
        automaticVacationDay.setModified(LocalDateTime.now());
        automaticVacationDay.setNextYearDaysBase(updateUserConfig.nextYearBase());
        automaticVacationDay.setNextYearHoursProposition(Double.valueOf(updateUserConfig.nextYearProposition()));
        return automaticVacationDaysRepository.save(automaticVacationDay);
    }


    private AutomaticVacationDay countProposition(AutomaticVacationDay automaticVacationDay){
        var shouldCountAgain = automaticVacationDay.getNextYearHoursProposition() == 0;
        if (shouldCountAgain){
            return countAgain(automaticVacationDay);
        }
        return automaticVacationDay;
    }

    private AutomaticVacationDay countAgain(AutomaticVacationDay automaticVacationDay){
        var user = automaticVacationDay.getUser();
        if (user.getWorkTime() == 8.0f){
            automaticVacationDay.setNextYearHoursProposition(automaticVacationDay.getNextYearDaysBase() * 8.0);
            automaticVacationDay.setModified(LocalDateTime.now());
        } // for now only for full-time workers set data
        return automaticVacationDaysRepository.save(automaticVacationDay);
    }

}