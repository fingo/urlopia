package info.fingo.urlopia.request.normal;

public class DayHourTime {

    private int days;

    private double hours;

    public static DayHourTime of(int days,
                                  double hours) {
        return new DayHourTime(days, hours);
    }

    private DayHourTime(int days,
                        double hours) {
        this.days = days;
        this.hours = hours;
    }

    public int getDays() {
        return days;
    }

    public double getHours() {
        return hours;
    }
}
