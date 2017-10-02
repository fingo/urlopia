package info.fingo.urlopia.holidays;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;

/**
 * @author Jakub
 *         Created on 30.08.2016.
 */
public class HolidayResponseTest {

    @Test @Ignore
    public void fromEntityTest(){
        LocalDate date = LocalDate.of(1995, 12, 30);

        Holiday holiday = new Holiday("swieto", date);
        HolidayResponse response = new HolidayResponse(holiday);


        Assert.assertEquals("Data: " + date.toString(), Long.parseLong("820278000000"), response.getDate()); //value from milis time generator
    }
}