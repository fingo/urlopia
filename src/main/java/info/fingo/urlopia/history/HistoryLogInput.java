package info.fingo.urlopia.history;

import info.fingo.urlopia.api.v2.automatic.vacation.days.model.AutomaticVacationDay;

import java.time.LocalDateTime;

public class HistoryLogInput {
    private static final String AUTOMATIC_ADD_MESSAGE_TEMPLATE = "Automatycznie dodano dni urlopowe dnia: %s";
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

    public static HistoryLogInput from(AutomaticVacationDay automaticVacationDay){
        var input = new HistoryLogInput();
        input.setHours(automaticVacationDay.getNextYearHoursProposition().floatValue());
        input.setComment(AUTOMATIC_ADD_MESSAGE_TEMPLATE.formatted(LocalDateTime.now()));
        return input;
    }
}
