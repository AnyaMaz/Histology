package org.spbu.histology.space.editor.spreadIcon;

import javafx.application.Platform;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.spbu.histology.space.editor.HomeController;
import org.spbu.histology.toolbar.ChosenTool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@ActionID(
        category = "Edit",
        id = "org.spbu.histology.toolbar.CleanSpread"
)
@ActionRegistration(
        iconBase = "org/spbu/histology/toolbar/close.png",
        displayName = "Убрать распространение Гистиона"
)
@ActionReference(path = "Toolbars/File", position = 430)
@Messages("CTL_Home1Action=Убрать распространение гистиона")

public class CleanSpread implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        ChosenTool.setToolNumber(5);
        Platform.runLater(() -> {
            new CleanSpreadMethod().clean();
        });

    }

}