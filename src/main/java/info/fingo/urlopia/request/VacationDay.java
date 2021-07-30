package info.fingo.urlopia.request;

import java.time.LocalDate;
import java.util.List;

public record VacationDay(LocalDate date,
                          List<String> userNames) {}
