package info.fingo.urlopia.mail;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * @author Józef Grodzicki
 */
public class MailParserTest {
    private Mail mail;
    private MailParser mailParser;
    private LocalDate expectedStartDate;
    private LocalDate expectedEndDate;
    private LocalDate startDate;
    private LocalDate endDate;

    @Before
    public void init() {
        mail = new Mail();
        mailParser = new MailParser();
        expectedStartDate = LocalDate.now();
        expectedEndDate = LocalDate.now();
    }

    private boolean parseSingleDate(String start, String end) {
        String email = "od " + start + " do " + end;

        mail.setContent(email);

        mailParser.parseContent(mail);
        startDate = mailParser.getStartDate();
        endDate = mailParser.getEndDate();
        boolean parsedIsCorrect = mailParser.isCorrect();

        expectedStartDate = LocalDate.of(2010, 4, 10);
        expectedEndDate = LocalDate.of(2010, 5, 1);

        return parsedIsCorrect;
    }

    @Test
    public void correctDates() throws Exception {
        String[][] dateFormats = {{"10.04.2010", "01.05.2010"},
                {"10-04-2010", "01-05-2010"},
                {"10/04/2010", "01/05/2010"},
                {"10 April 2010", "01 May 2010"},
                {"10 kwietnia 2010", "01 maja 2010"},
                {"10.04.10", "01.05.10"},
                {"10-04-10", "01-05-10"},
                {"10/04/10", "01/05/10"},
                {"10 April 10", "01 May 10"},
                {"10 kwiecień 10", "01 maja 10"}};

        for (String[] dateFormat : dateFormats) {
            boolean parsedIsCorrect = parseSingleDate(dateFormat[0], dateFormat[1]);
            assertTrue(dateFormat[0], parsedIsCorrect);
            assertEquals(dateFormat[0], expectedStartDate, startDate);
            assertEquals(dateFormat[0], expectedEndDate, endDate);
        }
    }

    @Test
    public void incorrectDates() throws Exception {
        String[][] dateFormats = {{"10.4.2010", "01.05.2010"},
                {"10-04-201", "01-05-2010"},
                {"10/2010", "01/05/2010"},
                {"10 april 2010", "01 May 2010"},
                {"10kwietnia 2010", "01 maja 2010"},
                {"10..10", "01.05.10"},
                {"10-04@10", "01-05-10"},
                {"10/04/", "01/05/10"},
                {"10 Aril 10", "01 May 10"},
                {"10 wietnia 10", "01 maja 10"}};

        for (String[] dateFormat : dateFormats) {
            boolean parsedIsCorrect = parseSingleDate(dateFormat[0], dateFormat[1]);
            assertFalse(dateFormat[0], parsedIsCorrect);
        }
    }

    @Test
    public void shouldParseAsReplyAndFindRequestId() throws Exception {
        mail.setSubject("Odp: Prośba o urlop [98]");
        mailParser.parseSubject(mail);
        assertTrue(mailParser.isReply());
        assertEquals(98, mailParser.getId());

    }

    @Test
    public void shouldFindOKanswer() {
        mail.setContent("Ok\n" +
                "Jan Nowak\n" +
                "superfirma sp. z o.o.");
        mailParser.parseReply(mail);
        assertEquals("Ok", mailParser.getReply());
    }
}