package info.fingo.urlopia.request.absence;

import java.time.LocalDate;

public record SpecialAbsence(Long requesterId,
                      LocalDate startDate,
                      LocalDate endDate,
                      SpecialAbsenceReason reason) {
}
