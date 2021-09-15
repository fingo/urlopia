package info.fingo.urlopia.api.v2.calendar;

import info.fingo.urlopia.config.persistance.filter.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final CalendarOutputProvider calendarOutputProvider;

    public CalendarOutput getCalendarInfo(Long authenticatedId,
                                          LocalDate startDate,
                                          LocalDate endDate,
                                          Filter filter) {
        return calendarOutputProvider.getCalendarOutputFor(authenticatedId, startDate, endDate, filter);
    }
}
