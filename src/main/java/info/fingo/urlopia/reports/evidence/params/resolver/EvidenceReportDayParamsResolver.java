package info.fingo.urlopia.reports.evidence.params.resolver;

import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService;
import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.reports.ParamResolver;
import info.fingo.urlopia.reports.evidence.EvidenceReportModel;
import info.fingo.urlopia.reports.evidence.params.resolver.handlers.day.params.resolver.EvidenceReportDayWithPresenceHandler;
import info.fingo.urlopia.reports.evidence.params.resolver.handlers.day.params.resolver.EvidenceReportStatusFromHolidayHandler;
import info.fingo.urlopia.reports.evidence.params.resolver.handlers.day.params.resolver.EvidenceReportStatusFromRequestHandler;
import info.fingo.urlopia.reports.evidence.params.resolver.handlers.day.params.resolver.EvidenceReportWeekendHandler;
import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.user.User;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class EvidenceReportDayParamsResolver implements ParamResolver {
    private final User user;
    private final int year;
    private final HolidayService holidayService;
    private final RequestService requestService;

    private final EvidenceReportStatusFromHolidayHandler fromHolidayHandler;
    private final EvidenceReportWeekendHandler fromWeekendHandler;
    private final EvidenceReportStatusFromRequestHandler fromRequestHandler;
    private final EvidenceReportDayWithPresenceHandler fromDayWithPresenceHandler;

    public EvidenceReportDayParamsResolver(User user,
                                           int year,
                                           HolidayService holidayService,
                                           RequestService requestService,
                                           PresenceConfirmationService presenceConfirmationService) {
        this.user = user;
        this.year = year;
        this.holidayService = holidayService;
        this.requestService = requestService;
        this.fromDayWithPresenceHandler = new EvidenceReportDayWithPresenceHandler(presenceConfirmationService);
        this.fromRequestHandler = new EvidenceReportStatusFromRequestHandler();
        this.fromWeekendHandler = new EvidenceReportWeekendHandler(requestService, fromRequestHandler);
        this.fromHolidayHandler = new EvidenceReportStatusFromHolidayHandler(holidayService);
    }


    @Override
    public Map<String, String> resolve() {
        Map<String, String> parameters = new HashMap<>();
        for (int month = 1; month <= 12; month++) {
            for (int dayOfMonth = 1; dayOfMonth <= 31; dayOfMonth++) {
                var dayStatus = resolveSpecificDay(month, dayOfMonth);
                var paramName = String.format(EvidenceReportModel.DATE_FORMATTING, month, dayOfMonth);
                parameters.put(paramName, dayStatus);
            }
        }
        return parameters;
    }

    private String resolveSpecificDay(int month, int dayOfMonth) {
        var currentDate = LocalDate.now();
        if (year > currentDate.getYear()
                || (year == currentDate.getYear() && month >= currentDate.getMonthValue())) {
            return "";
        }
        try {
            var date = LocalDate.of(year, month, dayOfMonth);
            if (holidayService.isHoliday(date)) {
                var holiday = holidayService.getHolidayByDate(date);
                return fromHolidayHandler.handle(holiday);
            }
            if (holidayService.isWeekend(date)) {
                return fromWeekendHandler.handle(user, date);
            }
            if (holidayService.isWorkingDay(date)) {
                return requestService
                        .getByUserAndDate(user.getId(), date).stream()
                        .filter(req -> req.getStatus() == Request.Status.ACCEPTED)
                        .map(fromRequestHandler::handle)
                        .findFirst()
                        .orElse(fromDayWithPresenceHandler.handle(user, date));
            }
        } catch (DateTimeException ignore) {
            // if day does not exist then default value
        }
        return "-";
    }
}
