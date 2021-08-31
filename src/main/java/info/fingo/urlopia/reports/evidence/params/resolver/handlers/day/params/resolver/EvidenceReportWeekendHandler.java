package info.fingo.urlopia.reports.evidence.params.resolver.handlers.day.params.resolver;

import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.request.RequestType;
import info.fingo.urlopia.request.absence.OperationNotSupportedException;
import info.fingo.urlopia.user.User;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class EvidenceReportWeekendHandler {
    private static final String SUNDAY_STATUS = "wn";
    private static final String SATURDAY_STATUS = "ws";
    private final RequestService requestService;
    private final EvidenceReportStatusFromRequestHandler evidenceReportStatusFromRequestHandler;


    public String handle(User user,
                          LocalDate date){
        return requestService
                .getByUserAndDate(user.getId(), date)
                .stream()
                .filter(request -> request.getType() == RequestType.SPECIAL)
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

}
