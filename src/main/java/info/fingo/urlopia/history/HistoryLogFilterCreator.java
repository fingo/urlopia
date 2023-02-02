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
    private static final String EVENT_FIELD_FILTER = "userDetailsChangeEvent";
    private static final String COUNT_NEXT_YEAR_FIELD_FILTER = "countForNextYear";


    public static Filter filterBy(LocalDate date,
                                  Long userId){
        var formattedStartOfDay = getFormattedDayStart(date);
        var formattedEndOfDay = getFormattedDayEnd(date);
        return Filter.newBuilder()
                .and("user.id", Operator.EQUAL, String.valueOf(userId))
                .and(CREATED_FIELD_FILTER, Operator.GREATER_OR_EQUAL, formattedStartOfDay)
                .and(CREATED_FIELD_FILTER, Operator.LESS_OR_EQUAL, formattedEndOfDay)
                .build();
    }

    public static Filter filterBy(LocalDate startDate,
                                  LocalDate endDate,
                                  Long userId){
        var formattedStartOfDay = getFormattedDayStart(startDate);
        var formattedEndOfDay = getFormattedDayEnd(endDate);
        return Filter.newBuilder()
                .and("user.id", Operator.EQUAL, String.valueOf(userId))
                .and(CREATED_FIELD_FILTER, Operator.GREATER_OR_EQUAL, formattedStartOfDay)
                .and(CREATED_FIELD_FILTER, Operator.LESS_OR_EQUAL, formattedEndOfDay)
                .build();
    }

    private static String getFormattedDayStart(LocalDate date){
        var startOfDay = LocalDateTime.of(date, LocalTime.MIN);
        var formatter = DateTimeFormatter.ofPattern(UrlopiaApplication.DATE_TIME_FORMAT);
        return formatter.format(startOfDay);
    }

    private static String getFormattedDayEnd(LocalDate date){
        var endOfDay = LocalDateTime.of(date, LocalTime.MAX);
        var formatter = DateTimeFormatter.ofPattern(UrlopiaApplication.DATE_TIME_FORMAT);
        return formatter.format(endOfDay);
    }

    public static Filter filterBy(UserDetailsChangeEvent changeEvent,
                                  YearMonth minYearMonth){
        var firstDayOfMonth = minYearMonth.atDay(1);
        var startOfFirstDay = LocalDateTime.of(firstDayOfMonth, LocalTime.MIN);

        return Filter.newBuilder()
                .and(CREATED_FIELD_FILTER, Operator.GREATER_OR_EQUAL, DATE_FORMATTER.format(startOfFirstDay))
                .and(EVENT_FIELD_FILTER, Operator.EQUAL, changeEvent.name())
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
                .and(EVENT_FIELD_FILTER, Operator.EQUAL, changeEvent.name())
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
                .and(EVENT_FIELD_FILTER, Operator.EQUAL, changeEvent.name())
                .build();
    }

    public static Filter filterBy(Integer year,
                                  Long userId,
                                  Boolean countForNextYear){
        var firstDayOfYear = LocalDate.of(year, 1, 1);
        var formattedFirstDayOfYear = getFormattedDayStart(firstDayOfYear);

        var lastDayOfMonth = LocalDate.of(year,12,31);
        var formattedLastDaysOfMonth = getFormattedDayEnd(lastDayOfMonth);

        return Filter.newBuilder()
                .and(USER_ID_FIELD_FILTER, Operator.EQUAL, String.valueOf(userId))
                .and(CREATED_FIELD_FILTER, Operator.GREATER_OR_EQUAL, formattedFirstDayOfYear)
                .and(CREATED_FIELD_FILTER, Operator.LESS_OR_EQUAL, formattedLastDaysOfMonth)
                .and(COUNT_NEXT_YEAR_FIELD_FILTER, Operator.EQUAL, String.valueOf(countForNextYear))
                .build();
    }
}
