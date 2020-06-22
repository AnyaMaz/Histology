package org.spbu.histology.space.editor;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.spbu.histology.model.Cell;
import org.spbu.histology.model.RestoreModel;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.nio.file.Paths;

public class LoadModelService {

    private static final String USER_DIRECTORY_ROOT = FileSystemView.getFileSystemView().getDefaultDirectory().getPath()
            + "\\HistologyApp" + System.getProperty("sun.arch.data.model");
    private static final String HISTIONS_FOLDER = "\\Histions";
    private static final String CELLS_FOLDER = "\\Cells";
    private static final String IMPORTED_MODELS_FOLDER = "\\models";

    public static Cell displayHistion(String fileName) {
        return RestoreModel.histionFromFile(new File(USER_DIRECTORY_ROOT + HISTIONS_FOLDER + "\\" + fileName), true);
    }

    public static void displayImportedModel(String fileName) {
        System.out.println("Display " + fileName);
        RestoreModel.histionFromFile(new File(USER_DIRECTORY_ROOT + IMPORTED_MODELS_FOLDER + "\\" + fileName), true);
    }

    public static void displayHistion() {

        Stage window = new Stage();
        window.setTitle("Загрузить гистион");

        final ToggleGroup group = new ToggleGroup();

        RadioButton rb1 = new RadioButton("Заменить текущий гистион");
        rb1.setToggleGroup(group);
        rb1.setSelected(true);

        RadioButton rb2 = new RadioButton("Добавить к текущему гистиону");
        rb2.setToggleGroup(group);

        Button closeButton = new Button("OK");
        closeButton.setOnAction(e -> {
            window.hide();
            File selectedFile = openFileChooser(HISTIONS_FOLDER).showOpenDialog(null);
            RestoreModel.histionFromFile(selectedFile, rb1.isSelected());
            window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(rb1, rb2, closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 350, 150);
        window.setScene(scene);
        window.showAndWait();
    }

    public static void displayCell() {
        File selectedFile = openFileChooser(CELLS_FOLDER).showOpenDialog(null);
        RestoreModel.cellFromFile(selectedFile);
    }

    public static void autoLoadCell(String name) {
        RestoreModel.autoLoadCell(Paths.get(USER_DIRECTORY_ROOT + CELLS_FOLDER + "/" + name).toString());
    }

    public static Cell createCell(String name) {
        return RestoreModel.cellFromFile(new File(USER_DIRECTORY_ROOT + CELLS_FOLDER + "/" + name));
    }

    private static FileChooser openFileChooser(String folder) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File userDirectory = new File(USER_DIRECTORY_ROOT + "\\" + folder);
        if (!userDirectory.exists()) {
            userDirectory.mkdirs();
        }
        fileChooser.setInitialDirectory(userDirectory);
        return fileChooser;
    }
}
