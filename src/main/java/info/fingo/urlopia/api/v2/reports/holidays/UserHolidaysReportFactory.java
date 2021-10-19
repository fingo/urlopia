package info.fingo.urlopia.api.v2.reports.holidays;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import info.fingo.urlopia.api.v2.reports.converters.pdf.PDFGenerationSettings;
import info.fingo.urlopia.api.v2.request.RequestStatus;
import info.fingo.urlopia.config.persistance.filter.Filter;
import info.fingo.urlopia.config.persistance.filter.FilterComponent;
import info.fingo.urlopia.config.persistance.filter.Operator;
import info.fingo.urlopia.request.Request;
import info.fingo.urlopia.request.RequestService;
import info.fingo.urlopia.request.RequestType;
import info.fingo.urlopia.user.User;
import info.fingo.urlopia.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserHolidaysReportFactory {
    private static final PDFGenerationSettings SETTINGS = PDFGenerationSettings.defaultSettings();
    private static final int HEADER_FONT_SIZE = 14;
    private static final int NUM_OF_TABLE_COLUMNS = 3;
    private static final float TABLE_WIDTH_PERCENTAGE = 90.f;

    private final UserService userService;
    private final RequestService requestService;

    public void createAsPDF(Long userId, OutputStream outputStream, Filter requestFilter) {
        var user = userService.get(userId);
        try {
            var pdf = createNewDocument(outputStream);
            pdf.open();

            var table = createNewTable();
            var font = SETTINGS.getFont();

            insertDocumentHeaders(table, user);
            insertTableHeaders(table, font);

            for (var request : getRequestsOf(user, requestFilter)) {
                insertRequest(table, font, request);
            }

            pdf.add(table);

            pdf.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private Document createNewDocument(OutputStream outputStream) throws DocumentException {
        var pdf = new Document();
        PdfWriter.getInstance(pdf, outputStream);
        return pdf;
    }

    private PdfPTable createNewTable() {
        var table = new PdfPTable(NUM_OF_TABLE_COLUMNS);
        table.setWidthPercentage(TABLE_WIDTH_PERCENTAGE);
        return table;
    }

    private void insertDocumentHeaders(PdfPTable table, User user) {
        insertUserName(table, user);
        insertCreationDate(table);
        insertBlankRow(table);
    }

    private void insertUserName(PdfPTable table, User user) {
        var userFirstName = user.getFirstName();
        var userLastName = user.getLastName();
        var headerString = "Wnioski urlopowe - %s %s".formatted(userFirstName, userLastName);

        var font = SETTINGS.getFont();
        font.setSize(HEADER_FONT_SIZE);

        var headerCell = new PdfPCell(new Phrase(headerString, font));
        headerCell.setColspan(NUM_OF_TABLE_COLUMNS);
        headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        headerCell.setBorder(Rectangle.BOTTOM);
        headerCell.setPaddingBottom(4.f);

        table.addCell(headerCell);
    }

    private void insertCreationDate(PdfPTable table) {
        var creationDateString = "Data utworzenia: %s".formatted(LocalDate.now().toString());

        var font = SETTINGS.getFont();
        font.setSize(font.getSize() - 1);
        font.setColor(BaseColor.GRAY);

        var creationDateCell = new PdfPCell(new Phrase(creationDateString, font));
        creationDateCell.setBorder(Rectangle.NO_BORDER);
        creationDateCell.setColspan(NUM_OF_TABLE_COLUMNS);
        creationDateCell.setHorizontalAlignment(Element.ALIGN_LEFT);

        table.addCell(creationDateCell);
    }

    private void insertBlankRow(PdfPTable table) {
        var blankRow = new PdfPCell(new Phrase(" "));
        blankRow.setColspan(NUM_OF_TABLE_COLUMNS);
        blankRow.setHorizontalAlignment(Element.ALIGN_CENTER);
        blankRow.setBorder(Rectangle.NO_BORDER);

        table.addCell(blankRow);
    }

    private void insertTableHeaders(PdfPTable table, Font font) {
        table.addCell(newTableCell("Termin urlopu", font));
        table.addCell(newTableCell("Typ wniosku", font));
        table.addCell(newTableCell("Status", font));
    }

    private List<Request> getRequestsOf(User user, Filter requestFilter) {
        if (requestFilter.isEmpty()) {
            requestFilter = requestFilter.toBuilder()
                    .and("status", Operator.EQUAL, Request.Status.ACCEPTED.toString())
                    .or(new FilterComponent("status", Operator.EQUAL, Request.Status.REJECTED.toString()))
                    .build();
        }

        requestFilter = requestFilter.toBuilder()
                .and("requester.id", Operator.EQUAL, String.valueOf(user.getId()))
                .build();

        return requestService.getAll(requestFilter).stream()
                .filter(request -> request.getType() != RequestType.SPECIAL)
                .sorted(Comparator.comparing(Request::getStartDate).reversed())
                .toList();
    }

    private void insertRequest(PdfPTable table, Font font, Request request) {
        var formattedWorkingDays = RequestFormatter.formattedWorkingDaysOf(request);
        var termString = request.getTerm() + "\n(" + formattedWorkingDays + ")";
        table.addCell(newTableCell(termString, font));

        var typeString = RequestFormatter.formattedRequestTypeOf(request);
        typeString = insertLineBreakIfTypeIsOccasional(typeString);
        table.addCell(newTableCell(typeString, font));

        var statusString = RequestFormatter.formattedRequestStatusOf(request);
        table.addCell(newTableCell(statusString, font));
    }

    private String insertLineBreakIfTypeIsOccasional(String typeString) {
        var idx = typeString.indexOf("(");
        if (idx == -1) {
            return typeString;
        }
        return typeString.substring(0, idx) + "\n" + typeString.substring(idx);
    }

    private PdfPCell newTableCell(String value, Font font) {
        var cell = new PdfPCell(new Phrase(value, font));
        cell.setPaddingBottom(5.f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }
}
