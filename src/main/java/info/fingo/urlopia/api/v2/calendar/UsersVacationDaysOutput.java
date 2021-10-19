package info.fingo.urlopia.api.v2.calendar;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record UsersVacationDaysOutput(Map<LocalDate, List<Long>> usersVacations) {}
