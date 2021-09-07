package info.fingo.urlopia.api.v2.calendar.unspecifiedabsence;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.*;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UsersIdentifiedDays {
    private final Map<Long, Set<LocalDate>> identifiedDays;

    public static UsersIdentifiedDays empty() {
        return new UsersIdentifiedDays(new HashMap<>());
    }

    public Set<LocalDate> ofUser(Long userId) {
        return identifiedDays.getOrDefault(userId, Collections.emptySet());
    }

    public Set<Long> userIds() {
        return identifiedDays.keySet();
    }

    public void add(Collection<LocalDate> dates) {
        for (var userId : userIds()) {
            add(userId, dates);
        }
    }

    public void add(Long userId, LocalDate date) {
        ensureKeyIsPresent(userId);
        identifiedDays.get(userId).add(date);
    }

    public void add(Long userId, Collection<LocalDate> dates) {
        ensureKeyIsPresent(userId);
        identifiedDays.get(userId).addAll(dates);
    }

    private void ensureKeyIsPresent(Long userId) {
        identifiedDays.putIfAbsent(userId, new HashSet<>());
    }

    public boolean isIdentified(Long userId, LocalDate date) {
        return ofUser(userId).contains(date);
    }

    public UsersIdentifiedDays mergeWith(UsersIdentifiedDays other) {
        var result = UsersIdentifiedDays.empty();
        identifiedDays.forEach(result::add);
        for (var userId : other.userIds()) {
            result.add(userId, other.ofUser(userId));
        }
        return result;
    }
}
