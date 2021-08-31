package info.fingo.urlopia.reports.evidence;

import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService;
import info.fingo.urlopia.history.HistoryLogService;
import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.reports.ParamResolver;
import info.fingo.urlopia.reports.evidence.params.resolver.*;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EvidenceReportModelFactory {
    private static final String START_TIME_PREFIX = "startTime";
    private static final String REPORT_DATE_PREFIX = "reportDate";
    private static final String END_TIME_PREFIX = "endTime";
    private static final String USER_METADATA_PREFIX = "user";
    private static final String VACATION_LEAVE_PREFIX = "vacationLeave";
    private static final String DAY_STATUS_PREFIX = "day";
    private static final String USED_TIME_PREFIX = "usedTime";

    private final PresenceConfirmationService presenceConfirmationService;
    private final HolidayService holidayService;
    private final RequestService requestService;
    private final HistoryLogService historyLogService;

    public EvidenceReportModel create(User user,
                                      int year) {
        var resolvers = getResolvers(user,year);
        Map<String, String> model = new HashMap<>();
        resolvers.forEach((prefix, resolver) -> putAllWithPrefix(resolver.resolve(), model, prefix));
        return new EvidenceReportModel(model);
    }

    private void putAllWithPrefix(Map<String, String> from,
                                  Map<String, String> to,
                                  String prefix) {
        from.forEach((key, value) -> to.put(prefix + "." + key, value));
    }

    private Map<String, ParamResolver> getResolvers(User user,
                              int year){
        Map<String, ParamResolver> resolvers = new HashMap<>();
        resolvers.put(START_TIME_PREFIX, EvidenceReportPresenceConfirmationTimeParamResolver.ofStartTime(user, year,
                                                                            presenceConfirmationService, holidayService));
        resolvers.put(END_TIME_PREFIX, EvidenceReportPresenceConfirmationTimeParamResolver.ofEndTime(user, year,
                presenceConfirmationService, holidayService));
        resolvers.put(REPORT_DATE_PREFIX, new EvidenceReportDateParamsResolver(year));
        resolvers.put(USER_METADATA_PREFIX, new EvidenceReportUserParamsResolver(user));
        resolvers.put(USED_TIME_PREFIX, new EvidenceReportUsedTimeDuringTheYearParamsResolver(user, year,
                                                                                              historyLogService));
        resolvers.put(VACATION_LEAVE_PREFIX, new EvidenceReportVacationLeaveParamsResolver(user, year,
                                                                                            historyLogService));
        resolvers.put(DAY_STATUS_PREFIX, new EvidenceReportDayParamsResolver(user, year, holidayService,
                requestService, presenceConfirmationService));
        return resolvers;

    }
}
