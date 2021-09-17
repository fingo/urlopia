package info.fingo.urlopia.api.v2.presence;

import info.fingo.urlopia.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "presence_confirmations")
@Getter
@NoArgsConstructor // Required for hibernate
public class PresenceConfirmation {
    @EmbeddedId
    private PresenceConfirmationId presenceConfirmationId;

    @Column(nullable = false)
    @Setter
    private LocalTime startTime;

    @Column(nullable = false)
    @Setter
    private LocalTime endTime;

    public PresenceConfirmation(User user,
                                LocalDate date,
                                LocalTime startTime,
                                LocalTime endTime) {
        this.presenceConfirmationId = PresenceConfirmationId.from(user, date);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static PresenceConfirmation empty(User user, LocalDate date) {
        return new PresenceConfirmation(user, date, LocalTime.MIN, LocalTime.MIN);
    }

    public LocalDate getDate() {
        return presenceConfirmationId.getDate();
    }

    public Long getUserId() {
        return presenceConfirmationId.getUserId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PresenceConfirmation that = (PresenceConfirmation) o;
        return getPresenceConfirmationId().equals(that.getPresenceConfirmationId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPresenceConfirmationId());
    }
}
