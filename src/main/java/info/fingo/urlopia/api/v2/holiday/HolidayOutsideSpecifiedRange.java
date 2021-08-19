package info.fingo.urlopia.api.v2.holiday;

import info.fingo.urlopia.api.v2.BaseCustomException;
import org.springframework.http.HttpStatus;

public class HolidayOutsideSpecifiedRange extends BaseCustomException {
    private static final String ERROR_MESSAGE = "Holidays are not in specified time period";

    private HolidayOutsideSpecifiedRange(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    public static HolidayOutsideSpecifiedRange holidaysOutsideTimePeriod(){
        return new HolidayOutsideSpecifiedRange(ERROR_MESSAGE);
    }

}
