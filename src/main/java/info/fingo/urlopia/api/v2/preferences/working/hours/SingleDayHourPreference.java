package info.fingo.urlopia.api.v2.preferences.working.hours;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "single_day_hour_preference")
@Getter
@NoArgsConstructor // Required for hibernate
public class SingleDayHourPreference {
    @Id
    @SequenceGenerator(name = "single_day_hour_preference_id_seq",
                       sequenceName = "single_day_hour_preference_id_seq",
                       allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY,
                    generator = "single_day_hour_preference_id_seq")
    private Long id;

    @Column(nullable = false)
    @Setter
    private Boolean nonWorking = false;

    @Column(nullable = false)
    @Setter
    private LocalTime startTime;

    @Column(nullable = false)
    @Setter
    private LocalTime endTime;

    public static SingleDayHourPreference getDefault() {
        var singleDayPreference = new SingleDayHourPreference();
        singleDayPreference.setStartTime(LocalTime.of(8, 0));
        singleDayPreference.setEndTime(LocalTime.of(16, 0));
        return singleDayPreference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleDayHourPreference that = (SingleDayHourPreference) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
