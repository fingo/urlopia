package info.fingo.urlopia.api.v2.automatic.vacation.days;

import info.fingo.urlopia.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "automatic_vacation_days")
@Getter
@Setter
public class AutomaticVacationDays {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_sequence")
    @SequenceGenerator(name = "id_sequence", sequenceName = "seq_automatic_vacation_days", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(nullable = false)
    private LocalDateTime modified;

    @Column(nullable = false)
    private Integer nextYearDaysBase = 26; //default value

    @Column(nullable = false)
    private Double nextYearHoursProposition = 0.0;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    public AutomaticVacationDays() {
        //required by hibernate
    }

    public AutomaticVacationDays(User user) {
        this.user = user;
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
    }
}
