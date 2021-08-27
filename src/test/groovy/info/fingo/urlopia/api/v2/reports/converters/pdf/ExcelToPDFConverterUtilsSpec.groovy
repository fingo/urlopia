package info.fingo.urlopia.api.v2.reports.converters.pdf

import com.itextpdf.text.Rectangle
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import spock.lang.Specification

class ExcelToPDFConverterUtilsSpec extends Specification {

    static def sampleWorkbook(numOfRows, numOfCols) {
        def workbook = new XSSFWorkbook()
        def sheet = workbook.createSheet()
        for (int i = 0; i < numOfRows; i++) {
            def row = sheet.createRow(i)
            for (int j = 0; j < numOfCols; j++) {
                row.createCell(j)
            }
        }
        return workbook
    }

    def "resolveCellValue() SHOULD properly resolve cell value"() {
        given: "sample cells"
        def workbook = sampleWorkbook(1, 6)
        def sheet = workbook.getSheetAt(0)
        def row = sheet.getRow(0)
        def cells = [] as List<Cell>
        for (def cell : row) {
            cells.add(cell)
        }

        and: "their types"
        cells[0].setCellValue("string value")
        cells[1].setCellValue(256d)
        cells[2].setCellType(CellType.BLANK)
        cells[3].setCellType(CellType.FORMULA)
        cells[4].setCellType(CellType.BOOLEAN)
        cells[5].setCellType(CellType.ERROR)

        expect:
        ExcelToPDFConverterUtils.resolveCellValue(cells[0]) == "string value"
        ExcelToPDFConverterUtils.resolveCellValue(cells[1]) == "256"
        for (def cell : cells[2..5]) {
            ExcelToPDFConverterUtils.resolveCellValue(cell) == ""
        }
    }

    def "mapNumericValueToString() WHEN string is numeric SHOULD map it properly"() {
        given: "a numeric string"
        def decimalString = "1.45"
        def numericStringWithTrailingZeros = "1.00"
        def integerString = "1"

        expect:
        ExcelToPDFConverterUtils.mapNumericValueToString(decimalString) == "1.45"
        ExcelToPDFConverterUtils.mapNumericValueToString(numericStringWithTrailingZeros) == "1"
        ExcelToPDFConverterUtils.mapNumericValueToString(integerString) == "1"
    }

    def "mapNumericValueToString() WHEN string is non numeric SHOULD return given value"() {
        given: "a non numeric string"
        def nonNumericString = "non numeric string"

        expect:
        ExcelToPDFConverterUtils.mapNumericValueToString(nonNumericString) == nonNumericString
    }

    def "maxNumberOfColumnsIn() SHOULD return max number of columns in a sheet"() {
        given: "sample sheet"
        def workbook = sampleWorkbook(4, 10)
        def sheet = workbook.getSheetAt(0)

        expect:
        ExcelToPDFConverterUtils.maxNumberOfColumnsIn(sheet) == 10
    }

    def "mapHorizontalAlignment() SHOULD properly map horizontal alignment"() {
        given: "sample alignments"
        def left = HorizontalAlignment.LEFT
        def right = HorizontalAlignment.RIGHT
        def center = HorizontalAlignment.CENTER
        def general = HorizontalAlignment.GENERAL

        expect:
        ExcelToPDFConverterUtils.mapHorizontalAlignment(left) == Rectangle.ALIGN_LEFT
        ExcelToPDFConverterUtils.mapHorizontalAlignment(right) == Rectangle.ALIGN_RIGHT
        ExcelToPDFConverterUtils.mapHorizontalAlignment(center) == Rectangle.ALIGN_CENTER
        ExcelToPDFConverterUtils.mapHorizontalAlignment(general) == Rectangle.ALIGN_RIGHT
    }
}
