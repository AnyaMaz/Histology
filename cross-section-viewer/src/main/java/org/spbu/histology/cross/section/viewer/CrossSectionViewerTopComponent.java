package org.spbu.histology.cross.section.viewer;

import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.spbu.histology.cross.section.viewer.util.Vertex;
import org.spbu.histology.model.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Display Histion from top i.e. 2D image of 3D-model
 */
@ConvertAsProperties(
        dtd = "-//org.spbu.histology.cross.section.viewer//CrossSectionViewer//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "CrossSectionViewerTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "org.spbu.histology.cross.section.viewer.CrossSectionViewerTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_CrossSectionViewerAction",
        preferredID = "CrossSectionViewerTopComponent"
)
@Messages({
        "CTL_CrossSectionViewerAction=CrossSectionViewer",
        "CTL_CrossSectionViewerTopComponent=CrossSectionViewer Window",
        "HINT_CrossSectionViewerTopComponent=This is a CrossSectionViewer window"
})
public final class CrossSectionViewerTopComponent extends TopComponent {

    private JFXPanel fxPanel;
    private Group root = new Group();
    private final double paneSize = 4000;
    private double scale = 1.0;

    public CrossSectionViewerTopComponent() {
        initComponents();
        setName(Bundle.CTL_CrossSectionViewerTopComponent());
        setToolTipText(Bundle.HINT_CrossSectionViewerTopComponent());
        setLayout(new BorderLayout());
        init();
    }

    private void init() {
        fxPanel = new JFXPanel();
        add(fxPanel, BorderLayout.CENTER);
        Platform.setImplicitExit(false);
        Platform.runLater(this::createScene);
    }

    private void createScene() {
        Pane drawingPane = new Pane();
        drawingPane.getChildren().add(root);
        drawingPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        ScrollPane scrollPane = new ScrollPane(drawingPane);
        scrollPane.addEventFilter(ScrollEvent.ANY, event -> {
            if (event.getDeltaY() > 0) {
                if (scale < 2.45) {
                    scale += 0.05;
                    root.setScaleX(scale);
                    root.setScaleY(scale);
                }
            } else {
                if (scale > 0.05) {
                    scale -= 0.05;
                    root.setScaleX(scale);
                    root.setScaleY(scale);
                }
            }
            event.consume();
        });
        scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-focus-color: transparent;");
        drawingPane.setMinHeight(paneSize);
        drawingPane.setMinWidth(paneSize);
        scrollPane.setHvalue(0.5);
        scrollPane.setVvalue(0.5);
        Scene scene = new Scene(scrollPane);

        fxPanel.setScene(scene);
        CrossSectionPlane.initialized.set(true);
    }

    public void clear() {
        root.getChildren().clear();
    }

    public void clearPolygonArray(ArrayList<Polygon> polygons) {
        polygons.forEach(p -> root.getChildren().remove(p));
    }

    public void clearLineArray(ArrayList<Line> lines) {
        lines.forEach(l -> root.getChildren().remove(l));
    }

    public void show(ArrayList<Polygon> polygons) {
        if (fxPanel == null) {
            return;
        }
        polygons.forEach(polygon -> {
            polygon.setTranslateX(paneSize / 2);
            polygon.setTranslateY(paneSize / 2);
            root.getChildren().add(polygon);
        });
    }

    public void showLines(ArrayList<Line> lineList) {
        if (fxPanel == null) {
            return;
        }
        lineList.forEach(line -> {
            line.setTranslateX(paneSize / 2);
            line.setTranslateY(paneSize / 2);
            root.getChildren().add(line);
        });
    }

    private final MapChangeListener<Integer, ArrayList<Line>> lineListener
            = (change) -> {
        if (change.wasRemoved()) {
            for (Line l : change.getValueRemoved()) {
                root.getChildren().remove(l);
            }
        }
        if (change.wasAdded()) {
            for (Line l : change.getValueAdded()) {
                l.setTranslateX(paneSize / 2);
                l.setTranslateY(paneSize / 2);
                root.getChildren().add(l);
            }
        }
    };

    private final MapChangeListener<Integer, ArrayList<Polygon>> polygonListener
            = (change) -> {
        if (change.wasRemoved()) {
            for (Polygon p : change.getValueRemoved()) {
                root.getChildren().remove(p);
            }
        }
        if (change.wasAdded()) {
            for (Polygon p : change.getValueAdded()) {
                p.setTranslateX(paneSize / 2);
                p.setTranslateY(paneSize / 2);
                if (HideCells.getCellIdToHideList().contains(change.getKey())) {
                    p.setStroke(Color.WHITE);
                    p.setStrokeWidth(7);
                }

                p.setOnMouseClicked(me -> {
                    if (me.getButton() == MouseButton.PRIMARY) {
                        if (!HideCells.getCellIdToHideList().contains(change.getKey()) && !HideCells.getCellIdToShowInOneViewerList().contains(change.getKey())) {
                            HideCells.addCellIdToHide(change.getKey());
                            HideCells.addCellIdToShowInOneViewer(change.getKey());
                            for (Polygon pol : CrossSectionVisualization.getPolygonMap().get(change.getKey())) {
                                pol.setStroke(Color.WHITE);
                                pol.setStrokeWidth(7);
                            }
                        } else {
                            HideCells.removeCellIdToHide(change.getKey());
                            HideCells.removeCellIdToShowInOneViewer(change.getKey());
                            for (Polygon pol : CrossSectionVisualization.getPolygonMap().get(change.getKey())) {
                                pol.setFill(CrossSectionVisualization.getPolygonColorMap().get(change.getKey()));
                                pol.setStrokeWidth(0);
                            }
                        }
                    }
                });
                root.getChildren().add(p);

                List<Vertex> vertexes = new ArrayList<>();

                CrossSectionVisualization.getPolygonMap()
                        .forEach((key, value1) -> value1.forEach(polygon -> {
                            for (int i = 0; i < polygon.getPoints().size(); i += 2) {
                                ObservableList<Double> points = polygon.getPoints();
                                Vertex newVertex = new Vertex(points.get(i), points.get(i + 1), "polygon_" + key);
                                vertexes.add(newVertex);
                            }
                        }));

                IntStream.range(0, vertexes.size())
                        .forEach(i -> {
                            Polygon polygon = new Polygon();
                            polygon.getPoints().addAll(vertexes.get(i).getX(), vertexes.get(i).getY());
                            polygon.setTranslateX(paneSize / 2);
                            polygon.setTranslateY(paneSize / 2);
                            polygon.setStroke(Color.BLACK);
                            polygon.setStrokeWidth(10);
                            polygon.setId("in_modal_" + i);
                            CrossSectionVisualization.addVertex(polygon, 0);
                            polygon.setOnMouseClicked(me -> {
                                if (CrossSectionVisualization.getVertexToClickCount().get(polygon) != null
                                        && CrossSectionVisualization.getVertexToClickCount().get(polygon) == 0) {
                                    polygon.setStroke(Color.GREEN);
                                    CrossSectionVisualization.addVertex(polygon, 1);
                                } else {
                                    CrossSectionVisualization.addVertex(polygon, 0);
                                    polygon.setStroke(Color.BLACK);
                                }
                            });
//                            group.getChildren().add(polygon);
                        });
            }
        }
    };

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        CrossSectionVisualization.getPolygonMap().addListener(polygonListener);
        CrossSectionVisualization.getLineMap().addListener(lineListener);
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        CrossSectionVisualization.getPolygonMap().removeListener(polygonListener);
        CrossSectionVisualization.getLineMap().removeListener(lineListener);
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}
