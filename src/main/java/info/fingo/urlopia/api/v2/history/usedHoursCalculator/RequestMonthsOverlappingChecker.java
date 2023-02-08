package info.fingo.urlopia.api.v2.history.usedHoursCalculator;

import info.fingo.urlopia.request.Request;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class RequestMonthsOverlappingChecker {

    public boolean requestNotOverlapOtherMonth(Integer year,
                                               Integer month,
                                               Request request) {
        return requestStartsInGivenMonth(year, month, request) && requestEndsInGivenMonth(year, month, request);
    }

    public boolean requestOverlapOnlyNextMonths(Integer year,
                                                 Integer month,
                                                 Request request) {
        return requestStartsInGivenMonth(year, month, request) && requestEndsInNextMonth(year, month, request);
    }

    public boolean requestOverlapNextAndPrevMonth(Integer year,
                                                   Integer month,
                                                   Request request) {
        return requestStartInPrevMonth(year, month, request) && requestEndsInNextMonth(year, month, request);
    }

    public boolean requestOverlapOnlyPrevMonths(Integer year,
                                                 Integer month,
                                                 Request request) {
        return requestStartInPrevMonth(year, month, request) && requestEndsInGivenMonth(year, month, request);
    }

    private boolean requestStartInPrevMonth(Integer year,
                                            Integer month,
                                            Request request) {
        var startDate = request.getStartDate();
        var endDate = request.getEndDate();
        if (endDate.isBefore(LocalDate.of(year,month,1))){
            return false;
        }
        var startsOnPrevMonthsInTheSameYear = startDate.getYear() == year && startDate.getMonth().getValue() < month;
        var startsOnPrevYear = startDate.getYear() < year;
        return startsOnPrevMonthsInTheSameYear || startsOnPrevYear;
    }

    private boolean requestEndsInNextMonth(Integer year,
                                           Integer month,
                                           Request request) {
        var endDate = request.getEndDate();
        var endsOnNextMonthsInTheSameYear = endDate.getYear() == year && endDate.getMonth().getValue() > month;
        var endsOnNextYear = endDate.getYear() > year;
        return endsOnNextMonthsInTheSameYear || endsOnNextYear;
    }

    private boolean requestStartsInGivenMonth(Integer year,
                                              Integer month,
                                              Request request) {
        var startDate = request.getStartDate();
        var endDate = request.getEndDate();
        if (endDate.isBefore(LocalDate.of(year,month,1))){
            return false;
        }
        return startDate.getYear() == year && startDate.getMonth().getValue() == month;
    }

    private boolean requestEndsInGivenMonth(Integer year,
                                            Integer month,
                                            Request request) {
        var endDate = request.getEndDate();
        return endDate.getYear() == year && endDate.getMonth().getValue() == month;
    }
}
