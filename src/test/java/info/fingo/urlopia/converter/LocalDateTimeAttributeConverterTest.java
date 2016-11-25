package info.fingo.urlopia.converter;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 * @author Mateusz Wiśniewski
 */
public class LocalDateTimeAttributeConverterTest {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Test
    public void convertToDatabaseColumnTest() {
        long current = System.currentTimeMillis();
        LocalDateTime localDateTime = Instant.ofEpochMilli(current).atZone(ZoneId.systemDefault()).toLocalDateTime();
        Timestamp sqlTimestamp = new LocalDateTimeAttributeConverter().convertToDatabaseColumn(localDateTime);

        Assert.assertEquals(localDateTime.getYear(), sqlTimestamp.getYear() + 1900);
        Assert.assertEquals(localDateTime.getMonthValue(), sqlTimestamp.getMonth() + 1);
        Assert.assertEquals(localDateTime.getDayOfMonth(), sqlTimestamp.getDate());
        Assert.assertEquals(localDateTime.getHour(), sqlTimestamp.getHours());
        Assert.assertEquals(localDateTime.getMinute(), sqlTimestamp.getMinutes());
        Assert.assertEquals(localDateTime.getSecond(), sqlTimestamp.getSeconds());
    }

    @Test
    public void convertToEntityAttributeTest() {
        long current = System.currentTimeMillis();
        Timestamp sqlTimestamp = new Timestamp(current);
        LocalDateTime localDateTime = new LocalDateTimeAttributeConverter().convertToEntityAttribute(sqlTimestamp);

        Assert.assertEquals(sqlTimestamp.getYear(), localDateTime.getYear() - 1900);
        Assert.assertEquals(sqlTimestamp.getMonth(), localDateTime.getMonthValue() - 1);
        Assert.assertEquals(sqlTimestamp.getDate(), localDateTime.getDayOfMonth());
        Assert.assertEquals(sqlTimestamp.getHours(), localDateTime.getHour());
        Assert.assertEquals(sqlTimestamp.getMinutes(), localDateTime.getMinute());
        Assert.assertEquals(sqlTimestamp.getSeconds(), localDateTime.getSecond());
    }
}
