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

        String sLocalDate = localDate.toString();
        String sSqlDate = sqlDate.toString();

        Assert.assertEquals(sLocalDate, sSqlDate);
    }

    @Test
    public void convertToEntityAttributeTest() {
        long current = System.currentTimeMillis();
        Date sqlDate = new Date(current);
        LocalDate localDate = new LocalDateAttributeConverter().convertToEntityAttribute(sqlDate);

        String sSqlDate = sqlDate.toString();
        String sLocalDate = localDate.toString();

        Assert.assertEquals(sSqlDate, sLocalDate);
    }
}
