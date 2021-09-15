package info.fingo.urlopia.api.v2.calendar;

import info.fingo.urlopia.api.v2.calendar.unspecifiedabsence.UsersIdentifiedDays;
import info.fingo.urlopia.api.v2.presence.PresenceConfirmation;
import info.fingo.urlopia.holidays.Holiday;
import info.fingo.urlopia.request.absence.InvalidDatesOrderException;
import info.fingo.urlopia.user.User;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

class CalendarOutputBuilder {
    private final User currentUser;
    private final LocalDate startDate;
    private final LocalDate endDate;

    private Map<LocalDate, List<Holiday>> holidayDays;
    private UsersIdentifiedDays usersVacationDays;
    private Map<LocalDate, PresenceConfirmation> userPresenceConfirmationsDays;
    private LocalDate userFirstPresenceConfirmationDate;
    private List<User> users;

    private CalendarOutputBuilder(User currentUser, LocalDate startDate, LocalDate endDate) {
        this.currentUser = currentUser;
        this.startDate = startDate;
        this.endDate = endDate;
        reset();
    }

    public static CalendarOutputBuilder of(User user, LocalDate startDate, LocalDate endDate) {
        return new CalendarOutputBuilder(user, startDate, endDate);
    }

    public CalendarOutputBuilder withHolidays(List<Holiday> holidays) {
        for (var holiday : holidays) {
            var holidayDate = holiday.getDate();
            holidayDays.computeIfAbsent(holidayDate, date -> new LinkedList<>());
            holidayDays.get(holidayDate).add(holiday);
        }
        return this;
    }

    public CalendarOutputBuilder withUsersVacationDays(UsersIdentifiedDays usersVacationDays) {
        this.usersVacationDays = this.usersVacationDays.mergeWith(usersVacationDays);
        return this;
    }

    public CalendarOutputBuilder withUserPresenceConfirmations(List<PresenceConfirmation> userPresenceConfirmations) {
        for (var confirmation : userPresenceConfirmations) {
            var confirmationDate = confirmation.getDate();
            userPresenceConfirmationsDays.put(confirmationDate, confirmation);
            if (confirmationDate.isBefore(userFirstPresenceConfirmationDate)) {
                userFirstPresenceConfirmationDate = confirmationDate;
            }
        }
        return this;
    }

    public CalendarOutputBuilder withUsers(List<User> users) {
        this.users.addAll(users);
        return this;
    }

    public CalendarOutput build() {
        var output = buildCalendarOutput();
        reset();
        return output;
    }

    private void reset() {
        holidayDays = new HashMap<>();
        usersVacationDays = UsersIdentifiedDays.empty();
        userPresenceConfirmationsDays = new HashMap<>();
        userFirstPresenceConfirmationDate = LocalDate.MAX;
        users = new LinkedList<>();
    }

    private CalendarOutput buildCalendarOutput() {
        var calendarOutput = new HashMap<LocalDate, SingleDayOutput>();

        if (endDate.isBefore(startDate)) {
            throw InvalidDatesOrderException.invalidDatesOrder();
        }

        startDate.datesUntil(endDate.plusDays(1))
                .forEach(date -> calendarOutput.put(date, buildSingleDayOutput(date)));

        return new CalendarOutput(calendarOutput);
    }

    private SingleDayOutput buildSingleDayOutput(LocalDate date) {
        var singleDayOutput = new SingleDayOutput();

        singleDayOutput.setWorkingDay(isWorkingDay(date));
        singleDayOutput.setHolidays(holidaysNamesFor(date));
        singleDayOutput.setAbsentUsers(absentUsersIn(date));
        singleDayOutput.setCurrentUserInformation(buildCurrentUserInformation(date));

        return singleDayOutput;
    }

    private boolean isWorkingDay(LocalDate date) {
        var dayOfWeek = date.getDayOfWeek();
        var isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
        var isHoliday = holidayDays.containsKey(date);
        return !isWeekend && !isHoliday;
    }

    private List<String> holidaysNamesFor(LocalDate date) {
        if (holidayDays.containsKey(date)) {
            return holidayDays.get(date).stream()
                    .map(Holiday::getName)
                    .toList();
        }

        return Collections.emptyList();
    }

    private List<AbsentUserOutput> absentUsersIn(LocalDate date) {
        return users.stream()
                .filter(user -> usersVacationDays.isIdentified(user.getId(), date))
                .map(AbsentUserOutput::of)
                .toList();
    }

    private CurrentUserInformationOutput buildCurrentUserInformation(LocalDate date) {
        var currentUserInformation = new CurrentUserInformationOutput();
        var currentUserId = currentUser.getId();

        currentUserInformation.setAbsent(usersVacationDays.isIdentified(currentUserId, date));
        currentUserInformation.setPresenceConfirmation(getPresenceConfirmationOutputFor(date));
        currentUserInformation.setVacationHoursModifications(Collections.emptyList());

        return currentUserInformation;
    }

    private PresenceConfirmationOutput getPresenceConfirmationOutputFor(LocalDate date) {
        if (userPresenceConfirmationsDays.containsKey(date)) {
            return PresenceConfirmationOutput.fromPresenceConfirmation(userPresenceConfirmationsDays.get(date));
        }

        if (userFirstPresenceConfirmationDate != LocalDate.MAX) {
            if (date.isBefore(userFirstPresenceConfirmationDate)) {
                return PresenceConfirmationOutput.unspecified();
            }
            return PresenceConfirmationOutput.empty();
        }

        return PresenceConfirmationOutput.unspecified();
    }
}
