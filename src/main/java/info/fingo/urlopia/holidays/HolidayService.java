package info.fingo.urlopia.holidays;

import info.fingo.urlopia.UrlopiaApplication;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.config.persistance.filter.Operator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Service
@Transactional
public class HolidayService {

    @Autowired
    private HolidayRepository holidayRepository;

    public List<HolidayResponse> getAllHolidaysInYear(int year, Filter filter) {
        var formatter = DateTimeFormatter.ofPattern(UrlopiaApplication.DATE_FORMAT);
        var yearStart = LocalDate.of(year, 1, 1).format(formatter);
        var yearEnd = LocalDate.of(year, 12, 31).format(formatter);

        var filterWithRestrictions = filter.toBuilder()
                .and("date", Operator.GREATER_OR_EQUAL, yearStart)
                .and("date", Operator.LESS_OR_EQUAL, yearEnd)
                .build();
        var holidays = holidayRepository.findAll(filterWithRestrictions);
        List<HolidayResponse> result = new ArrayList<>(holidays.size());

        for (var h : holidays) {
            result.add(new HolidayResponse(h));
        }

        return result;
    }

    public void addHoliday(HolidayResponse holiday) {
        holidayRepository.save(entityFromResponse(holiday));
    }

    public void addHolidays(List<HolidayResponse> holidays) {
        for (var h : holidays) {
            addHoliday(h);
        }
    }

    protected Holiday entityFromResponse(HolidayResponse holiday) {
        var holidayDateTimestamp = holiday.getDate() / 1000;
        var zoneId = TimeZone.getDefault().toZoneId();
        var date = LocalDateTime.ofInstant(Instant.ofEpochSecond(holidayDateTimestamp), zoneId);

        return new Holiday(holiday.getName(), date.toLocalDate());
    }

    @Scheduled(cron = "1 0 0 1 1 *")
    protected void synchronizeDatabase() {
        deleteYear(LocalDate.now().getYear() - 1);
        holidayRepository.saveAll(generateHolidaysList(LocalDate.now().getYear() + 1));
    }

    public void deleteYear(int year) {
        var yearStartDate = LocalDate.of(year, 1, 1);
        var yearEndDate = LocalDate.of(year, 12, 31);
        var holidaysToDelete = holidayRepository.findByDateBetween(yearStartDate, yearEndDate);
        holidayRepository.deleteAll(holidaysToDelete);
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

// https://stackoverflow.com/questions/26022233/calculate-the-date-of-easter-sunday
    private LocalDate getEaster(int year) {
        var a = year % 19;
        var b = year / 100;
        var c = year % 100;
        var d = b / 4;
        var e = b % 4;
        var g = (8 * b + 13) / 25;
        var h = (19 * a + b - d - g + 15) % 30;
        var j = c / 4;
        var k = c % 4;
        var m = (a + 11 * h) / 319;
        var r = (2 * e + 2 * j - k - h + m + 32) % 7;
        var n = (h - m + r + 90) / 25;
        var p = (h - m + r + n + 19) % 32;

        return LocalDate.of(year, n, p);
    }

    public boolean isWorkingDay(LocalDate date) {
        return !this.isWeekend(date) && !this.isHoliday(date);
    }

    public boolean isWeekend(LocalDate date) {
        var dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    public boolean isHoliday(LocalDate date) {
        return holidayRepository.existsByDate(date);
    }
}
