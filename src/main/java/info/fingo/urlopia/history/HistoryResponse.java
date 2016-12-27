package info.fingo.urlopia.history;


import info.fingo.urlopia.request.AcceptanceDTO;
import info.fingo.urlopia.request.RequestResponse;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Tomasz Pilarczyk
 */
public class HistoryResponse {

    private static final float FULL_TIME = 8f;

    private String deciderName;
    private String created;
    private float hours;
    private float hoursLeft;
    private float workTime;
    private float workTimeNominator;
    private float workTimeDenominator;
    private RequestResponse request;
    private List<AcceptanceDTO> acceptances;
    private String comment;

    public HistoryResponse(HistoryDTO historyDTO) {
        if (historyDTO.getDecider() != null) {
            this.deciderName = historyDTO.getDecider().getName();
        } else {
            this.deciderName = null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.created = historyDTO.getCreated().format(formatter);
        this.hours = historyDTO.getHours();

        if (historyDTO.getRequest() != null) {
            this.request = new RequestResponse(historyDTO.getRequest(), historyDTO.getAcceptances());
            this.acceptances = historyDTO.getAcceptances();
        }

        this.comment = historyDTO.getComment();
    }

    public HistoryResponse(HistoryDTO historyDTO, float hoursLeft) {
        if (historyDTO.getDecider() != null) {
            this.deciderName = historyDTO.getDecider().getName();
        } else {
            this.deciderName = null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.workTime = historyDTO.getWorkTime();
        createFraction(workTime);
        this.created = historyDTO.getCreated().format(formatter);
        this.hours = historyDTO.getHours();

        if (historyDTO.getRequest() != null) {
            this.request = new RequestResponse(historyDTO.getRequest(), historyDTO.getAcceptances());
            this.acceptances = historyDTO.getAcceptances();
        }

        this.hoursLeft = hoursLeft;
        this.comment = historyDTO.getComment();
    }

    private void createFraction(float value) {
        value = value / 8;

        float denominator = 0;
        float nominator;

        do {
            denominator++;
            nominator = value * denominator;
        } while (Math.floor(nominator) != nominator);

        this.workTimeNominator = nominator;
        this.workTimeDenominator = denominator;
    }

    private String print(float hours, TimeUnit unit) {
        StringBuilder printing = new StringBuilder();

        if (hours > 0) {
            printing.append('+');
        } else if (hours < 0) {
            printing.append('-');
        }

        hours = Math.abs(hours);

        if(unit == TimeUnit.DAYS) {
            printing.append((int) Math.floor(hours / workTime)).append("d ");
            hours %= workTime;
        }

        if (hours == (long) hours ) {
            printing.append((long) hours).append('h');
        } else {
            printing.append(Math.round(hours * 100f) / 100f).append('h');
        }

        return printing.toString();
    }

    public String getDays() {
        return print(hours, TimeUnit.DAYS);
    }

    public String getDaysLeft() {
        return print(hoursLeft, TimeUnit.DAYS).replace("+", "");
    }

    public float getHoursLeft() {
        return hoursLeft;
    }

    public void setHoursLeft(float hoursLeft) {
        this.hoursLeft = hoursLeft;
    }

    public String getDeciderName() {
        return deciderName;
    }

    public void setDeciderName(String deciderName) {
        this.deciderName = deciderName;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getHours() {
        return print(hours, TimeUnit.HOURS);
    }

    public void setHours(float hours) {
        this.hours = hours;
    }

    public float getWorkTime() {
        return workTime;
    }

    public float getWorkTimeNominator() {
        return workTimeNominator;
    }

    public float getWorkTimeDenominator() {
        return workTimeDenominator;
    }

    public RequestResponse getRequest() {
        return request;
    }

    public void setRequest(RequestResponse request) {
        this.request = request;
    }

    public List<AcceptanceDTO> getAcceptances() {
        return acceptances;
    }

    public void setAcceptances(List<AcceptanceDTO> acceptances) {
        this.acceptances = acceptances;
    }

    public String getComment() {
        return comment;
    }

    public boolean isFullTime() {
        return workTime == this.FULL_TIME;
    }
}
