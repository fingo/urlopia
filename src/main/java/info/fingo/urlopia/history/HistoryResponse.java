package info.fingo.urlopia.history;


import info.fingo.urlopia.request.AcceptanceDTO;
import info.fingo.urlopia.request.RequestDTO;
import info.fingo.urlopia.request.RequestResponse;
import info.fingo.urlopia.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author Tomasz Pilarczyk
 */
public class HistoryResponse {

    private String deciderName;
    private String created;
    private float hours;
    private float hoursLeft;
    private float workTime;
    private RequestResponse request;
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
        this.workTime = historyDTO.getUser().getWorkTime();
        this.created = historyDTO.getCreated().format(formatter);
        this.hours = historyDTO.getHours();

        if (historyDTO.getRequest() != null) {
            this.request = new RequestResponse(historyDTO.getRequest(), historyDTO.getAcceptances());
            this.acceptances = historyDTO.getAcceptances();
        }

        this.hoursLeft = hoursLeft;
        this.comment = historyDTO.getComment();
    }

    private String printDays(float hours) {
        StringBuilder printing = new StringBuilder();

        if (hours > 0) {
            printing.append('+');
        } else if (hours < 0) {
            printing.append('-');
        }

        hours = Math.abs(hours);
        printing.append((int) Math.floor(hours / workTime)).append("d ");
        printing.append(Math.round(hours % workTime)).append('h');

        return printing.toString();
    }

    public String getDays() {
        return printDays(hours);
    }

    public String getDaysLeft() {
        return printDays(hoursLeft).replace("+", "");
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
}
