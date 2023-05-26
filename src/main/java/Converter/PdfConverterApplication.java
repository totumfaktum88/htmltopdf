package Converter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class PdfConverterApplication extends Application {
    protected String chooserPlaceholder = "Válasszon ki egy HTML fájlt";
    protected File selectedFile = null;
    protected File destination = null;
    protected File previousDir = null;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("iText HTML to PDF");

        FileChooser fileChooser = new FileChooser();

        Text fileInfo = new Text(chooserPlaceholder);

        fileInfo.setDisable(true);

        TextArea output = new TextArea();
        output.setDisable(true);

        Button button = new Button("Select HTML File");
        button.setOnAction(e -> {
            output.setText("Fájl betöltése.\r\n");
            if (previousDir != null) {
                fileChooser.setInitialDirectory(previousDir);
            }

            selectedFile = fileChooser.showOpenDialog(primaryStage);
            destination = getDestination(selectedFile);

            previousDir = selectedFile.getParentFile();
            fileInfo.setText(selectedFile.getAbsolutePath());

            output.appendText("Fájl betöltve: ".concat(selectedFile.getAbsolutePath()).concat("\r\n"));
        });

        CheckBox waterMark = new CheckBox("Watermark");
        TextField waterMarkText = new TextField();

        waterMarkText.setDisable(true);

        waterMark.setOnAction(e -> {
            waterMarkText.setDisable(!waterMark.isSelected());
        });

        Button convertButton = new Button("Convert");

        convertButton.setOnAction(e -> {
            output.appendText("Konvertálás: ".concat(destination.getAbsolutePath()).concat("\r\n"));

            try {

                HtmlToPdf converter = new HtmlToPdf(selectedFile, destination);

                if (waterMark.isSelected() && waterMarkText.getText().length() > 0) {
                    converter.setWatermark(waterMarkText.getText());
                }

                convertButton.setDisable(true);

                converter.convert();

                convertButton.setDisable(false);
            } catch (Exception ex) {
                ex.printStackTrace();
                convertButton.setDisable(false);
            }
        });

        //Creating a Grid Pane
        GridPane gridPane = new GridPane();

        //Setting size for the pane
        gridPane.setMinSize(400, 200);

        //Setting the padding
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        gridPane.setVgap(15);
        gridPane.setHgap(15);

        //Setting the Grid alignment
        gridPane.setAlignment(Pos.CENTER);

        //Arranging all the nodes in the grid
        gridPane.add(button, 0, 0);
        gridPane.add(fileInfo, 1, 0);
        gridPane.add(waterMark, 0, 1);
        gridPane.add(waterMarkText, 1, 1);
        gridPane.add(convertButton, 1, 2);
        gridPane.add(output, 0, 3, 2,1);

        GridPane.setColumnSpan(output, 3);

        Scene scene = new Scene(gridPane, 960, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public File getDestination(File file) {
        String ext = ".".concat(getExtensionByStringHandling(file.getName()).get());
        String path = file.getAbsolutePath().replace(file.getName(), file.getName().replace(ext, ".pdf"));


        return new File(path);
    }
}