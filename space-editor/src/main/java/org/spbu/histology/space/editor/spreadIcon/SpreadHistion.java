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
        id = "org.spbu.histology.toolbar.SpreadHistion"
)
@ActionRegistration(
        iconBase = "org/spbu/histology/space/editor/spread.png",

        displayName = "Распространить Гистион"
)
@ActionReference(path = "Toolbars/File", position = 400)
@Messages("CTL_HomeAction=Распространить гистион")

public class SpreadHistion extends HomeController implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        ChosenTool.setToolNumber(4);
        Platform.runLater(() -> {
            new SpreadHistionMethod().view();
        });

    }

}