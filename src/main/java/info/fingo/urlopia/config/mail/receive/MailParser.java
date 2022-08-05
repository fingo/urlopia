package info.fingo.urlopia.config.mail.receive;

import info.fingo.urlopia.api.v2.anonymizer.Anonymizer;
import info.fingo.urlopia.config.mail.Mail;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parsing e-mails
 * Sending requests to the database
 */
@Service
@Slf4j
public class MailParser {

    private LocalDate startDate;
    private LocalDate endDate;
    private boolean correct;
    private String emailContent;
    private String startDateS;
    private String endDateS;
    private long id;
    private boolean isReply;
    private String reply;

    public boolean parseContent(Mail mail) {
        String[] emailLines = splitByLines(mail.getContent());

        var stringBuilder = new StringBuilder();
        for (String line : emailLines) {
            stringBuilder.append(line);
        }
        emailContent = stringBuilder.toString();

        correct = true;
        findDate();
        if (correct) {
            convertDate();
        }

        if (correct && !checkDate(startDate, endDate)) {
            var loggerInfo = "Could not parse content of mail sent from: %s."
                    .formatted(Anonymizer.anonymizeMail(mail.getSenderAddress()));
            log.warn(loggerInfo);
            return false;
        }

        return correct;
    }

    private String[] splitByLines(String text) {
        return text.split("\\r\\n|\\n|\\r");
    }

    public void parseSubject(Mail mail) {
        var subject = mail.getSubject();

        if (subject == null) {
            return;
        }

        var pattern = Pattern.compile(".*(re:|odp.:|odp:|odpowiedź:|reply:|response:).*", Pattern.CANON_EQ);
        var matcher = pattern.matcher(subject.toLowerCase());
        isReply = matcher.matches();

        if (isReply) {
            var b1 = subject.indexOf('[');
            var b2 = subject.indexOf(']');
            if (b1 != -1 && b2 != -1) {
                id = Long.parseLong(subject.substring(b1 + 1, b2));
            }
        }
    }

    public void parseReply(Mail mail) {
        var emailLines = splitByLines(mail.getContent());
        reply = emailLines[0];
    }

    private void convertDate() {
        Map<String, String> formats = new LinkedHashMap<>();
        formats.put("\\d{1,2}?\\.\\d{2}?\\.\\d{4}?", "d.MM.yyyy");
        formats.put("\\d{1,2}?-\\d{2}?-\\d{4}?", "d-MM-yyyy");
        formats.put("\\d{1,2}?/\\d{2}?/\\d{4}?", "d/MM/yyyy");
        formats.put("\\d{1,2}? \\w{3}? \\d{4}?", "d MMM yyyy");
        formats.put("\\d{1,2}?\\.\\d{2}?\\.\\d{2}?", "d.MM.yy");
        formats.put("\\d{1,2}?-\\d{2}?-\\d{2}?", "d-MM-yy");
        formats.put("\\d{1,2}?/\\d{2}?/\\d{2}?", "d/MM/yy");
        formats.put("\\d{1,2}? \\w{3}? \\d{2}?", "d MMM yy");

        for (var format : formats.entrySet()) {
            var pattern = Pattern.compile(format.getKey());
            if (pattern.matcher(startDateS).matches()) {
                correct = convertSingleDate(format.getValue());
            }
        }

        if (!correct) {
            log.error("Date is inconvertible!");
        }
    }

    private void findDate() {
        var pattern = "(\\A|.*[\\D]+)(\\d{1,2}?[-./]\\d{2}?[-./](?:\\d{4}|\\d{2}))\\D+(\\d{1,2}?[-./]\\d{2}?[-./](?:\\d{4}|\\d{2})).*";
        var p = Pattern.compile(pattern);
        var m = p.matcher(emailContent);
        if (m.matches()) {
            startDateS = m.group(2);
            endDateS = m.group(3);
            return;
        }
       var normalizedContent = Normalizer.normalize(emailContent, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        pattern = "(\\A|.*[\\D]+)(\\d{1,2}?\\W)([\\wźśń]{3,}?)(\\W(?:\\d{4}|\\d{2})).*\\D+(\\d{1,2}?\\W)([\\wźśń]{3,}?)(\\W(?:\\d{4}|\\d{2})).*";
        p = Pattern.compile(pattern);
        m = p.matcher(normalizedContent);
        Map<String, String> monthNameMap = getMonthNameMap();
        String month;

        if (m.matches()) {
            month = m.group(3).substring(0, 3);
            var tmpMonth = monthNameMap.get(month);
            if (tmpMonth != null)
                month = tmpMonth;
            startDateS = m.group(2) + month + m.group(4);

            month = m.group(6).substring(0, 3);
            tmpMonth = monthNameMap.get(month);
            if (tmpMonth != null)
                month = tmpMonth;
            endDateS = m.group(5) + month + m.group(7);
            return;
        }
        log.error("Date not found!");
        correct = false;
    }

    private Map<String, String> getMonthNameMap() {
        Map<String, String> monthNameMap = new HashMap<>();
        monthNameMap.put("sty", "Jan");
        monthNameMap.put("lut", "Feb");
        monthNameMap.put("mar", "Mar");
        monthNameMap.put("kwi", "Apr");
        monthNameMap.put("maj", "May");
        monthNameMap.put("cze", "Jun");
        monthNameMap.put("lip", "Jul");
        monthNameMap.put("sie", "Aug");
        monthNameMap.put("wrz", "Sep");
        monthNameMap.put("paz", "Oct");
        monthNameMap.put("lis", "Nov");
        monthNameMap.put("gru", "Dec");
        return monthNameMap;
    }

    private boolean convertSingleDate(String format) {
        try {
            var formatter = DateTimeFormatter.ofPattern(format, Locale.US);
            startDate = LocalDate.parse(startDateS, formatter);
            endDate = LocalDate.parse(endDateS, formatter);
            return true;
        } catch (DateTimeParseException e) {
            log.info(e.getMessage(), e);
            return false;
        }
    }

    public boolean isAcceptedByMail(String decision) {
        decision = decision.trim().toLowerCase();
        return decision.matches("(tak|yes|ok).*");
    }

    public boolean isRejectedByMail(String decision) {
        decision = decision.trim().toLowerCase();
        return decision.matches("(nie|no).*");
    }

    public boolean checkDate(LocalDate startDate, LocalDate endDate) {
        return !endDate.isBefore(startDate) && !startDate.isBefore(LocalDate.now().minusDays(30));
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public long getId() {
        return id;
    }

    public boolean isReply() {
        return isReply;
    }

    public String getReply() {
        return reply;
    }

    public void clear() {
        this.isReply = false;
        this.correct = true;
        this.startDate = null;
        this.startDateS = null;
        this.endDate = null;
        this.endDateS = null;
        this.emailContent = null;
        this.reply = null;
        this.id = 0;
    }
}