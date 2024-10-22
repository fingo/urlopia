package info.fingo.urlopia.config.ad;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
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

    public static String getRelativeDN(String distinguishedName,
                                       String base) {
        if (distinguishedName.equals(base)) {
            return "";
        } else if(distinguishedName.endsWith(base) && !base.isBlank()) {
            return distinguishedName.substring(0, distinguishedName.length() - base.length() - 1); // -1 for comma
        } else {
            return distinguishedName;
        }
    }

    public static String getParentDN(String distinguishedName) {
        var commasIgnoringEscapedRegex = "(?<!\\\\),";
        var dnParts = Arrays.stream(distinguishedName.split(commasIgnoringEscapedRegex)).toList();
        if (dnParts.size() == 1) {
            return "";
        } else {
            return String.join(",", dnParts.subList(1, dnParts.size()));
        }
    }

    public static boolean isOU(SearchResult object) {
        return isObjectClass(object, ActiveDirectoryObjectClass.ORGANIZATIONAL_UNIT);
    }

    public static boolean isPerson(SearchResult object) {
        return isObjectClass(object, ActiveDirectoryObjectClass.PERSON);
    }

    public static boolean isObjectClass(SearchResult object, ActiveDirectoryObjectClass objectClass) {
        var objectClasses = pickAttribute(object, info.fingo.urlopia.config.ad.Attribute.OBJECT_CLASS);
        return Arrays.stream(objectClasses.split(","))
                .map(String::trim)
                .anyMatch(oc -> oc.equalsIgnoreCase(objectClass.getKey()));
    }

}
