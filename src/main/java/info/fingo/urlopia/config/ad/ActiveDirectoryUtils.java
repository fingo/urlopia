package info.fingo.urlopia.config.ad;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ActiveDirectoryUtils {
    public static final List<String> DISABLED_STATUS = List.of("514", "546");


    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private ActiveDirectoryUtils() {
        // private constructor to prevent creating *Utils* class
    }

    public static String pickAttribute(SearchResult result,
                                       info.fingo.urlopia.config.ad.Attribute attribute) {
        Attributes attributes = result.getAttributes();
        Attribute receivedAttribute = attributes.get(attribute.getKey());
        return Optional.ofNullable(receivedAttribute)
                .map(Object::toString)
                .map(value -> value.substring(value.indexOf(':') + 2))  // TODO: think about using get method
                .orElse("");
    }

    public static String[] split(String stringGroup) {
        return stringGroup.split(", (?=CN=)");
    }

    public static LocalDateTime convertToLocalDateTime(String time) {
        time = time.substring(0, time.indexOf("."));
        return LocalDateTime.parse(time, dateTimeFormatter);
    }

    public static boolean isDisabled(SearchResult searchResult){
        var accountStatus = pickAttribute(searchResult,
                info.fingo.urlopia.config.ad.Attribute.USER_ACCOUNT_CONTROL);
        return DISABLED_STATUS.contains(accountStatus);
    }

}
