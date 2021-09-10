package info.fingo.urlopia.api.v2.reports.holidays;

import info.fingo.urlopia.request.Request;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestFormatter {
    public static String formattedWorkingDaysOf(Request request) {
        var workingDays = request.getWorkingDays();
        if (workingDays == 1) {
            return "1 dzień roboczy";
        } else if (workingDays < 5) {
            return "%s dni robocze".formatted(workingDays);
        } else {
            return "%s dni roboczych".formatted(workingDays);
        }
    }

    public static String formattedRequestTypeOf(Request request) {
        var requestType = request.getType();
        return switch (requestType) {
            case NORMAL -> "Wypoczynkowy";
            case OCCASIONAL -> "Okolicznościowy (" + request.getTypeInfo().getInfo() + ")";
            case SPECIAL -> "Specjalny (" + request.getTypeInfo().getInfo() + ")";
        };
    }

    public static String formattedRequestStatusOf(Request request) {
        var status = request.getStatus();
        return switch (status) {
            case ACCEPTED -> "Zaakceptowany";
            case REJECTED -> "Odrzucony";
            case CANCELED -> "Anulowany";
            case PENDING -> "Oczekujący";
        };
    }
}
