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
import java.nio.file.Files;

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



    public HtmlToPdf(File source, File destination) {
        this.source      = source;
        this.destination = destination;

        this.baseURI = destination.getAbsolutePath().replace(source.getName(), "");

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
    }

    public void setWatermark(String text) {
        this.watermark = text;
    }

    public void addFontDirectory(File dir) {
        if( dir.exists() && dir.isDirectory() && dir.listFiles().length > 0 ) {
            fontProvider.addDirectory(dir.toPath().toString());
        }
    }

    public File convert() throws Exception {
        if (this.source.exists()) {
            /*File jar = new File(System.getProperty("java.class.path"));
            File dir = new File(jar.getParent().concat("/fonts"));

            // Hát ez még kérdéses, hogy jó-e így :D
            if( !dir.toString().contains(";") && dir.exists() ) {
                fontProvider.addDirectory(dir.toPath().toString());
            }*/

            properties.setBaseUri(this.baseURI);
            properties.setFontProvider(this.fontProvider);

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
            }
        } else {
            throw new FileNotFoundException();
        }

        return destination;
    }
}
