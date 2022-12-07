package info.fingo.urlopia.history;

import info.fingo.urlopia.UrlopiaApplication;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.config.persistance.filter.Operator;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class HistoryLogFilterCreator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(UrlopiaApplication.DATE_TIME_FORMAT);
    private static final String CREATED_FIELD_FILTER = "created";
    private static final String USER_ID_FIELD_FILTER = "user.id";
    private static final String EVENT_FILTER = "userDetailsChangeEvent";


    public static Filter filterBy(UserDetailsChangeEvent changeEvent,
                                  YearMonth minYearMonth){
        var firstDayOfMonth = minYearMonth.atDay(1);
        var startOfFirstDay = LocalDateTime.of(firstDayOfMonth, LocalTime.MIN);

        return Filter.newBuilder()
                .and(CREATED_FIELD_FILTER, Operator.GREATER_OR_EQUAL, DATE_FORMATTER.format(startOfFirstDay))
                .and(EVENT_FILTER, Operator.EQUAL, changeEvent.name())
                .build();
    }

    public static Filter filterBy(UserDetailsChangeEvent changeEvent,
                                  YearMonth yearMonth,
                                  Long userId){
        var firstDayOfMonth = yearMonth.atDay(1);
        var startOfFirstDay = LocalDateTime.of(firstDayOfMonth, LocalTime.MIN);

        var lastDayOfMonth = yearMonth.atEndOfMonth();
        var endOfLastDay = LocalDateTime.of(lastDayOfMonth, LocalTime.MAX);

        return Filter.newBuilder()
                .and(USER_ID_FIELD_FILTER, Operator.EQUAL, String.valueOf(userId))
                .and(CREATED_FIELD_FILTER, Operator.GREATER_OR_EQUAL, DATE_FORMATTER.format(startOfFirstDay))
                .and(CREATED_FIELD_FILTER, Operator.LESS_OR_EQUAL, DATE_FORMATTER.format(endOfLastDay))
                .and(EVENT_FILTER, Operator.EQUAL, changeEvent.name())
                .build();
    }

    public static Filter filterBy(UserDetailsChangeEvent changeEvent,
                                  int year,
                                  Long userId){
        var firstDayOfYear = LocalDate.of(year, 1, 1);
        var startOfFirstDay = LocalDateTime.of(firstDayOfYear, LocalTime.MIN);

        var lastDayOfMonth = LocalDate.of(year,12,31);
        var endOfLastDay = LocalDateTime.of(lastDayOfMonth, LocalTime.MAX);

        return Filter.newBuilder()
                .and(USER_ID_FIELD_FILTER, Operator.EQUAL, String.valueOf(userId))
                .and(CREATED_FIELD_FILTER, Operator.GREATER_OR_EQUAL, DATE_FORMATTER.format(startOfFirstDay))
                .and(CREATED_FIELD_FILTER, Operator.LESS_OR_EQUAL, DATE_FORMATTER.format(endOfLastDay))
                .and(EVENT_FILTER, Operator.EQUAL, changeEvent.name())
                .build();
    }
}
