package info.fingo.urlopia.converter;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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

        String sLocalDateTime = localDateTime.format(formatter);
        String sSqlTimestamp = sqlTimestamp.toString();

        Assert.assertEquals(sLocalDateTime, sSqlTimestamp);
    }

    @Test
    public void convertToEntityAttributeTest() {
        long current = System.currentTimeMillis();
        Timestamp sqlTimestamp = new Timestamp(current);
        LocalDateTime localDateTime = new LocalDateTimeAttributeConverter().convertToEntityAttribute(sqlTimestamp);

        String sSqlTimestamp = sqlTimestamp.toString();
        String sLocalDateTime = localDateTime.format(formatter);

        Assert.assertEquals(sSqlTimestamp, sLocalDateTime);
    }
}
