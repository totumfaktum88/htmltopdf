import Converters.HtmlToPdf;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.logging.Handler;
import java.util.logging.Level;

public class Main {
    public static void main(String args[]) throws Exception {
        new Main(args);
    }

    public Main(String args[]) throws Exception {
        Options options = new Options();
        options.addOption("watermark", false, "Vizjel hasznalata a pdf-ben.");

            CommandLineParser parser = new DefaultParser();
            CommandLine          cmd = parser.parse(options, args);

            /*if( cmd.hasOption("silence") ) {
                LOG.setLevel(Level.ALL);

                for (Handler h : rootLogger.getHandlers()) {
                    h.setLevel(Level.ALL);
                }
            }*/

            if (args.length >= 2) {
                HtmlToPdf converter = new HtmlToPdf(new File(args[0]), new File(args[1]));

                if( cmd.hasOption("watermark") ) {
                    converter.setWatermark("pina");
                }

                File jar = new File(System.getProperty("java.class.path"));

                if( !jar.toString().contains(";") && jar.exists() && jar.isDirectory() ) {
                    converter.addFontDirectory(new File(jar.getParent().concat("/fonts")));
                }

                converter.convert();
            }else if (args.length == 1) {
                System.out.println("Nincs megadva kimeneti fájl.");
                System.exit(1);
            } else {
                System.out.println("Be és kimeneti fájl megadása egyaránt szükséges.");
                System.exit(1);
            }
    }
}