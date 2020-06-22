package org.spbu.histology.space.viewer;

import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import org.openide.LifecycleManager;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.spbu.histology.fxyz.Line3D;
import org.spbu.histology.menu.ChosenMenuItem;
import org.spbu.histology.model.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



@ActionID(
        category = "Edit",
        id = "org.spbu.histology.toolbar.stand"
)
@ActionRegistration(
        iconBase = "org/spbu/histology/space/viewer/qwe.png",
        displayName = "#CTL_Stand"
)
@ActionReference(path = "Toolbars/File", position = 460)
@NbBundle.Messages("CTL_Stand=Этажерка")
public class t extends SpaceViewerTopComponent implements ActionListener{

    @Override
    public void actionPerformed(ActionEvent e) {
        //Platform.runLater(()->{
            if (ChosenMenuItem.getMenuItem() != 460) {
                ChosenMenuItem.setMenuItem(460);
            }else {
                ChosenMenuItem.setMenuItem(-460);
            }
       // });
    }
}


