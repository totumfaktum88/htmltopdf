package Converter;

import javafx.application.Application;

public class Main {
    public static void main(String args[]) throws Exception {
        new Main(args);
    }

    public Main(String args[]) throws Exception {
        Application.launch(PdfConverterApplication.class, args);
    }
}