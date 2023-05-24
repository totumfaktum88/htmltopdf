package Converter;

import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

public class Watermark {
    protected PdfDocument document;
    protected String watermark;

    Watermark(PdfDocument doc, String text) {
        document  = doc;
        watermark = text;
    }

    protected void manipulatePdf() throws Exception {
        Document doc        = new Document(document);
        PdfFont font        = PdfFontFactory.createFont(FontProgramFactory.createFont(StandardFonts.HELVETICA));
        Paragraph paragraph = new Paragraph(watermark).setFont(font).setFontSize(100).setRotationAngle(1).setWidth(500).setFixedPosition(155,475, 750);
        PdfExtGState gs1    = new PdfExtGState().setFillOpacity(0.5f);

        // Implement transformation matrix usage in order to scale image
        for (int i = 1; i <= document.getNumberOfPages(); i++) {
            PdfPage   pdfPage  = document.getPage(i);
            Rectangle pageSize = pdfPage.getPageSize();

            float x = (pageSize.getLeft() + pageSize.getRight()) / 2;
            float y = (pageSize.getTop() + pageSize.getBottom()) / 2;

            PdfCanvas over = new PdfCanvas(pdfPage);
            over.saveState();
            over.setExtGState(gs1);

            doc.showTextAligned(paragraph, x, y, i, TextAlignment.CENTER, VerticalAlignment.TOP, 0);

            over.restoreState();
        }

        doc.close();
    }
}