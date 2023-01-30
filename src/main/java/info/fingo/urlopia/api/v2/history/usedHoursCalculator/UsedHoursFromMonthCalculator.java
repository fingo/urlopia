package info.fingo.urlopia.api.v2.history.usedHoursCalculator;

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
    private final RequestMonthsOverlappingChecker requestMonthsOverlappingChecker;

    public double countUsedHours(Integer year,
                                 Integer month,
                                 HistoryLog log) {
        var request = log.getRequest();
        if (requestMonthsOverlappingChecker.requestNotOverlapOtherMonth(year, month, request)) {
            return log.getHours() * -1;
        }
        if (requestMonthsOverlappingChecker.requestOverlapOnlyNextMonths(year, month, request)) {
            return countHoursWhenOverlapOnlyNextMonth(request, log);
        }
        if (requestMonthsOverlappingChecker.requestOverlapOnlyPrevMonths(year, month, request)) {
            return countHoursWhenOverlapOnlyPrevMonths(year, month, request, log);
        }
        if (requestMonthsOverlappingChecker.requestOverlapNextAndPrevMonth(year, month, request)) {
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
        var yearOfNextMonth = currentMonthNumber == DECEMBER_NUMBER ? startDate.getYear() + 1 : startDate.getYear();
        return LocalDate.of(yearOfNextMonth, nextMonthNumber, 1);
    }


}
