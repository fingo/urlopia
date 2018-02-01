package info.fingo.urlopia.config.mail.receive;

import info.fingo.urlopia.config.mail.Mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parsing e-mails
 * Sending requests to the database
 */
@Service
public class MailParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailParser.class);

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
        emailContent = "";

        for (String line : emailLines) {
            emailContent += line;
        }

        correct = true;
        findDate();
        if (correct) {
            convertDate();
        }

        if (correct && !checkDate(startDate, endDate)) {
            return false;
        }

        return correct;
    }

    private String[] splitByLines(String text) {
        return text.split("\\r\\n|\\n|\\r");
    }

    public void parseSubject(Mail mail) {
        String subject = mail.getSubject();

        if (subject == null) {
            return;
        }

        isReply = subject.toLowerCase().matches(".*(re:|odp.:|odp:).*");
        if (isReply) {
            int b1 = subject.indexOf('[');
            int b2 = subject.indexOf(']');
            id = Long.parseLong(subject.substring(b1 + 1, b2));
        }
    }

    public void parseReply(Mail mail) {
        String[] emailLines = splitByLines(mail.getContent());
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

        for (Map.Entry<String, String> format : formats.entrySet()) {
            Pattern pattern = Pattern.compile(format.getKey());
            if (pattern.matcher(startDateS).matches()) {
                correct = convertSingleDate(format.getValue());
            }
        }

        if (!correct) {
            LOGGER.error("Date is inconvertible!");
        }
    }

    private void findDate() {
        String pattern = "(\\A|.*[\\D]+)(\\d{1,2}?[-./]\\d{2}?[-./](?:\\d{4}|\\d{2}))\\D+(\\d{1,2}?[-./]\\d{2}?[-./](?:\\d{4}|\\d{2})).*";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(emailContent);
        if (m.matches()) {
            startDateS = m.group(2);
            endDateS = m.group(3);
            return;
        }
        String normalizedContent = Normalizer.normalize(emailContent, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        pattern = "(\\A|.*[\\D]+)(\\d{1,2}?\\W)([\\wźśń]{3,}?)(\\W(?:\\d{4}|\\d{2})).*\\D+(\\d{1,2}?\\W)([\\wźśń]{3,}?)(\\W(?:\\d{4}|\\d{2})).*";
        p = Pattern.compile(pattern);
        m = p.matcher(normalizedContent);
        Map<String, String> monthNameMap = getMonthNameMap();
        String month;

        if (m.matches()) {
            month = m.group(3).substring(0, 3);
            String tmpMonth = monthNameMap.get(month);
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
        LOGGER.error("Date not found!");
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, Locale.US);
            startDate = LocalDate.parse(startDateS, formatter);
            endDate = LocalDate.parse(endDateS, formatter);
            return true;
        } catch (DateTimeParseException e) {
            LOGGER.info(e.getMessage(), e);
            return false;
        }
    }

    public boolean isAcceptedByMail(String decision) {
        return decision.matches(".*(tak|yes|ok).*");
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

    public boolean isCorrect() {
        return correct;
    }

    public String getEmailContent() {
        return emailContent;
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