package info.fingo.urlopia.request;

import java.time.LocalDate;
import java.util.List;

public class VacationDay {

    private final LocalDate date;

    private final List<String> userNames;

    public VacationDay(LocalDate date, List<String> userNames) {
        this.date = date;
        this.userNames = userNames;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public List<String> getUserNames() {
        return this.userNames;
    }

}
