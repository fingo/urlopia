package info.fingo.urlopia.converter;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * @author Mateusz Wiśniewski
 */
public class LocalDateAttributeConverterTest {

    @Test
    public void convertToDatabaseColumnTest() {
        long current = System.currentTimeMillis();
        LocalDate localDate = Instant.ofEpochMilli(current).atZone(ZoneId.systemDefault()).toLocalDate();
        Date sqlDate = new LocalDateAttributeConverter().convertToDatabaseColumn(localDate);

        Assert.assertEquals(localDate.getYear(), sqlDate.getYear() + 1900);
        Assert.assertEquals(localDate.getMonthValue(), sqlDate.getMonth() + 1);
        Assert.assertEquals(localDate.getDayOfMonth(), sqlDate.getDate());
    }

    @Test
    public void convertToEntityAttributeTest() {
        long current = System.currentTimeMillis();
        Date sqlDate = new Date(current);
        LocalDate localDate = new LocalDateAttributeConverter().convertToEntityAttribute(sqlDate);

        Assert.assertEquals(sqlDate.getYear(), localDate.getYear() - 1900);
        Assert.assertEquals(sqlDate.getMonth(), localDate.getMonthValue() - 1);
        Assert.assertEquals(sqlDate.getDate(), localDate.getDayOfMonth());
    }
}
