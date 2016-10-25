package info.fingo.urlopia.history;


import info.fingo.urlopia.request.AcceptanceDTO;
import info.fingo.urlopia.request.RequestDTO;
import info.fingo.urlopia.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author Tomasz Pilarczyk
 */
public class HistoryResponse {

    @Autowired
    private UserService userService;

    private String deciderName;
    private String created;
    private float hours;
    private float hoursLeft;
    private float workTime;
    private String days;
    private String daysLeft;
    private RequestDTO request;
    private List<AcceptanceDTO> acceptances;
    private int type;
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
        this.request = historyDTO.getRequest();
        this.acceptances = historyDTO.getAcceptances();
        this.type = historyDTO.getType();
        this.comment = historyDTO.getComment();
    }

    public HistoryResponse(HistoryDTO historyDTO, float hoursLeft) {
        if (historyDTO.getDecider() != null) {
            this.deciderName = historyDTO.getDecider().getName();
        } else {
            this.deciderName = null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.workTime = historyDTO.getUser().getWorkTime();
        this.created = historyDTO.getCreated().format(formatter);
        this.hours = historyDTO.getHours();
        this.request = historyDTO.getRequest();
        this.acceptances = historyDTO.getAcceptances();
        this.type = historyDTO.getType();
        this.hoursLeft = hoursLeft;
        this.comment = historyDTO.getComment();
    }

    private String printDays(float hours) {
        int lDays;
        if (hours >= 0) lDays = (int) Math.floor(hours / workTime);
        else {
            lDays = (int) Math.ceil(hours / workTime);
        }
        double lHours;
        int lHoursWithoutMinutes;
        lHours = Math.round((hours % workTime) * 100.0) / 100.0;
        lHoursWithoutMinutes = Math.round(hours % workTime);
        if ((lHours * 10) % 10 != 0) {
            return lDays + "d " + lHours + "h";
        } else {
            return lDays + "d " + lHoursWithoutMinutes + "h";
        }
    }

    public String getDays() {
        return printDays(hours);
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getDaysLeft() {
        return printDays(hoursLeft);
    }

    public void setDaysLeft(String daysLeft) {
        this.daysLeft = daysLeft;
    }

    public float getHoursLeft() {
        return hoursLeft;
    }

    public void setHoursLeft(float hoursLeft) {
        this.hoursLeft = hoursLeft;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public float getHours() {
        return hours;
    }

    public void setHours(float hours) {
        this.hours = hours;
    }

    public RequestDTO getRequest() {
        return request;
    }

    public void setRequest(RequestDTO request) {
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
}
