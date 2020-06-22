package org.spbu.histology.space.editor;

import javafx.application.Platform;
import org.spbu.histology.space.editor.spreadIcon.SpreadHistionService;

import java.io.FileNotFoundException;

public class AfterStartUpActions {

    public static void displayChoiceWindow() {
        try {
            ChooseModelService.displayModalWindowWithChoice();
        } catch (Exception e) {
            AfterStartUpActions.displayHistionOnApplicationStartUp();
        }
    }

    public static void displayHistionOnApplicationStartUp() {
        Platform.runLater(() -> {
            LoadModelService.displayHistion("test.txt");
            autoSpread();
        });
    }

    public static void beginWithBuildingBookcaseView() {
        Platform.runLater(() -> LoadModelService.autoLoadCell("Hexagonal.txt"));
    }

    public static void autoSpread() {
        new SpreadHistionService().spread(false);
    }


}
