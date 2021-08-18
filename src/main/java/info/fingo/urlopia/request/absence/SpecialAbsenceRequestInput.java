package info.fingo.urlopia.request.absence;

import info.fingo.urlopia.request.RequestType;
import lombok.Getter;

import java.time.LocalDate;

public class SpecialAbsenceRequestInput implements BaseRequestInput{

    private final LocalDate startDate;
    private final LocalDate endDate;
    @Getter
    private final SpecialAbsenceReason reason;
    private final RequestType type;

    public SpecialAbsenceRequestInput(LocalDate startDate,
                                      LocalDate endDate,
                                      SpecialAbsenceReason reason) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.type = RequestType.SPECIAL;
    }

    private SpecialAbsenceRequestInput(SpecialAbsence specialAbsence) {
        this.startDate = specialAbsence.startDate();
        this.endDate = specialAbsence.endDate();
        this.reason = specialAbsence.reason();
        this.type = RequestType.SPECIAL;
    }

    public static SpecialAbsenceRequestInput fromSpecialAbsence(SpecialAbsence specialAbsence) {
        return new SpecialAbsenceRequestInput(specialAbsence);
    }

    @Override
    public RequestType getType() {
        return type;
    }

    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public LocalDate getEndDate() {
        return endDate;
    }
}
