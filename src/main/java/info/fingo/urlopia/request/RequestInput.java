package info.fingo.urlopia.request;

import java.time.LocalDate;

public class RequestInput {
    private LocalDate startDate;
    private LocalDate endDate;
    private Request.Type type;
    private Request.OccasionalType occasionalType;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Request.Type getType() {
        return type;
    }

    public void setType(Request.Type type) {
        this.type = type;
    }

    public Request.OccasionalType getOccasionalType() {
        return occasionalType;
    }

    public void setOccasionalType(Request.OccasionalType occasionalType) {
        this.occasionalType = occasionalType;
    }
}
