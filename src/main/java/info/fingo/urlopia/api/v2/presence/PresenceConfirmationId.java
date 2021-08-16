package info.fingo.urlopia.api.v2.presence;

import info.fingo.urlopia.user.User;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor // Required by hibernate
@EqualsAndHashCode // Required by hibernate (entity id should implement equals and hashCode)
public class PresenceConfirmationId implements Serializable {
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDate date;

    public static PresenceConfirmationId from(Long userId, LocalDate date) {
        return new PresenceConfirmationId(userId, date);
    }

    public static PresenceConfirmationId from(User user, LocalDate date) {
        return PresenceConfirmationId.from(user.getId(), date);
    }
}
