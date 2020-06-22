/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spbu.histology.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javafx.application.Platform;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.spbu.histology.util.FindLineByThreePoints;

@ActionID(
        category = "Edit",
        id = "org.spbu.histology.menu.FindPointAction"
)
@ActionRegistration(
        displayName = "#CTL_FindPointAction"
)
@ActionReference(path = "Menu/Tools", position = 0, separatorAfter = 50)
@Messages("CTL_FindPointAction=Найти координату точки")
public final class FindPointAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        Platform.runLater(() -> FindLineByThreePoints.display());
    }
}
