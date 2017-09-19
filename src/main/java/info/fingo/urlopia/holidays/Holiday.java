package info.fingo.urlopia.holidays;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Holidays")
public class Holiday {

    @Id
    @SequenceGenerator(name = "holidays_id_seq", sequenceName = "holidays_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "holidays_id_seq")
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate date;

    /**
     * Default constructor only exists for the sake of JPA
     */
    protected Holiday() {
    }

    public Holiday(String name, LocalDate date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
