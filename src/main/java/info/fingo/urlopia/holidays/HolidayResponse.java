package info.fingo.urlopia.holidays;

import java.time.ZoneId;

/**
 * @author Jakub Licznerski
 *         Created on 30.08.2016.
 */
public class HolidayResponse {
    private String name;
    private long date;

    //default constructor to deserialize data from javascript
    public HolidayResponse() {

    }

    public HolidayResponse(String name, long date) {
        this.name = name;
        this.date = date;
    }

    public HolidayResponse(Holiday holiday) {
        this.name = holiday.getName();
        this.date = holiday.getDate().atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
