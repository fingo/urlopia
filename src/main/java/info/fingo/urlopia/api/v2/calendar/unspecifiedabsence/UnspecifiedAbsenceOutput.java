package info.fingo.urlopia.api.v2.calendar.unspecifiedabsence;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record UnspecifiedAbsenceOutput(Map<Long, List<LocalDate>> users) {}
