package info.fingo.urlopia.api.v2.reports.converters.pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExcelToPDFConverterSettings {
    public static final float DEFAULT_WIDTH_PERCENTAGE = 80.f;
    public static final String DEFAULT_FONT_NAME = BaseFont.HELVETICA;
    public static final int DEFAULT_FONT_SIZE = 12;

    private float widthPercentage;
    private String fontName;
    private int fontSize;

    public static ExcelToPDFConverterSettings defaultSettings() {
        var settings = new ExcelToPDFConverterSettings();
        settings.setWidthPercentage(DEFAULT_WIDTH_PERCENTAGE);
        settings.setFontName(DEFAULT_FONT_NAME);
        settings.setFontSize(DEFAULT_FONT_SIZE);
        return settings;
    }

    public Font getFont() {
        try {
            var baseFont = BaseFont.createFont(fontName, BaseFont.CP1257, BaseFont.EMBEDDED);
            return new Font(baseFont, fontSize, Font.NORMAL);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        return new Font();
    }
}
