package info.fingo.urlopia.api.v2.history;

import info.fingo.urlopia.history.HistoryLog;
import info.fingo.urlopia.holidays.WorkingDaysCalculator;
import info.fingo.urlopia.request.Request;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class UsedHoursFromMonthCalculator{
    private static final int DECEMBER_NUMBER = 12;

    private final WorkingDaysCalculator workingDaysCalculator;

    public double countUsedHours(Integer year,
                                 Integer month,
                                 HistoryLog log) {
        var request = log.getRequest();
        if (requestNotOverlapNextMonth(year, month, request)) {
            return log.getHours() * -1;
        }
        if (requestOverlapOnlyNextMonths(year, month, request)) {
            return countHoursWhenOverlapOnlyNextMonth(request, log);
        }
        if (requestOverlapOnlyPrevMonths(year, month, request)) {
            return countHoursWhenOverlapOnlyPrevMonths(year, month, request, log);
        }
        if (requestOverlapNextAndPrevMonth(year, month, request)) {
            return countHoursWhenOverlapNextAndPrevMonth(year, month, log);
        }
        return 0;
    }

    private double countHoursWhenOverlapOnlyPrevMonths(Integer year,
                                                       Integer month,
                                                       Request request,
                                                       HistoryLog log) {
        var firstDayOfGivenMonth = LocalDate.of(year, month, 1);
        var endDate = request.getEndDate();
        var daysInGivenMonth = workingDaysCalculator.calculate(firstDayOfGivenMonth, endDate);
        return daysInGivenMonth * log.getUserWorkTime();
    }

    private double countHoursWhenOverlapOnlyNextMonth(Request request,
                                                      HistoryLog log) {
        var startDate = request.getStartDate();
        var firstDayOfNextMonth = getFirstDayOfNextMonth(startDate);
        var lastDayOfGivenMonth = firstDayOfNextMonth.minusDays(1);
        var daysInGivenMonth = workingDaysCalculator.calculate(startDate, lastDayOfGivenMonth);
        return daysInGivenMonth * log.getUserWorkTime();
    }

    private double countHoursWhenOverlapNextAndPrevMonth(Integer year,
                                                         Integer month,
                                                         HistoryLog log) {
        var firstDayOfGivenMonth = (LocalDate.of(year, month, 1));
        var firstDayOfNextMonth = getFirstDayOfNextMonth(firstDayOfGivenMonth);
        var lastDayOfGivenMonth = firstDayOfNextMonth.minusDays(1);
        var daysInGivenMonth = workingDaysCalculator.calculate(firstDayOfGivenMonth, lastDayOfGivenMonth);
        return daysInGivenMonth * log.getUserWorkTime();
    }

    private LocalDate getFirstDayOfNextMonth(LocalDate startDate) {
        var currentMonthNumber = startDate.getMonth().getValue();
        var nextMonthNumber = currentMonthNumber == DECEMBER_NUMBER ? 1 : currentMonthNumber + 1;
        return LocalDate.of(startDate.getYear(), nextMonthNumber, 1);
    }

    private boolean requestNotOverlapNextMonth(Integer year,
                                               Integer month,
                                               Request request) {
        return requestStartsInGivenMonth(year, month, request) && requestEndsInGivenMonth(year, month, request);
    }

    private boolean requestOverlapOnlyNextMonths(Integer year,
                                                 Integer month,
                                                 Request request) {
        return requestStartsInGivenMonth(year, month, request) && requestEndsInNextMonth(year, month, request);
    }

    private boolean requestOverlapNextAndPrevMonth(Integer year,
                                                   Integer month,
                                                   Request request) {
        return requestStartInPrevMonth(year, month, request) && requestEndsInNextMonth(year, month, request);
    }

    private boolean requestOverlapOnlyPrevMonths(Integer year,
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
        var startsOnPrevMonthsInTheSameYear = startDate.getYear() == year && startDate.getMonth().getValue() <= month;
        var startsOnPrevYear = startDate.getYear() < year;
        return startsOnPrevMonthsInTheSameYear || startsOnPrevYear;
    }

    private boolean requestEndsInNextMonth(Integer year,
                                           Integer month,
                                           Request request) {
        var endDate = request.getEndDate();
        var endsOnNextMonthsInTheSameYear = endDate.getYear() == year && endDate.getMonth().getValue() >= month;
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
