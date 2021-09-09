package info.fingo.urlopia.api.v2.anonymizer;

import org.apache.commons.lang3.StringUtils;

public class Anonymizer {
    private static final Integer MAX_LENGTH_OF_FULLY_ANONYMIZED_NAME_IN_MAIL = 3;
    private static final Integer MAX_LENGTH_OF_FULLY_ANONYMIZED_SUBJECT = 20;
    private static final Integer LENGTH_OF_SUBJECT_SUBSTRINGS = 5;

    private Anonymizer() {}
    public static String anonymizeMail(String mail) {
        var partsOfMail = mail.split("@");
        var name = partsOfMail[0];
        var length = name.length();
        if (length <= MAX_LENGTH_OF_FULLY_ANONYMIZED_NAME_IN_MAIL) {
            return StringUtils.repeat('.', length) + "@" + partsOfMail[1];
        }
        return name.charAt(0) +
                StringUtils.repeat('.', length-2) +
                name.charAt(length-1) +
                "@" +
                partsOfMail[1];
    }

    public static String anonymizeSubject(String subject) {
        var length = subject.length();
        if (length <= MAX_LENGTH_OF_FULLY_ANONYMIZED_SUBJECT) {
            return StringUtils.repeat('.', length);
        }
        return subject.substring(0, LENGTH_OF_SUBJECT_SUBSTRINGS) +
                StringUtils.repeat('.', length - 2 * LENGTH_OF_SUBJECT_SUBSTRINGS) +
                subject.substring(subject.length() - LENGTH_OF_SUBJECT_SUBSTRINGS);
    }

    public static String anonymizeYearlyReportFileName(String fileName) {
        var lastOccurrence = fileName.lastIndexOf('_');
        return fileName.substring(0, lastOccurrence + 1) + "....xlsx";
    }
}
