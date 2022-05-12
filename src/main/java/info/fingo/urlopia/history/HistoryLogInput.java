package info.fingo.urlopia.history;

public class HistoryLogInput {
    private Float hours;
    private String comment;

    private Boolean countForNextYear = false;

    public Boolean getCountForNextYear() {
        return countForNextYear;
    }

    public void setCountForNextYear(Boolean countForNextYear) {
        this.countForNextYear = countForNextYear;
    }

    public Float getHours() {
        return hours;
    }

    public void setHours(Float hours) {
        this.hours = hours;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
