package info.fingo.urlopia.api.v2.automatic.vacation.days.schedule;

import info.fingo.urlopia.api.v2.automatic.vacation.days.AutomaticVacationDayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AddDaysScheduler {

    private final AutomaticVacationDayService automaticVacationDayService;

    @Scheduled(cron = "${urlopia.automatic.days.add.cron}")
    public void addNewYearHours() {
        log.info("Automatic add of hours for new year started");
        automaticVacationDayService.addHoursForNewYear();
    }
}
