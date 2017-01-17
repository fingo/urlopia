package info.fingo.urlopia.holidays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jakub Licznerski
 *         Created on 31.08.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class HolidayServiceTest {

    @InjectMocks
    private HolidayService holidayService;

    @Test
    public void entityFromResponseTest() {
        //01/01/2000 in epoch = 946681200000
        HolidayResponse holiday = new HolidayResponse("swieto", Long.parseLong("946681200000"));

        Assert.assertEquals(new HolidayResponse(holidayService.entityFromResponse(holiday)).getDate(), Long.parseLong("946681200000"));
    }

    @Test
    public void generateHolidaysTest() {
        int currentYear = LocalDate.now().getYear();

        List<Holiday> holidayList = holidayService.generateHolidaysList(currentYear);
        holidayList.addAll(holidayService.generateHolidaysList(currentYear + 1));
        List<LocalDate> holidayDates = holidayList.stream().map(Holiday::getDate).collect(Collectors.toList());

        List<LocalDate> test_holidayDates = new ArrayList<>(holidayList.size());
        final int[][] static_holidays = {{1, 1}, {1, 6}, {5, 1}, {5, 3}, {8, 15}, {11, 1}, {11, 11}, {12, 25}, {12, 26}};

        for (int i = 0; i < static_holidays.length; i++) {
            test_holidayDates.add(LocalDate.of(currentYear, static_holidays[i][0], static_holidays[i][1]));
            test_holidayDates.add(LocalDate.of(currentYear + 1, static_holidays[i][0], static_holidays[i][1]));
        }

        Assert.assertTrue(holidayDates.containsAll(test_holidayDates)); //collection.contains(h) uses h.equals(collection_element)
    }

}