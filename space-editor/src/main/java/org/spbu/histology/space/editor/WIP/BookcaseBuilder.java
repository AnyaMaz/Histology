package org.spbu.histology.space.editor.WIP;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;
import org.openide.util.Lookup;
import org.spbu.histology.model.*;
import org.spbu.histology.space.editor.spreadIcon.SpreadHistionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class BookcaseBuilder {

    private static HistionManager histionManager = Lookup.getDefault().lookup(HistionManager.class);
    private static Histion main = histionManager.getHistionMap().get(0);

    public static void start() {

        Platform.runLater(BookcaseBuilder::mySpread);
    }

    private void handleMouseEvent() {
        histionManager.getShapeMap().addListener(meshListener);
    }

    // Method to create basic 3D figure based on choice via boolean params
    private static void getLayerFromBasicFigures(CellType newCellType, CellParams params) {
        int id = 0;
        int cellId = (int) histionManager.getAllHistions().stream().map(HistologyObject::getItems).count() + 1;

        // initialize cell with base data
        Cell newCell = new Cell(null, 0, 0, 0, 0, 0, FXCollections.observableArrayList(),
                null, null, 0, true);

        // create tetgen points to new cell with one layer ( Part = layer)
        Part partOfNewCell = new Part(null, FXCollections.observableArrayList(), newCell.getId());
        ObservableList<TetgenPoint> newCellTetgenPointData = FXCollections.observableArrayList();

        // <=================================== POINTS INITIALIZING ============================================>

        // TO UNDERSTAND HOW IT WORKS:
        // 1) IN COMPARISION WITH CLASSIC OXYZ SYSTEM WE HAVE OXZY
        // 2) DRAW ONE BY HAND
        // 3) IDs of points goes by ++,so if you want to change order of point creation do not forget about connections

        if (newCellType == CellType.HEXAGONAL || newCellType == CellType.QUADRANGULAR) {
            //top plane:
            newCellTetgenPointData.add(new TetgenPoint(id + 1, params.getShiftX() + params.getScaleLength() * params.getBaseX(), params.getShiftY() - params.getScaleHeight() * params.getBaseY(), params.getShiftZ() + params.getScaleWidth() * params.getBaseZ())); // I quarter up
            newCellTetgenPointData.add(new TetgenPoint(id + 1, params.getShiftX() - params.getScaleLength() * params.getBaseX(), params.getShiftY() - params.getScaleHeight() * params.getBaseY(), params.getShiftZ() + params.getScaleWidth() * params.getBaseZ())); // II quarter up
            newCellTetgenPointData.add(new TetgenPoint(id + 1, params.getShiftX() - params.getScaleLength() * params.getBaseX(), params.getShiftY() - params.getScaleHeight() * params.getBaseY(), params.getShiftZ() - params.getScaleWidth() * params.getBaseZ())); // III quarter up
            newCellTetgenPointData.add(new TetgenPoint(id + 1, params.getShiftX() + params.getScaleLength() * params.getBaseX(), params.getShiftY() - params.getScaleHeight() * params.getBaseY(), params.getShiftZ() - params.getScaleWidth() * params.getBaseZ())); // IV quarter up
        }

        if (newCellType == CellType.PYRAMID || newCellType == CellType.ORTHOGONAL_TETRAHEDRON) {
            //top plane:
            newCellTetgenPointData.add(new TetgenPoint(id + 1, params.getShiftX(), params.getShiftY() - 50, params.getShiftZ())); // by default is (0;-50;0)
        }

        if (newCellType == CellType.TETRAHEDRON) {
            //top plane:
            newCellTetgenPointData.add(new TetgenPoint(id + 1, params.getShiftX(), params.getShiftY() - 50, params.getShiftZ() - params.getBaseZ() / 2)); // by default is (0;-50;0)
        }

        if (newCellType == CellType.TRIANGLE_PRISM) {
            //top plane:
            newCellTetgenPointData.add(new TetgenPoint(id + 1, 0, params.getShiftY() - params.getScaleHeight() * params.getBaseY(), params.getShiftZ() - params.getScaleWidth() * params.getBaseZ())); // I-II quarter border
            newCellTetgenPointData.add(new TetgenPoint(id + 1, params.getShiftX() - params.getScaleLength() * params.getBaseX(), params.getShiftY() - params.getScaleHeight() * params.getBaseY(), 0)); // II-III quarter border
            newCellTetgenPointData.add(new TetgenPoint(id + 1, params.getShiftX() + params.getScaleLength() * params.getBaseX(), params.getShiftY() - params.getScaleHeight() * params.getBaseY(), 0)); // I-IV quarter border
        }

        if (newCellType == CellType.HEXAGONAL) {
            // additional to quad points in top pane for hexagon
            newCellTetgenPointData.add(new TetgenPoint(id + 1, 0, params.getShiftY() - params.getScaleHeight() * params.getBaseY(), params.getShiftZ() + params.getScaleWidth() * (params.getBaseZ() + params.getBaseZ() / 2))); // I-II quarter border
            newCellTetgenPointData.add(new TetgenPoint(id + 1, 0, params.getShiftY() - params.getScaleHeight() * params.getBaseY(), params.getShiftZ() - params.getScaleWidth() * (params.getBaseZ() + params.getBaseZ() / 2))); // III-IV quarter border
        }

        if (newCellType == CellType.HEXAGONAL || newCellType == CellType.QUADRANGULAR || newCellType == CellType.PYRAMID) {
            //bottom plane:
            newCellTetgenPointData.add(new TetgenPoint(id + 1, params.getShiftX() + params.getScaleLength() * params.getBaseX(), params.getShiftY() + params.getScaleHeight() * params.getBaseY(), params.getShiftZ() + params.getScaleWidth() * params.getBaseZ())); // I quarter down
            newCellTetgenPointData.add(new TetgenPoint(id + 1, params.getShiftX() - params.getScaleLength() * params.getBaseX(), params.getShiftY() + params.getScaleHeight() * params.getBaseY(), params.getShiftZ() + params.getScaleWidth() * params.getBaseZ())); // II quarter down
            newCellTetgenPointData.add(new TetgenPoint(id + 1, params.getShiftX() - params.getScaleLength() * params.getBaseX(), params.getShiftY() + params.getScaleHeight() * params.getBaseY(), params.getShiftZ() - params.getScaleWidth() * params.getBaseZ())); // III quarter down
            newCellTetgenPointData.add(new TetgenPoint(id + 1, params.getShiftX() + params.getScaleLength() * params.getBaseX(), params.getShiftY() + params.getScaleHeight() * params.getBaseY(), params.getShiftZ() - params.getScaleWidth() * params.getBaseZ())); // IV quarter down
        }

        if (newCellType == CellType.TRIANGLE_PRISM || newCellType == CellType.ORTHOGONAL_TETRAHEDRON || newCellType == CellType.TETRAHEDRON) {
            //bottom pane:
            newCellTetgenPointData.add(new TetgenPoint(id + 1, 0, params.getShiftY() + params.getScaleHeight() * params.getBaseY(), params.getShiftZ() - params.getScaleWidth() * params.getBaseZ())); // I-II quarter border
            newCellTetgenPointData.add(new TetgenPoint(id + 1, params.getShiftX() - params.getScaleLength() * params.getBaseX(), params.getShiftY() + params.getScaleHeight() * params.getBaseY(), 0)); // II-III quarter border
            newCellTetgenPointData.add(new TetgenPoint(id + 1, params.getShiftX() + params.getScaleLength() * params.getBaseX(), params.getShiftY() + params.getScaleHeight() * params.getBaseY(), 0)); // I-IV quarter border
        }

        if (newCellType == CellType.HEXAGONAL) {
            // additional to quad points in bottom pane for hexagon
            newCellTetgenPointData.add(new TetgenPoint(id + 1, 0, params.getShiftY() + params.getScaleHeight() * params.getBaseY(), params.getShiftZ() + params.getScaleWidth() * (params.getBaseZ() + params.getBaseZ() / 2))); // I-II quarter border
            newCellTetgenPointData.add(new TetgenPoint(id + 1, 0, params.getShiftY() + params.getScaleHeight() * params.getBaseY(), params.getShiftZ() - params.getScaleWidth() * (params.getBaseZ() + params.getBaseZ() / 2))); // III-IV quarter border
        }

        // add tetgen point to part
        partOfNewCell.setPointData(newCellTetgenPointData);
        partOfNewCell.setAvgNode();

        newCell.addChild(partOfNewCell);

        // create facet data for new cell
        ObservableList<ArrayList<Integer>> facetData = FXCollections.observableArrayList();

        // <=================================== HOW TO CONNECT POINTS ============================================>

        if (newCellType == CellType.HEXAGONAL) {

            newCell.setSpecularColor(Color.RED);
            newCell.setDiffuseColor(Color.RED);
            newCell.setName("HexagonalCell" + "<" + cellId + ">");
            partOfNewCell.setName(newCell.getName() + " part");

            // front-left:
            facetData.add(new ArrayList<>(Arrays.asList(3, 6, 12, 9)));
            // front-right:
            facetData.add(new ArrayList<>(Arrays.asList(6, 4, 10, 12)));
            //back-left
            facetData.add(new ArrayList<>(Arrays.asList(5, 2, 8, 11)));
            //back-right
            facetData.add(new ArrayList<>(Arrays.asList(1, 5, 11, 7)));
            //left:
            facetData.add(new ArrayList<>(Arrays.asList(3, 2, 8, 9)));
            //right:
            facetData.add(new ArrayList<>(Arrays.asList(4, 10, 7, 1)));
            //upper:
            facetData.add(new ArrayList<>(Arrays.asList(2, 5, 1, 4, 6, 3)));
            //bottom:
            facetData.add(new ArrayList<>(Arrays.asList(8, 11, 7, 10, 12, 9)));

        }
        if (newCellType == CellType.QUADRANGULAR) {

            newCell.setSpecularColor(Color.GREEN);
            newCell.setDiffuseColor(Color.GREEN);
            newCell.setName("QuadrangularCell" + "<" + cellId + ">");
            partOfNewCell.setName(newCell.getName() + " part");

            // front:
            facetData.add(new ArrayList<>(Arrays.asList(3, 4, 8, 7)));
            //back
            facetData.add(new ArrayList<>(Arrays.asList(2, 1, 5, 6)));
            //left:
            facetData.add(new ArrayList<>(Arrays.asList(7, 3, 2, 6)));
            //right:
            facetData.add(new ArrayList<>(Arrays.asList(4, 8, 5, 1)));
            //upper:
            facetData.add(new ArrayList<>(Arrays.asList(2, 1, 4, 3)));
            //bottom:
            facetData.add(new ArrayList<>(Arrays.asList(8, 7, 6, 5)));
        }
        if (newCellType == CellType.PYRAMID) {

            newCell.setSpecularColor(Color.YELLOW);
            newCell.setDiffuseColor(Color.YELLOW);
            newCell.setName("TriangleWithQuadBottomCell" + "<" + cellId + ">");
            partOfNewCell.setName(newCell.getName() + " part");

            //front:
            facetData.add(new ArrayList<>(Arrays.asList(1, 5, 4)));
            //back:
            facetData.add(new ArrayList<>(Arrays.asList(1, 3, 2)));
            //left:
            facetData.add(new ArrayList<>(Arrays.asList(1, 4, 3)));
            //right:
            facetData.add(new ArrayList<>(Arrays.asList(1, 2, 5)));
            //upper:
            facetData.add(new ArrayList<>(Collections.singletonList(1)));
            //bottom:
            facetData.add(new ArrayList<>(Arrays.asList(2, 3, 4, 5)));
        }
        if (newCellType == CellType.TRIANGLE_PRISM) {

            newCell.setSpecularColor(Color.BLUE);
            newCell.setDiffuseColor(Color.BLUE);
            newCell.setName("TrianglePrismCell" + "<" + cellId + ">");
            partOfNewCell.setName(newCell.getName() + " part");

            //front:
            facetData.add(new ArrayList<>(Arrays.asList(2, 3, 6, 5)));
            //back:
            facetData.add(new ArrayList<>(Arrays.asList(1, 4)));
            // left:
            facetData.add(new ArrayList<>(Arrays.asList(2, 1, 4, 5)));
            //right:
            facetData.add(new ArrayList<>(Arrays.asList(1, 3, 6, 4)));
            //upper:
            facetData.add(new ArrayList<>(Arrays.asList(1, 2, 3)));
            //bottom:
            facetData.add(new ArrayList<>(Arrays.asList(4, 5, 6)));
        }
        if (newCellType == CellType.ORTHOGONAL_TETRAHEDRON || newCellType == CellType.TETRAHEDRON) {

            newCell.setSpecularColor(Color.BROWN);
            newCell.setDiffuseColor(Color.BROWN);
            newCell.setName("TriangularCell" + "<" + cellId + ">");
            partOfNewCell.setName(newCell.getName() + " part");

            //front:
            facetData.add(new ArrayList<>(Arrays.asList(1, 3, 4)));
            //right:
            facetData.add(new ArrayList<>(Arrays.asList(1, 2, 4)));
            //left:
            facetData.add(new ArrayList<>(Arrays.asList(1, 2, 3)));
            //bottom:
            facetData.add(new ArrayList<>(Arrays.asList(2, 3, 4)));
        }

        newCell.setFacetData(facetData);

        createLinesForNewCell(newCell, newCellTetgenPointData, facetData);
        Names.addCellName(newCell.getName());

        main.addChild(newCell);
    }

    private static void mySpread() {
        getLayerFromBasicFigures(CellType.HEXAGONAL, new CellParams().defaultValues());
        new SpreadHistionService().spread(false);
        getLayerFromBasicFigures(CellType.HEXAGONAL, new CellParams(100, 25).defaultValues());
        getLayerFromBasicFigures(CellType.HEXAGONAL, new CellParams(100, -25).defaultValues());
        getLayerFromBasicFigures(CellType.HEXAGONAL, new CellParams(-100, 25).defaultValues());
        getLayerFromBasicFigures(CellType.HEXAGONAL, new CellParams(-100, -25).defaultValues());
    }


    private final MapChangeListener<Integer, MeshView> meshListener = (change) -> {
        if (change.wasAdded()) {

            MeshView meshCell = change.getValueAdded();
            Scene scene = meshCell.getScene();
            Cell cell = main.getItemMap().get(change.getKey());
            System.out.println("Added " + cell.getName());

            meshCell.setOnMouseEntered(event -> meshCell.setCursor(Cursor.OPEN_HAND));
            meshCell.setOnMouseExited(event -> meshCell.setCursor(Cursor.DEFAULT));

            // Drag and drop start
            meshCell.setOnDragDetected(event -> {
                meshCell.setCursor(Cursor.MOVE);
                meshCell.startFullDrag();
                event.consume();
            });
            // Drag and drop ends
            meshCell.setOnMouseDragExited(event -> {
                meshCell.setCursor(Cursor.DEFAULT);
                event.consume();
            });

            // Drag and drop run
            scene.setOnMouseDragOver(event -> {
                Point3D clickCoordinates = event.getPickResult().getIntersectedPoint();
                Bounds cameraCoordinates = scene.getCamera().getBoundsInParent();
                Point3D cameraPoint = new Point3D(cameraCoordinates.getMinX(), cameraCoordinates.getMinY(), cameraCoordinates.getMinZ());

                double cosWithAxisX = ((clickCoordinates.getX() - cameraPoint.getX()) * Math.signum(clickCoordinates.getX() - cameraPoint.getX())) /
                        (Math.sqrt(Math.pow(clickCoordinates.getX() - cameraPoint.getX(), 2) + Math.pow(clickCoordinates.getZ() - cameraPoint.getZ(), 2)));

                double cosWithAxisZ = ((clickCoordinates.getZ() - cameraPoint.getZ()) * Math.signum(clickCoordinates.getZ() - cameraPoint.getZ())) /
                        (Math.sqrt(Math.pow(clickCoordinates.getX() - cameraPoint.getX(), 2) + Math.pow(clickCoordinates.getZ() - cameraPoint.getZ(), 2)));

                cell.setYCoordinate(cameraPoint.getY());

                if (cosWithAxisX >= 0.7) {
                    cell.setXCoordinate(2 * cameraPoint.getX() / 3);
                    cell.setZCoordinate(clickCoordinates.getZ());
                } else if (cosWithAxisZ >= 0.7) {
                    cell.setXCoordinate(clickCoordinates.getX());
                    cell.setZCoordinate(2 * cameraPoint.getZ() / 3);
                }

                main.deleteChild(cell.getId());
                main.addChild(cell);
                event.consume();
            });
        }
    };

    private static void createLinesForNewCell(Cell newCell, ObservableList<TetgenPoint> newCellTetgenPointData, ObservableList<ArrayList<Integer>> facetData) {
        // create lines
        ArrayList<TwoIntegers> lineList = new ArrayList<>();
        for (ArrayList<Integer> facet : facetData) {
            for (int j = 1; j < facet.size(); j++) {
                TwoIntegers ti = new TwoIntegers(j, facet.get(j - 1), facet.get(j));
                if (!lineList.contains(ti)) {
                    lineList.add(ti);
                }
            }

            TwoIntegers ti = new TwoIntegers(facet.size(), facet.get(facet.size() - 1), facet.get(0));
            if (!lineList.contains(ti)) {
                lineList.add(ti);
            }
        }

        ArrayList<Line> lines = new ArrayList<>();
        for (int j = 0; j < lineList.size(); j++) {
            TetgenPoint point1 = newCellTetgenPointData.get(lineList.get(j).getPoint1() - 1);
            TetgenPoint point2 = newCellTetgenPointData.get(lineList.get(j).getPoint2() - 1);
            if (Math.abs(point1.getY() - point2.getY()) < 0.0001) {
                lines.add(new Line(new Node(point1.getX(), point1.getZ(), point1.getY()),
                        new Node(point2.getX(), point2.getZ(), point2.getY())));
            }
        }
        LineEquations.addLine(newCell.getId(), lines);
    }
}
