package info.fingo.urlopia.request;

import info.fingo.urlopia.request.occasional.OccasionalType;

import java.time.LocalDate;

public class RequestInput {
    private LocalDate startDate;
    private LocalDate endDate;
    private RequestType type;
    private OccasionalType occasionalType;  // TODO: remove OccasionalType from here

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

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public OccasionalType getOccasionalType() {
        return occasionalType;
    }

    public void setOccasionalType(OccasionalType occasionalType) {
        this.occasionalType = occasionalType;
    }
}
