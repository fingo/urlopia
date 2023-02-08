package info.fingo.urlopia.api.v2.history.usedHoursCalculator;

import info.fingo.urlopia.request.Request;

import java.time.YearMonth;

public record RequestCheckDTO(Request request, YearMonth dateToCheck) {
}
