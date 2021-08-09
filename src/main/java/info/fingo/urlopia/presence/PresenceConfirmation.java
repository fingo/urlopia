package info.fingo.urlopia.presence;

import info.fingo.urlopia.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "presence_confirmations")
@Getter
@NoArgsConstructor // Required for hibernate
public class PresenceConfirmation {
    @Id
    @SequenceGenerator(name = "presence_confirmations_id_seq", sequenceName = "presence_confirmations_id_seq",
                       allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "presence_confirmations_id_seq")
    private Long id;

    @Column(nullable = false)
    @Setter
    private LocalDate date;

    @Column(nullable = false)
    @Setter
    private LocalTime startTime;

    @Column(nullable = false)
    @Setter
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(nullable = false)
    @Setter
    private User user;

    public PresenceConfirmation(User user,
                                LocalDate date,
                                LocalTime startTime,
                                LocalTime endTime) {
        this.user = user;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public PresenceConfirmation(Long id,
                                User user,
                                LocalDate date,
                                LocalTime startTime,
                                LocalTime endTime) {
        this(user, date, startTime, endTime);
        this.id = id;
    }
}
