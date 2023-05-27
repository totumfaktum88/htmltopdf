package Converter;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.attach.impl.DefaultTagWorkerFactory;
import com.itextpdf.html2pdf.attach.impl.OutlineHandler;
import com.itextpdf.html2pdf.css.apply.impl.DefaultCssApplierFactory;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

/**
 * HTML-ből PDF állományt konvertáló osztály, amely az iText library-t  használja fel erre a célra.
 */
public class HtmlToPdf {
    protected PdfWriter writer;
    protected PdfDocument pdf;
    protected Document doc;
    protected FontProvider fontProvider;
    protected ConverterProperties properties ;

    protected String baseURI;
    protected String watermark;

    protected File source;
    protected File destination;



    public HtmlToPdf(File source, File destination) throws IOException, InvalidMimeTypeException {
        this.source      = source;
        this.destination = destination;

        //Mime típus és fájl útvonal ellenőrzése
        String mimeType = Files.probeContentType(source.toPath());

        System.out.println(mimeType);
        if (!source.exists()) {
            throw new FileNotFoundException("File not found.");
        }else if (!mimeType.equals("text/html")) {
            throw new InvalidMimeTypeException("Accepted mime type: text/html.");
        }

        this.baseURI = destination.getAbsolutePath().replace(source.getName(), "");
    }

    /**
     * Watermark beállítása
     * @param text
     */
    public void setWatermark(String text) {
        this.watermark = text;
    }

    /**
     * Font mappa hozzáadása a Font Providerhez, ha szükséges
     * @param dir
     */
    public void addFontDirectory(File dir) {
        if( dir.exists() && dir.isDirectory() && dir.listFiles().length > 0 ) {
            fontProvider.addDirectory(dir.toPath().toString());
        }
    }

    /**
     * Konverzió indítása a megadott forrás fájl alapján
     * @return File
     * @throws Exception
     */
    public File convert() throws Exception {
        if (this.source.exists()) {
            // Fontok inicializálása
            this.fontProvider = new DefaultFontProvider(false, false, false);
            this.properties   = new ConverterProperties().setBaseUri(".")
                    .setCreateAcroForm(false)
                    .setCssApplierFactory(new DefaultCssApplierFactory())
                    .setFontProvider(new DefaultFontProvider())
                    .setMediaDeviceDescription(MediaDeviceDescription.createDefault())
                    .setOutlineHandler(new OutlineHandler())
                    .setTagWorkerFactory(new DefaultTagWorkerFactory());

            fontProvider.addSystemFonts();
            fontProvider.addStandardPdfFonts();

            properties.setBaseUri(this.baseURI);
            properties.setFontProvider(this.fontProvider);

            //Konverzió indítása
            this.writer = new PdfWriter(this.destination);
            this.pdf    = new PdfDocument(this.writer);
            this.doc    = HtmlConverter.convertToDocument(
                    new FileInputStream(this.source),
                    this.pdf,
                    this.properties);

            doc.close();
            pdf.close();
            writer.close();

            System.gc();

            // Ha van megadva vízjel, létrehozunk egy vízjeles verziót is.
            // Mivel a cél fájl használatban van ezért kell egy külön fájl, ahova tárolásra kerül a vízjelezett verzió.
            // Ha végzett a folyamat, az eredeti fájl törlésre kerül, a vízjelezett meg a célfájl nevére lesz átnevezve.
            if( this.watermark != null && this.watermark.length() > 0 ) {
                File watermarked = new File(destination.getParent().toString().concat("/"+destination.getName().replace(".pdf","-watermark.pdf")));

                if(watermarked.exists()) {
                    Files.delete(watermarked.toPath());
                }

                PdfReader signerReader = new PdfReader(destination);
                PdfWriter signerWriter = new PdfWriter(watermarked);
                PdfDocument signerDoc  = new PdfDocument(
                        signerReader,
                        signerWriter
                );

                (new Watermark(
                        signerDoc,
                        watermark
                )).manipulatePdf();

                signerDoc.close();
                signerWriter.close();
                signerReader.close();

                System.gc();
                destination.delete();
                watermarked.renameTo(destination);
            }
        } else {
            throw new IOException();
        }

        return destination;
    }
}
