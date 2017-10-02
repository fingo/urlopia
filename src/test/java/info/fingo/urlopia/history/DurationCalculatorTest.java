package info.fingo.urlopia.history;

import info.fingo.urlopia.holidays.HolidayService;
import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.request.RequestDTO;
import info.fingo.urlopia.user.UserDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Józef Grodzicki
 */
public class DurationCalculatorTest {


    public RequestDTO request;

    private HolidayService mockedService;

    //methods needed for testing purpose (mocking initial data)
    private List<LocalDate> getHolidayDates() {
        int this_year = LocalDate.now().getYear();

        List<LocalDate> holidays = new ArrayList<>(26);

        //loop for this and next year
        for (int i = 0; i <= 1; i++) {
            holidays.add(LocalDate.of(this_year + i, 1, 1));
            holidays.add(LocalDate.of(this_year + i, 1, 6));
            holidays.add(LocalDate.of(this_year + i, 5, 1));
            holidays.add(LocalDate.of(this_year + i, 5, 3));
            holidays.add(LocalDate.of(this_year + i, 8, 15));
            holidays.add(LocalDate.of(this_year + i, 11, 1));
            holidays.add(LocalDate.of(this_year + i, 11, 11));
            holidays.add(LocalDate.of(this_year + i, 12, 25));
            holidays.add(LocalDate.of(this_year + i, 12, 26));

            LocalDate easter = getEaster(this_year + i);

            holidays.add(easter);
            holidays.add(easter.plusDays(1));
            holidays.add(easter.plusDays(49));
            holidays.add(easter.plusDays(60));
        }
        return holidays;
    }
    private LocalDate getEaster(int year) {
        // Easter
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int p = (h + l - 7 * m + 114) % 31;
        int easterDay = p + 1;
        int easterMonth = (h + l - 7 * m + 114) / 31;

        return LocalDate.of(year, easterMonth, easterDay);
    }

    @Before @Ignore
    public void setup() {
        mockedService = mock(HolidayService.class);
        when(mockedService.getAllHolidaysDates()).thenReturn(getHolidayDates());
    }

    @Test @Ignore
    public void moreDays() throws Exception {

        UserDTO user = new UserDTO(0L, "a@b.pl");
        user.setWorkTime(6.4f);
        request = new RequestDTO(0L, LocalDateTime.now(), LocalDateTime.now(),
                user, LocalDate.of(2016, 12, 31), LocalDate.of(2017, 1, 8), null, null, Request.Status.ACCEPTED);
        float workHours = DurationCalculator.calculate(request, mockedService);

        Assert.assertEquals(25.6f, workHours, 0);
    }

    @Test @Ignore
    public void oneDay() throws Exception {
        UserDTO user = new UserDTO(0L, "a@b.pl");
        user.setWorkTime(6.4f);
        request = new RequestDTO(0L, LocalDateTime.now(), LocalDateTime.now(),
                user, LocalDate.of(2017, 1, 10), LocalDate.of(2017, 1, 10), null, null, Request.Status.ACCEPTED);
        float workHours = DurationCalculator.calculate(request, mockedService);

        Assert.assertEquals(6.4f, workHours, 0);
    }
}