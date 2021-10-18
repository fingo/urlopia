package info.fingo.urlopia.api.v2.preferences.working.hours;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "user_working_hours_preference")
@Getter
@NoArgsConstructor // Required for hibernate
public class UserWorkingHoursPreference {
    @Id
    @Setter
    private Long userId;

    @Column(nullable = false)
    @Setter
    private LocalDateTime changed = LocalDateTime.now();

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Setter
    private SingleDayHourPreference mondayPreference;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Setter
    private SingleDayHourPreference tuesdayPreference;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Setter
    private SingleDayHourPreference wednesdayPreference;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Setter
    private SingleDayHourPreference thursdayPreference;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Setter
    private SingleDayHourPreference fridayPreference;

    @PrePersist
    public void preUpdate() {
        changed = LocalDateTime.now();
    }

    public static UserWorkingHoursPreference getDefault(Long userId) {
        var preference = new UserWorkingHoursPreference();
        preference.setUserId(userId);
        preference.setMondayPreference(SingleDayHourPreference.getDefault());
        preference.setTuesdayPreference(SingleDayHourPreference.getDefault());
        preference.setWednesdayPreference(SingleDayHourPreference.getDefault());
        preference.setThursdayPreference(SingleDayHourPreference.getDefault());
        preference.setFridayPreference(SingleDayHourPreference.getDefault());
        return preference;
    }

    @Transient
    public Optional<SingleDayHourPreference> getDayPreferenceBy(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> Optional.of(mondayPreference);
            case TUESDAY -> Optional.of(tuesdayPreference);
            case WEDNESDAY -> Optional.of(wednesdayPreference);
            case THURSDAY -> Optional.of(thursdayPreference);
            case FRIDAY -> Optional.of(fridayPreference);
            default -> Optional.empty();
        };
    }

    @Transient
    public void setDayPreferenceBy(DayOfWeek dayOfWeek, SingleDayHourPreference singleDayHourPreference) {
        switch (dayOfWeek) {
            case MONDAY:
                setMondayPreference(singleDayHourPreference);
                break;
            case TUESDAY:
                setTuesdayPreference(singleDayHourPreference);
                break;
            case WEDNESDAY:
                setWednesdayPreference(singleDayHourPreference);
                break;
            case THURSDAY:
                setThursdayPreference(singleDayHourPreference);
                break;
            case FRIDAY:
                setFridayPreference(singleDayHourPreference);
                break;
            default:
                break;
        }
    }

    @Transient
    public boolean isNonWorkingOn(DayOfWeek dayOfWeek) {
        return getDayPreferenceBy(dayOfWeek)
                .map(SingleDayHourPreference::getNonWorking)
                .orElse(false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserWorkingHoursPreference that = (UserWorkingHoursPreference) o;
        return getUserId().equals(that.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId());
    }
}
