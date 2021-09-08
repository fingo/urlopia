package info.fingo.urlopia.api.v2.reports.attendance;


import info.fingo.urlopia.api.v2.presence.PresenceConfirmationService;
import info.fingo.urlopia.api.v2.reports.attendance.resolver.MonthlyAttendanceListReportDateParamsResolver;
import info.fingo.urlopia.api.v2.reports.attendance.resolver.MonthlyAttendanceListReportUserParamsResolver;
import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.reports.ParamResolver;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class MonthlyAttendanceListReportFactory {
    private static final String FIRST_USER_PREFIX = "firstUser";
    private static final String SECOND_USER_PREFIX = "secondUser";
    private static final String THIRD_USER_PREFIX = "thirdUser";
    private static final String FOURTH_USER_PREFIX = "fourthUser";
    private static final String FIFTH_USER_PREFIX = "fifthUser";
    private static final String DATE_PREFIX =  "reportDate";

    private final HolidayService holidayService;
    private final RequestService requestService;
    private final PresenceConfirmationService presenceConfirmationService;

    public MonthlyAttendanceListReportModel create(int month,
                                                   int year,
                                                   AttendanceListPage page){
        var resolvers = getResolvers(month,year,page);
        Map<String, String> model = new HashMap<>();
        resolvers.forEach((prefix, resolver) -> putAllWithPrefix(resolver.resolve(), model, prefix));
        return new MonthlyAttendanceListReportModel(model);
    }

    private void putAllWithPrefix(Map<String, String> from,
                                  Map<String, String> to,
                                  String prefix) {
        from.forEach((key, value) -> to.put(prefix + "." + key, value));
    }

    private Map<String, ParamResolver> getResolvers(int month,
                                                    int year,
                                                    AttendanceListPage page){
        Map<String, ParamResolver> resolvers = new HashMap<>();
        resolvers.put(DATE_PREFIX,new MonthlyAttendanceListReportDateParamsResolver(year, month));
        putOneUserResolver(resolvers,FIRST_USER_PREFIX,month,year,page);
        putOneUserResolver(resolvers,SECOND_USER_PREFIX,month,year,page);
        putOneUserResolver(resolvers,THIRD_USER_PREFIX,month,year,page);
        putOneUserResolver(resolvers,FOURTH_USER_PREFIX,month,year,page);
        putOneUserResolver(resolvers,FIFTH_USER_PREFIX,month,year,page);
        return resolvers;

    }

    private void putOneUserResolver(Map<String, ParamResolver> resolvers,
                                    String prefix,
                                    int month,
                                    int year,
                                    AttendanceListPage page){
        var index = mapPrefixToIndex(prefix);
        User user = index < page.getUsersOnPage().size() ? page.getUsersOnPage().get(index) : null;
        var resolver = new MonthlyAttendanceListReportUserParamsResolver(user, year, month, holidayService,
                                                                         requestService, presenceConfirmationService);
        resolvers.put(prefix, resolver);
    }

    private int mapPrefixToIndex(String prefix){
        return switch (prefix){
            case FIRST_USER_PREFIX -> 0;
            case SECOND_USER_PREFIX ->  1;
            case THIRD_USER_PREFIX -> 2;
            case FOURTH_USER_PREFIX -> 3;
            case FIFTH_USER_PREFIX -> 4;
            default -> -1;
        };
    }


}
