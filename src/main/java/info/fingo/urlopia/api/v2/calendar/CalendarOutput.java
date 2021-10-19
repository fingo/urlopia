package info.fingo.urlopia.api.v2.calendar;

import java.time.LocalDate;
import java.util.Map;

public record CalendarOutput(Map<LocalDate, SingleDayOutput> calendar) {}
