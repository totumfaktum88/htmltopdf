import Converter.HtmlToPdf;
import Converter.InvalidMimeTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class PdfConverterTest {

    HtmlToPdf converter;
    File resourcePath = new File("src/test/resources");
    File pdfToCompare = new File(resourcePath.getAbsolutePath().concat("/pdf-to-compare.pdf"));

    @BeforeEach
    void setUp() {

    }

    @Test
    void testPdfOutput() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("html-to-generate.html").getFile());
        File destination = new File(resourcePath.getAbsolutePath().concat("/output.pdf"));

            converter = new HtmlToPdf(file, destination);

        File convert = converter.convert();
        assertTrue(destination.exists() && pdfToCompare.length() == destination.length());
    }

    @Test
    void testInvalidMimeType() {
        assertThrows(
            InvalidMimeTypeException.class,
            () -> {
                ClassLoader classLoader = getClass().getClassLoader();
                File file = new File(resourcePath.getAbsolutePath().concat("/wrong-mime-type.pdf"));
                File destination = new File(resourcePath.getAbsolutePath().concat("/output.pdf"));

                converter = new HtmlToPdf(file, destination);

                File convert = converter.convert();
            }
        );
    }

    @Test
    void testWrongSourcePath() {
        assertThrows(
            IOException.class,
            () -> {
                ClassLoader classLoader = getClass().getClassLoader();
                File file = new File(resourcePath.getAbsolutePath().concat("/wrong-file.html"));
                File destination = new File(resourcePath.getAbsolutePath().concat("/output.pdf"));

                converter = new HtmlToPdf(file, destination);

                File convert = converter.convert();
            }
        );
    }
}
