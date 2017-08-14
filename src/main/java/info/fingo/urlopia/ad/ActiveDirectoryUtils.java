package info.fingo.urlopia.ad;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ActiveDirectoryUtils {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private ActiveDirectoryUtils() {
        // private constructor to prevent creating *Utils* class
    }

    public static String pickAttribute(SearchResult result, info.fingo.urlopia.ad.Attribute attribute) {
        Attributes attributes = result.getAttributes();
        Attribute receivedAttribute = attributes.get(attribute.getKey());
        return Optional.ofNullable(receivedAttribute)
                .map(Object::toString)
                .map(value -> value.substring(value.indexOf(':') + 2))  // TODO: think about using get method
                .orElse("");
    }

    public static LocalDateTime convertToLocalDateTime(String time) {
        time = time.substring(0, time.indexOf("."));
        return LocalDateTime.parse(time, dateTimeFormatter);
    }

}
