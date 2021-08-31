package info.fingo.urlopia.reports.evidence.params.resolver.handlers.day.params.resolver;

import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.request.absence.OperationNotSupportedException;
import info.fingo.urlopia.request.absence.SpecialAbsenceReason;
import info.fingo.urlopia.user.User;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class EvidenceReportWeekendHandler {
    private static final String SUNDAY_STATUS = "wn";
    private static final String SATURDAY_STATUS = "ws";
    private static final List<SpecialAbsenceReason> SPECIAL_REQUESTS_TYPES_NEEDS_TO_BE_HANDLE =
            List.of(SpecialAbsenceReason.CHILDCARE,
                    SpecialAbsenceReason.PARENTAL_LEAVE,
                    SpecialAbsenceReason.MATERNITY_LEAVE,
                    SpecialAbsenceReason.PATERNITY_LEAVE);
    private final RequestService requestService;
    private final EvidenceReportStatusFromRequestHandler evidenceReportStatusFromRequestHandler;


    public String handle(User user,
                          LocalDate date){
        return requestService
                .getByUserAndDate(user.getId(), date)
                .stream()
                .filter(this::checkIfRequestNeedsToBeProcessed)
                .map(evidenceReportStatusFromRequestHandler::handle)
                .findFirst()
                .orElse(getEvidenceReportStatusFromWeekendDay(date));

    }

    private String getEvidenceReportStatusFromWeekendDay(LocalDate date) {
        var dayOfWeek = date.getDayOfWeek();
        return switch (dayOfWeek){
            case SATURDAY -> SATURDAY_STATUS;
            case SUNDAY -> SUNDAY_STATUS;
            default -> throw OperationNotSupportedException.operationNotSupported();
        };
    }

    private boolean checkIfRequestNeedsToBeProcessed(Request request){
        var requestType =request.getType();
        return switch (requestType){
            case NORMAL, OCCASIONAL -> false;
            case SPECIAL -> checkIfSpecialRequestNeedsToBeProcessed(request);
        };
    }

    private boolean checkIfSpecialRequestNeedsToBeProcessed(Request request){
        var specialAbsenceReason = SpecialAbsenceReason.valueOf(request.getSpecialTypeInfo());
        return SPECIAL_REQUESTS_TYPES_NEEDS_TO_BE_HANDLE.contains(specialAbsenceReason);
    }

}
