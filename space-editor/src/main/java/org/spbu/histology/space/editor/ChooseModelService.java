package org.spbu.histology.space.editor;

import javafx.application.Platform;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import org.openide.util.Lookup;
import org.spbu.histology.model.*;
import org.spbu.histology.space.editor.spreadIcon.SpreadHistionService;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ChooseModelService extends HomeController {

    private static final String USER_DIRECTORY_ROOT = FileSystemView.getFileSystemView().getDefaultDirectory().getPath()
            + "\\HistologyApp" + System.getProperty("sun.arch.data.model");


    public static void displayModalWindowWithChoice() {
        File folder = new File(USER_DIRECTORY_ROOT + "\\startUpChoiceModels");
        File[] listOfFiles = folder.listFiles();
        List<Button> models = new ArrayList<>();

        if (listOfFiles == null) {
            throw new RuntimeException("Nothing found in " + folder + " ,here should be images of carpets");
        }

        Stage stage = new Stage();

        HBox root = new HBox();
        root.setPadding(new Insets(20));
        root.setSpacing(5);

        Scene scene = new Scene(root, 500, 700);

        for (File file : listOfFiles) {
            Image image = new Image(file.toURI().toString());
            BackgroundImage backgroundImage = new BackgroundImage(image,
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

            Background background = new Background(backgroundImage);
            Button button = new Button();
            button.setMinSize(image.getWidth(), image.getHeight());
            button.setBackground(background);

            button.setOnMouseClicked(event -> {
                CrossSectionVisualization.getPolygonMap().addListener(polygonListener);
                CrossSectionVisualization.getLineMap().addListener(lineListener);
                CrossSectionPlane.setYCoordinate("-800");

                LoadModelService.displayHistion(file.getName().replace(".png", ".txt"));
                new SpreadHistionService().spread(false);
                Platform.runLater(ChooseModelService::plotCells);
                Platform.runLater(ChooseModelService::plotCellConnections);
                Platform.runLater(ChooseModelService::plotHistionConnections);
                stage.hide();
                root.getChildren().clear();
                modalWindow(stage);
            });

            stage.setTitle("Выберите мозаику, чтобы начать!");
            models.add(button);
        }

        root.setSpacing(60.0);
        root.getChildren().addAll(models);

        stage.setScene(scene);

        scene.setRoot(root);
        stage.showAndWait();

        stage.setOnCloseRequest(event -> {
            CrossSectionVisualization.getPolygonMap().removeListener(polygonListener);
            CrossSectionVisualization.getLineMap().removeListener(lineListener);
        });
    }


    private static final int paneSize = 700;
    private static final Group standGroup = new Group();
    private static final Group cellGroup = new Group();
    private static double scale = 1;

    private static final HistionManager histionManager = Lookup.getDefault().lookup(HistionManager.class);

    private static final Map<Circle, List<Line>> injected = new HashMap<>();

    private static int clickCount = 0;
    private static int clickCount1 = 0;

    private static void modalWindow(Stage stage) {
        VBox mainPane = new VBox();

        VBox standPane = new VBox();
        standPane.setAlignment(Pos.CENTER);
        standPane.getChildren().add(standGroup);
        standPane.setScaleX(scale);
        standPane.setScaleY(scale);

        VBox cellPane = new VBox();
        cellPane.setAlignment(Pos.CENTER);
        cellPane.getChildren().add(cellGroup);
        cellPane.setScaleX(scale);
        cellPane.setScaleY(scale);

        SplitPane rootPane = new SplitPane();
        rootPane.getItems().addAll(standPane, cellPane);

        rootPane.setMinHeight(500);
        rootPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        rootPane.setStyle("-fx-focus-color: transparent;");

        standPane.setOnMouseClicked(event -> {

            if (event.getButton().equals(MouseButton.PRIMARY)) {

                Point2D sceneToPane = standGroup.sceneToLocal(event.getSceneX(), event.getSceneY());
                Circle circle = new Circle(sceneToPane.getX(), sceneToPane.getY(), 5);
                circle.setFill(Color.GREEN);

                List<Point2D> collect1 = CrossSectionVisualization.getVertexToClickCount()
                        .keySet()
                        .stream()
                        .map(polygon -> {
                            ObservableList<Double> points = polygon.getPoints();
                            return polygon.localToScene(points.get(0), points.get(1));
                        })
                        .collect(Collectors.toList());

                Optional<Point2D> first = collect1.stream().filter(point2D -> {
                    return Math.abs(Math.abs(circle.getCenterX()) - Math.abs(point2D.getX())) <= 8
                            && Math.abs(Math.abs(circle.getCenterY()) - Math.abs(point2D.getY())) <= 8;
                }).findFirst();


                if (first.isPresent()) {
                    circle.setCenterX(first.get().getX());
                    circle.setCenterY(first.get().getY());

//                    CrossSectionVisualization.getVertexToClickCount()
                } else {
                    return;
                }

                List<Circle> nearestCells = new ArrayList<>();
                List<Line> lineList = new ArrayList<>();

                averagePointsOfCells.values()
                        .forEach(entry -> {
                            List<Circle> collect = entry.values()
                                    .stream()
                                    .filter(cell -> {
                                        Point2D point = cell.localToParent(cell.getCenterX(), cell.getCenterY());
                                        double xDiff = Math.abs(point.getX() - circle.getCenterX());
                                        double yDiff = Math.abs(point.getY() - circle.getCenterY());
                                        return xDiff <= 110 && yDiff <= 110;
                                    })
                                    .collect(Collectors.toList());
                            nearestCells.addAll(collect);
                        });

                nearestCells.forEach(cell -> {
                    Point2D point = cell.localToParent(cell.getCenterX(), cell.getCenterY());
                    Line line = new Line(
                            point.getX(),
                            point.getY(),
                            circle.getCenterX(),
                            circle.getCenterY()
                    );
                    line.setStrokeWidth(2f);

                    lineList.add(line);
                });

                standGroup.getChildren().addAll(lineList);
                standGroup.getChildren().add(circle);

                injected.put(circle, lineList);
            }
        });


        Button button = new Button();
        button.setText("Трансформировать");
        button.setAlignment(Pos.CENTER);
        button.setOnMouseClicked(event -> {
            clickCount++;
            if (clickCount == 1) {
                topologicalPrimitiveNumber1();
                Kal();
            }

            if (clickCount == 2) {
                topologicalPrimitiveNumber2();
            }
        });


        Button button1 = new Button();
        button1.setText("Показать");
        button1.setAlignment(Pos.CENTER);
        button1.setOnMouseClicked(event -> {
            clickCount1++;
            if (clickCount1 == 1) {
                CrossSectionPlane.setYCoordinate("-600");
            }
            if (clickCount1 == 2) {
                CrossSectionPlane.setYCoordinate("240");
            }
        });

        mainPane.setAlignment(Pos.CENTER);
        mainPane.setPadding(new Insets(5, 0, 0, 0));
        mainPane.setSpacing(10);
        mainPane.getChildren().addAll(rootPane, button, button1);

        Scene scene = new Scene(mainPane);

        stage.setTitle("Определите, куда будут внедрены новые клетки!");
        stage.setMinHeight(730);
        stage.setMinWidth(1300);

        stage.setScene(scene);
        stage.show();
    }


    private static Map<Integer, Map<Integer, Circle>> averagePointsOfCells = new ConcurrentHashMap<>();

    private static void getPointsFor2dStand() {
        histionManager.getAllHistions().forEach(histion -> histion.getItems().forEach(cell -> {
            Map<Integer, Circle> partsAverage = new HashMap<>();

            if (cell.getName().contains("HexR")) {
                //            for (int j = 0; j < cell.getItems().size() - 1; j++) {
                if (cell.getItems().get(0).getPointData().size() == 1) {
                    cell.getItems().get(0).setAvgNode();
                }
                Node avgNode = cell.getItems().get(0).getAvgNode();


                Circle circle = new Circle(avgNode.x, -avgNode.z, 5);
                circle.setTranslateX(paneSize / 2);
                circle.setTranslateY(paneSize / 2);

                partsAverage.put(0, circle);
//            }
                averagePointsOfCells.put(cell.getId(), partsAverage);

            }


        }));
    }

    private static List<Line> graphEdges = new ArrayList<>();

    private static void drawConnections(Cell oneCell, Cell anotherCell) {
        int countP1 = -1;
        int countP2 = -1;
        for (int i = 0; i < 1; i++) {
            countP1++;
            int sizep1 = oneCell.getItems().get(i).getPointData().size();
            for (int j = 0; j < 1; j++) {
                int sizep2 = anotherCell.getItems().get(i).getPointData().size();
                int counter = pointsCount(oneCell.getItems().get(j), anotherCell.getItems().get(j));

                if (counter > 1 || counter == 1 && (sizep1 == 1 || sizep2 == 1)) {

                    Line line = new Line(
                            oneCell.getItems().get(i).getAvgNode().x,
                            -oneCell.getItems().get(i).getAvgNode().z,
                            anotherCell.getItems().get(j).getAvgNode().x,
                            -anotherCell.getItems().get(j).getAvgNode().z
                    );

                    line.setStrokeWidth(2f);
                    line.setTranslateX(paneSize / 2);
                    line.setTranslateY(paneSize / 2);
                    standGroup.getChildren().add(line);
                    graphEdges.add(line);
                    break;
                } else if (counter == 1 && (sizep1 == 2 && sizep2 == 2)) {
                    if (i < oneCell.getItems().size() - 1 && j < anotherCell.getItems().size() - 1) {
                        if (2 < oneCell.getItems().get(i + 1).getPointData().size() && 2 < anotherCell.getItems().get(j + 1).getPointData().size()
                                && pointsCount(oneCell.getItems().get(i + 1), anotherCell.getItems().get(j + 1)) > 1) {

                            Line line = new Line(
                                    oneCell.getItems().get(i).getAvgNode().x,
                                    -oneCell.getItems().get(i).getAvgNode().z,
                                    anotherCell.getItems().get(j).getAvgNode().x,
                                    -anotherCell.getItems().get(j).getAvgNode().z
                            );

                            line.setStrokeWidth(2f);
                            line.setTranslateX(paneSize / 2);
                            line.setTranslateY(paneSize / 2);

                            standGroup.getChildren().add(line);
                            graphEdges.add(line);
                            break;
                        }
                    }
                    if (i > 0 && j > 0) {
                        if (2 < oneCell.getItems().get(i - 1).getPointData().size() && 2 < anotherCell.getItems().get(j - 1).getPointData().size() && pointsCount(oneCell.getItems().get(i - 1), anotherCell.getItems().get(j - 1)) > 1) {

                            Line line = new Line(
                                    oneCell.getItems().get(i).getAvgNode().x,
                                    -oneCell.getItems().get(i).getAvgNode().z,
                                    anotherCell.getItems().get(j).getAvgNode().x,
                                    -anotherCell.getItems().get(j).getAvgNode().z
                            );

                            line.setStrokeWidth(2f);
                            line.setTranslateX(paneSize / 2);
                            line.setTranslateY(paneSize / 2);

                            standGroup.getChildren().add(line);
                            graphEdges.add(line);
                            break;
                        }
                    }
                }
            }
        }
    }

    private static void plotCells() {
        getPointsFor2dStand();
        histionManager.getAllHistions().forEach(histion -> {
            histion.getItems().forEach(c ->
                    averagePointsOfCells.values().forEach(map -> standGroup.getChildren().addAll(map.values())));
        });

    }

    private static void plotCellConnections() {
        histionManager.getAllHistions().forEach(histion -> {
            for (int i = 0; i < histion.getItems().size(); i++) {
                for (int j = 0; j < histion.getItems().size(); j++) {
                    if (i != j) {
                        if (histion.getItems().get(i).getName().contains("HexR") && histion.getItems().get(j).getName().contains("HexR"))
                            drawConnections(histion.getItems().get(i), histion.getItems().get(j));
                    }
                }
            }
        });
    }

    private static void plotHistionConnections() {
        for (int i = 0; i < histionManager.getAllHistions().size(); i++) {
            for (int j = 0; j < histionManager.getAllHistions().size(); j++) {
                if (i != j) {
                    Histion histion1 = histionManager.getAllHistions().get(i);
                    Histion histion2 = histionManager.getAllHistions().get(j);
                    histion1.getItems().forEach(c1 -> {
                        if (c1.getName().contains("HexR")) {
                            histion2.getItems().forEach(c2 -> {
                                if (c2.getName().contains("HexR"))
                                    drawConnections(c1, c2);
                            });
                        }
                    });
                }
            }
        }
    }

    private static int pointsCount(Part p1, Part p2) {
        int result = 0;
        double eps = 2;
        for (TetgenPoint point1 : p1.getPointData()) {
            for (TetgenPoint point2 : p2.getPointData()) {
                if (twoPointsRange(point1, point2) <= eps) {
                    result++;
                }
            }
        }
        return result;
    }

    private static double twoPointsRange(TetgenPoint p1, TetgenPoint p2) {
        double result;
        result = Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2.0) + Math.pow(p1.getY() - p2.getY(), 2.0) + Math.pow(p1.getZ() - p2.getZ(), 2.0));
        return result;
    }

    private static final MapChangeListener<Integer, ArrayList<Line>> lineListener
            = (change) -> {
        if (change.wasRemoved()) {
            for (Line l : change.getValueRemoved()) {
                cellGroup.getChildren().remove(l);
            }
        }
        if (change.wasAdded()) {
            for (Line l : change.getValueAdded()) {
                l.setTranslateX(paneSize / 2);
                l.setTranslateY(paneSize / 2);
                cellGroup.getChildren().add(l);
            }
        }
    };

    private final static MapChangeListener<Integer, ArrayList<Polygon>> polygonListener = (change) -> {
        if (change.wasRemoved()) {
            cellGroup.getChildren().removeAll(CrossSectionVisualization.getVertexToClickCount().keySet());
            for (Polygon p : change.getValueRemoved()) {
                cellGroup.getChildren().remove(p);
            }
        }

        if (change.wasAdded()) {
            for (Polygon p : change.getValueAdded()) {
                p.setTranslateX(paneSize / 2);
                p.setTranslateY(paneSize / 2);
                cellGroup.getChildren().add(p);

                List<Vertex> vertexes = new ArrayList<>();

                CrossSectionVisualization.getPolygonMap()
                        .forEach((key, value1) -> value1.forEach(polygon -> {
                            for (int i = 0; i < polygon.getPoints().size(); i += 2) {
                                polygon.setTranslateX(paneSize / 2);
                                polygon.setTranslateY(paneSize / 2);
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
                            polygon.setStrokeWidth(0);
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

                            cellGroup.getChildren().add(polygon);
                        });
            }
        }
    };

    /// TOPOLOGICAL PRIMITIVES SEARCH:

    private static List<Line> newConnections = new ArrayList<>();

    private static void topologicalPrimitiveNumber1() {
        System.out.println("Search for topological primitive #1");

        for (Map.Entry<Circle, List<Line>> e : injected.entrySet()) {
            Circle oneCell = e.getKey();
            List<Line> oneLines = e.getValue();
            List<Point2D> onePoints = new ArrayList<>();

            oneLines.forEach(line -> onePoints.add(new Point2D(line.getStartX(), line.getStartY())));

            for (Map.Entry<Circle, List<Line>> entry : injected.entrySet()) {
                Circle anotherCell = entry.getKey();
                List<Line> anotherList = entry.getValue();
                if (!oneCell.equals(anotherCell)) {
                    List<Point2D> anotherPoints = new ArrayList<>();

                    anotherList.forEach(line -> anotherPoints.add(new Point2D(line.getStartX(), line.getStartY())));

                    List<Point2D> commonDots = onePoints.stream()
                            .filter(anotherPoints::contains)
                            .collect(Collectors.toList());

                    if (commonDots.size() == 2) {

                        Polygon p = null;

                        for (ArrayList<Polygon> value : CrossSectionVisualization.getPolygonMap().values()) {
                            if (!value.isEmpty()) {
                                p = value.get(0);
                            }
                        }


                        Point2D point1 = p.parentToLocal(commonDots.get(0));
                        Point2D point2 = p.parentToLocal(commonDots.get(1));


                        for (Line line : graphEdges) {

                            if ((Math.abs(line.getStartX() - point1.getX()) < 0.01 && Math.abs(line.getStartY() - point1.getY()) < 0.01) &&
                                    (Math.abs(line.getEndX() - point2.getX()) < 0.01 && Math.abs(line.getEndY() - point2.getY()) < 0.01)) {
                                standGroup.getChildren().remove(line);
                            }

                            if ((Math.abs(line.getStartX() - point2.getX()) < 0.01 && Math.abs(line.getStartY() - point2.getY()) < 0.01) &&
                                    (Math.abs(line.getEndX() - point1.getX()) < 0.01 && Math.abs(line.getEndY() - point1.getY()) < 0.01)) {
                                standGroup.getChildren().remove(line);
                            }


                        }


                        Line newConnection = new Line(oneCell.getCenterX()
                                , oneCell.getCenterY()
                                , anotherCell.getCenterX()
                                , anotherCell.getCenterY());

                        newConnection.setStrokeWidth(2f);

                        newConnections.add(newConnection);

                        standGroup.getChildren().add(newConnection);
                    }

                }
            }
        }
    }

    private static void topologicalPrimitiveNumber3() {

    }


    private static void topologicalPrimitiveNumber2() {


    }


    private static void Kal() {
        System.out.println("Kal transformation");

        List<Point2D> transformedInjectedCells =
                injected.keySet().stream()
                        .map(circle -> new Point2D(circle.getCenterX(), circle.getCenterY()))
                        .collect(Collectors.toList());

        List<Circle> affectedCells = new ArrayList<>();
        List<Line> addedConnections = new ArrayList<>();


        standGroup.getChildren().forEach(node -> {

            if (node instanceof Line) {
                Line line = (Line) node;
                double endX = line.getEndX();
                double endY = line.getEndY();

                transformedInjectedCells
                        .forEach(point2D -> {
                            if (point2D.getY() == endY && point2D.getX() == endX) {
                                if (!newConnections.contains(line)) {
                                    addedConnections.add(line);
                                }
                            }
                        });
            }
        });

        standGroup.getChildren().forEach(node -> {
            if (node instanceof Circle) {
                Circle circle = (Circle) node;

                double x = circle.getCenterX();
                double y = circle.getCenterY();

                addedConnections.forEach(line -> {
                    Point2D transformed = CrossSectionVisualization.getPolygonMap().values().iterator().next().get(0).parentToLocal(line.getStartX(), line.getStartY());
                    if (transformed.getX() == x && transformed.getY() == y) {
                        affectedCells.add(circle);
                    }
                });
            }

        });

        List<Integer> kalList = new ArrayList<>();

        affectedCells.forEach(cell -> {
            for (Map.Entry<Integer, Map<Integer, Circle>> entry : averagePointsOfCells.entrySet()) {
                entry.getValue().values().forEach(circle -> {
                    if (circle.equals(cell)) {
                        kalList.add(entry.getKey());
                    }
                });
            }
        });

        List<Cell> collect = new ArrayList<>();
        for (Integer kal : kalList) {
            histionManager.getAllHistions()
                    .forEach(histion1 -> histion1.getItems().forEach(cell -> {
                        if (cell.getId().equals(kal)) {
                            collect.add(cell);
                        }
                    }));
        }


//        // TODO : Make cell smaller
        collect.forEach(cell -> {
            Cell newCell = new Cell(new Random().nextInt(), cell);

            cell.getItems().forEach(part -> {
                ObservableList<TetgenPoint> pointList = FXCollections.observableArrayList();

                part.getPointData().forEach(point -> {
                    System.out.println("in");
                    point.setX(point.getX() * 3 / 4);
                    point.setZ(point.getZ() * 3 / 4);

                    pointList.add(point);
                });

                Part part1 = new Part(part.getName(), pointList, new Random().nextInt());
                newCell.addChild(part1);
            });

            histionManager.getAllHistions().get(cell.getHistionId()).deleteChild(cell.getId());
            histionManager.getAllHistions().get(cell.getHistionId()).addChild(newCell);
        });
    }

    private static void plot() {

        injected.forEach((circle, lines) -> lines.forEach(line -> {
            double newCircleX = (line.getStartX() + line.getEndX()) / 2;
            double newCircleY = (line.getStartY() + line.getEndY()) / 2;

            Circle circle1 = new Circle(newCircleX, newCircleY, 5);
            circle1.setFill(Color.RED);

            standGroup.getChildren().add(circle1);
        }));

        newConnections.forEach(line -> {
            double newCircleX = (line.getStartX() + line.getEndX()) / 2;
            double newCircleY = (line.getStartY() + line.getEndY()) / 2;

            Circle circle1 = new Circle(newCircleX, newCircleY, 5);
            circle1.setFill(Color.RED);

            standGroup.getChildren().add(circle1);
        });
    }
}
