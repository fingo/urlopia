package info.fingo.urlopia.holidays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Service
@Transactional
public class HolidayService {

    @Autowired
    private HolidayRepository holidayRepository;

    public List<LocalDate> getAllHolidaysDates() {
        List<Holiday> holidays = holidayRepository.findAll();
        List<LocalDate> result = new ArrayList<>(holidays.size());

        for (Holiday h :
                holidays) {
            result.add(h.getDate());
        }

        return result;
    }

    public List<HolidayResponse> getAllHolidaysInYear(int year) {
        List<Holiday> holidays = holidayRepository.findByDateBetween(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));
        List<HolidayResponse> result = new ArrayList<>(holidays.size());

        for (Holiday h :
                holidays) {
            result.add(new HolidayResponse(h));
        }

        return result;
    }

    public void addHoliday(HolidayResponse holiday) {
        holidayRepository.save(entityFromResponse(holiday));
    }

    public void addHolidays(List<HolidayResponse> holidays) {
        for (HolidayResponse h :
                holidays) {
            addHoliday(h);
        }
    }

    public void addAllHolidays(List<Holiday> holidays) {
        holidayRepository.save(holidays);
    }

    protected Holiday entityFromResponse(HolidayResponse holiday) {
        return new Holiday(holiday.getName(), LocalDateTime.ofInstant(Instant.ofEpochSecond(holiday.getDate() / 1000), TimeZone
                .getDefault().toZoneId()).toLocalDate());
    }

    @Scheduled(cron = "1 0 0 1 1 *")
    protected void synchronizeDatabase() {
        deleteYear(LocalDate.now().getYear() - 1);
        holidayRepository.save(generateHolidaysList(LocalDate.now().getYear() + 1));
    }

    public void deleteYear(int year) {
        List<Holiday> toDelete = holidayRepository.findByDateBetween(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));
        holidayRepository.delete(toDelete);
    }

    //public for convenience of testing
    public List<Holiday> generateHolidaysList(int year) {
        List<Holiday> holidays = new ArrayList<>(13);

        //loop for this and next year
            holidays.add(new Holiday("Nowy Rok", LocalDate.of(year, 1, 1)));
            holidays.add(new Holiday("Trzech Króli", LocalDate.of(year, 1, 6)));
            holidays.add(new Holiday("Święto Pracy", LocalDate.of(year, 5, 1)));
            holidays.add(new Holiday("Święto Konstytucji 3 Maja", LocalDate.of(year, 5, 3)));
            holidays.add(new Holiday("Święto Wojska Polskiego", LocalDate.of(year, 8, 15)));
            holidays.add(new Holiday("Wszystkich Świętych", LocalDate.of(year, 11, 1)));
            holidays.add(new Holiday("Święto Niepodległości", LocalDate.of(year, 11, 11)));
            holidays.add(new Holiday("Boże Narodzenie", LocalDate.of(year, 12, 25)));
            holidays.add(new Holiday("Boże Narodzenie", LocalDate.of(year, 12, 26)));

            LocalDate easter = getEaster(year);

            holidays.add(new Holiday("Wielkanoc", easter));
            holidays.add(new Holiday("Poniedziałek Wielkanocny", easter.plusDays(1)));
            holidays.add(new Holiday("Zesłanie Ducha Świętego", easter.plusDays(49)));
            holidays.add(new Holiday("Boże Ciało", easter.plusDays(60)));
        return holidays;
    }

    private LocalDate getEaster(int year) {
        // Easter
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int p = (h + l - 7 * m + 114) % 31;
        int easterDay = p + 1;
        int easterMonth = (h + l - 7 * m + 114) / 31;

        return LocalDate.of(year, easterMonth, easterDay);
    }

    public boolean isWorkingDay(LocalDate date) {
        return !this.isWeekend(date) & !this.isHoliday(date);
    }

    public boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    public boolean isHoliday(LocalDate date) {
        return holidayRepository.existsByDate(date);
    }
}
