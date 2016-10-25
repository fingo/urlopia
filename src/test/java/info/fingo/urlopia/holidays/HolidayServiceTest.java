package info.fingo.urlopia.holidays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jakub Licznerski
 *         Created on 31.08.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class HolidayServiceTest {

    //inner class mocks equals method for testing convenience
    private class HolidayMock extends Holiday {

        public HolidayMock (String name, LocalDate date){
            super(name, date);
        }

        @Override
        public boolean equals (Object holiday){
            return this.getDate().equals(((Holiday)holiday).getDate());
        }
    }

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
        List<Holiday> holidayList = holidayService.generateHolidaysList(LocalDate.now().getYear());
        holidayList.addAll(holidayService.generateHolidaysList(LocalDate.now().getYear()+1));

        List<HolidayMock> test_holidays = new ArrayList<>(holidayList.size());
        final int[][] static_holidays = {{1, 1}, {1, 6}, {5, 1}, {5, 3}, {8, 15}, {11, 1}, {11, 11}, {12, 25}, {12, 26}};


        for (int i = 0; i < static_holidays.length; i++) {
            test_holidays.add(new HolidayMock("", LocalDate.of(2016, static_holidays[i][0], static_holidays[i][1])));
            test_holidays.add(new HolidayMock("", LocalDate.of(2017, static_holidays[i][0], static_holidays[i][1])));
        }

        test_holidays.add(new HolidayMock("", LocalDate.of(2016, 3, 27)));
        test_holidays.add(new HolidayMock("", LocalDate.of(2016, 3, 28)));
        test_holidays.add(new HolidayMock("", LocalDate.of(2016, 5, 15)));
        test_holidays.add(new HolidayMock("", LocalDate.of(2016, 5, 26)));

        test_holidays.add(new HolidayMock("", LocalDate.of(2017, 4, 16)));
        test_holidays.add(new HolidayMock("", LocalDate.of(2017, 4, 17)));
        test_holidays.add(new HolidayMock("", LocalDate.of(2017, 6, 4)));
        test_holidays.add(new HolidayMock("", LocalDate.of(2017, 6, 15)));

        Assert.assertTrue(holidayList.containsAll(test_holidays)); //collection.contains(h) uses h.equals(collection_element)

    }

}