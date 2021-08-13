package info.fingo.urlopia.api.v2.holiday;

public class HolidayOutsideSpecifiedRange extends RuntimeException {
    private static final String ERROR_MESSAGE = "Holidays are not in specified time period";

    private HolidayOutsideSpecifiedRange(String errorMessage) {
        super(errorMessage);
    }

    public static HolidayOutsideSpecifiedRange holidaysOutsideTimePeriod(){
        return new HolidayOutsideSpecifiedRange(ERROR_MESSAGE);
    }

}
