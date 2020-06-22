package org.spbu.histology.space.editor.spreadIcon;

import javafx.beans.property.*;
import org.openide.util.Lookup;
import org.spbu.histology.model.*;
import org.spbu.histology.space.editor.HistionRecurrence;
import org.spbu.histology.space.editor.HomeController;

import java.util.ArrayList;

public class SpreadHistionService extends HomeController {


    public void spreadWithParam(double shiftX, double shiftZ) {
        spread(false, shiftX, shiftZ);
    }

    public void spread(boolean showModalWindowWithSettings) {
        spread(showModalWindowWithSettings, 0, 0);
    }

    public void spread(boolean showModalWindowWithSettings, double shiftX, double shiftZ) {
        System.out.println("Spread call");
        IntegerProperty xUpperLimit = new SimpleIntegerProperty(1);
        IntegerProperty xLowerLimit = new SimpleIntegerProperty(1);
        IntegerProperty yUpperLimit = new SimpleIntegerProperty(1);
        IntegerProperty yLowerLimit = new SimpleIntegerProperty(1);
        IntegerProperty zUpperLimit = new SimpleIntegerProperty(1);
        IntegerProperty zLowerLimit = new SimpleIntegerProperty(1);

        DoubleProperty xShift = new SimpleDoubleProperty(0);
        DoubleProperty zShift = new SimpleDoubleProperty(0);
        BooleanProperty buttonPressed = new SimpleBooleanProperty(false);

        if (showModalWindowWithSettings) {
            HistionRecurrence.display("Spacing", xUpperLimit, xLowerLimit,
                    yUpperLimit, yLowerLimit, zUpperLimit, zLowerLimit,
                    buttonPressed, xShift, zShift);
        } else {
            xUpperLimit.set(1);
            xLowerLimit.set(1);
            yUpperLimit.set(0);
            yLowerLimit.set(0);
            zUpperLimit.set(1);
            zLowerLimit.set(1);
            xShift.set(shiftX);
            zShift.set(shiftZ);
            buttonPressed.set(true);
            RecurrenceShifts.setXShift(xShift.get());
            RecurrenceShifts.setZShift(zShift.get());
        }
        spread(xUpperLimit, xLowerLimit, yUpperLimit, yLowerLimit, zUpperLimit, zLowerLimit, xShift, zShift);
    }

    public void spread(IntegerProperty xUpperLimit, IntegerProperty xLowerLimit, IntegerProperty yUpperLimit, IntegerProperty yLowerLimit, IntegerProperty zUpperLimit, IntegerProperty zLowerLimit, DoubleProperty xShift, DoubleProperty zShift) {
        HistionManager hm = Lookup.getDefault().lookup(HistionManager.class);
        final DoubleProperty leftX = new SimpleDoubleProperty(10000);
        final DoubleProperty rightX = new SimpleDoubleProperty(-10000);
        final DoubleProperty upperZ = new SimpleDoubleProperty(-10000);
        final DoubleProperty bottomZ = new SimpleDoubleProperty(10000);
        final DoubleProperty upperY = new SimpleDoubleProperty(-10000);
        final DoubleProperty bottomY = new SimpleDoubleProperty(10000);

        ArrayList<TetgenPoint> pl = new ArrayList<>();

        hm.getHistionMap().get(0).getItems().forEach(c -> {
            if (c.getShow()) {
                c.getTransformedPointData().forEach(point -> {
                    pl.add(point);
                    if (point.getX() < leftX.get()) {
                        leftX.set(point.getX());
                    }
                    if (point.getX() > rightX.get()) {
                        rightX.set(point.getX());
                    }
                    if (point.getZ() < bottomZ.get()) {
                        bottomZ.set(point.getZ());
                    }
                    if (point.getZ() > upperZ.get()) {
                        upperZ.set(point.getZ());
                    }
                    if (point.getY() < bottomY.get()) {
                        bottomY.set(point.getY());
                    }
                    if (point.getY() > upperY.get()) {
                        upperY.set(point.getY());
                    }
                });
            }
        });

        DoubleProperty xSpace = new SimpleDoubleProperty(0);
        DoubleProperty ySpace = new SimpleDoubleProperty(0);
        DoubleProperty xzSpace = new SimpleDoubleProperty(0);
        DoubleProperty zSpace = new SimpleDoubleProperty(0);

        for (TetgenPoint point : pl) {
            hm.getHistionMap().get(0).getItems().forEach(c -> {
                if (c.getShow()) {
                    double minY = 10000;
                    double maxY = -10000;
                    for (TetgenPoint p : c.getTransformedPointData()) {
                        if (p.getY() < minY) {
                            minY = p.getY();
                        }
                        if (p.getY() > maxY) {
                            maxY = p.getY();
                        }
                    }
                    if (maxY - minY > ySpace.get()) {
                        ySpace.set(maxY - minY);
                    }
                    for (Line line : LineEquations.getLineMap().get(c.getId())) {
                        if (Math.abs(point.getY() - line.p1.z) < 0.000001) {
                            Node p1 = intersect(new Line(new Node(point.getX(), point.getZ(), point.getY()), new Node(point.getX() + 1, point.getZ(), point.getY())), line);
                            Node p2 = intersect(new Line(new Node(point.getX(), point.getZ(), point.getY()), new Node(point.getX(), point.getZ() + 1, point.getY())), line);

                            if (p1.z == 0) {
                                if (Math.abs(point.getX() - p1.x) > xSpace.get()) {
                                    xSpace.set(Math.abs(point.getX() - p1.x));
                                }
                            }
                            if (p2.z == 0) {
                                if (Math.abs(point.getZ() - p2.y) > zSpace.get()) {
                                    zSpace.set(Math.abs(point.getZ() - p2.y));
                                }
                            }
                        }
                    }
                }
            });
        }

        ArrayList<Double> yArr = new ArrayList<>();
        for (TetgenPoint point : pl) {
            if (!yArr.contains(point.getY())) {
                yArr.add(point.getY());
            }
        }
        final IntegerProperty count = new SimpleIntegerProperty(0);
        final IntegerProperty count2 = new SimpleIntegerProperty(0);
        for (TetgenPoint point : pl) {
            for (Double y : yArr) {
                count.set(0);
                count2.set(0);
                if (Math.abs(point.getY() - y) < 0.000001) {
                    continue;
                }
                hm.getHistionMap().get(0).getItems().forEach(c -> {
                    if (c.getShow()) {
                        for (Line line : LineEquations.getLineMap().get(c.getId())) {
                            if (Math.abs(y - line.p1.z) < 0.000001) {
                                {
                                    Node p1 = intersect2(new Line(new Node(point.getX(), point.getZ(), point.getY()), new Node(point.getX() + 1, point.getZ(), point.getY())), line);
                                    Node p2 = intersect3(new Line(new Node(point.getX(), point.getZ(), point.getY()), new Node(point.getX() - 1, point.getZ(), point.getY())), line);
                                    if (p1.z == 0) {
                                        count.set(count.get() + 1);
                                    }
                                    if (p2.z == 0) {
                                        count2.set(count2.get() + 1);
                                    }
                                }
                            }
                        }
                    }
                });
                if ((count.get() % 2 == 1) || (count2.get() % 2 == 1)) {
                    if (Math.abs(point.getY() - y) > ySpace.get()) {
                        ySpace.set(Math.abs(point.getY() - y));
                    }
                }
            }
        }

        double deltaX = xSpace.get();
        double deltaY = ySpace.get();
        double deltaZ = zSpace.get();

        double hZ = hm.getHistionMap().get(0).getZCoordinate();
        double hY = hm.getHistionMap().get(0).getYCoordinate();
        double hX = hm.getHistionMap().get(0).getXCoordinate();

        double xUpperBoundary = deltaX * xUpperLimit.get();
        double xLowerBoundary = deltaX * xLowerLimit.get();
        double yUpperBoundary = deltaY * yUpperLimit.get();
        double yLowerBoundary = deltaY * yLowerLimit.get();
        double zUpperBoundary = deltaZ * zUpperLimit.get();
        double zLowerBoundary = deltaZ * zLowerLimit.get();

        if ((xUpperLimit.get() >= 0) && (xLowerLimit.get() >= 0)) {
            while (hX < xUpperBoundary) {
                Histion newHistion = new Histion(hm.getHistionMap().get(0));
                newHistion.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                hm.getHistionMap().get(0).getItems().forEach(c -> {
                    Cell newCell = new Cell(c, newHistion.getId());
                    c.getItems().forEach(p -> {
                        Part newP = new Part(p.getId(), p);
                        newP.getPointData().clear();
                        p.getPointData().forEach(point -> {
                            TetgenPoint newPoint = new TetgenPoint(point);
                            newPoint.setX(point.getX() + deltaX + xShift.get());
                            newPoint.setZ(point.getZ() + zShift.get());
                            newP.getPointData().add(newPoint);
                        });
                        newP.setAvgNode();
                        newCell.addChild(newP);


                    });


                    newCell.setXCoordinate(newCell.getXCoordinate() + deltaX + xShift.get());
                    newCell.setZCoordinate(newCell.getZCoordinate() + zShift.get());


                    newHistion.addChild(newCell);
                });
                newHistion.setXCoordinate(hX + deltaX + xShift.get());
                newHistion.setZCoordinate(hZ + zShift.get());
                hm.addHistion(newHistion);
                hX += deltaX + xShift.get();
                hZ += zShift.get();
            }
            hX = hm.getHistionMap().get(0).getXCoordinate();
            hZ = hm.getHistionMap().get(0).getZCoordinate();
            while (hX > -xLowerBoundary) {
                Histion newHistion = new Histion(hm.getHistionMap().get(0));
                newHistion.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                hm.getHistionMap().get(0).getItems().forEach(c -> {
                    Cell newCell = new Cell(c, newHistion.getId());
                    c.getItems().forEach(p -> {

                        Part newP = new Part(p.getId(), p);
                        newP.getPointData().clear();
                        p.getPointData().forEach(point -> {
                            TetgenPoint newPoint = new TetgenPoint(point);
                            newPoint.setX(point.getX() - deltaX - xShift.get());
                            newPoint.setZ(point.getZ() - zShift.get());
                            newP.getPointData().add(newPoint);
                        });
                        newP.setAvgNode();
                        newCell.addChild(newP);


                    });


                    newCell.setXCoordinate(newCell.getXCoordinate() - deltaX - xShift.get());
                    newCell.setZCoordinate(newCell.getZCoordinate() - zShift.get());


                    newHistion.addChild(newCell);
                });

                newHistion.setXCoordinate(hX - deltaX - xShift.get());
                newHistion.setZCoordinate(hZ - zShift.get());
                hm.addHistion(newHistion);
                hX -= deltaX + xShift.get();
                hZ -= zShift.get();
            }
            hX = hm.getHistionMap().get(0).getXCoordinate();
            hZ = hm.getHistionMap().get(0).getZCoordinate();
        }

        if ((yUpperLimit.get() >= 0) && (yLowerLimit.get() >= 0)) {
            while (hY < yUpperBoundary) {
                Histion newHistion = new Histion(hm.getHistionMap().get(0));
                newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Y: "
                        + String.valueOf(hY + deltaY) + " ; Z: "
                        + String.valueOf(hZ) + ">");
                hm.getHistionMap().get(0).getItems().forEach(c -> {
                    Cell newCell = new Cell(c, newHistion.getId());
                    c.getItems().forEach(p -> {


                        Part newP = new Part(p.getId(), p);
                        newP.getPointData().clear();
                        p.getPointData().forEach(point -> {
                            TetgenPoint newPoint = new TetgenPoint(point);
                            newPoint.setY(point.getY() + deltaY);
                            newP.getPointData().add(newPoint);
                        });
                        newP.setAvgNode();
                        newCell.addChild(newP);


                    });


                    newCell.setYCoordinate(newCell.getYCoordinate() + deltaY);

                    newHistion.addChild(newCell);
                });
                newHistion.setYCoordinate(hY + deltaY);
                hm.addHistion(newHistion);
                hY += deltaY;
            }
            hY = hm.getHistionMap().get(0).getYCoordinate();
            while (hY > -yLowerBoundary) {
                Histion newHistion = new Histion(hm.getHistionMap().get(0));
                newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Y: "
                        + String.valueOf(hY - deltaY) + " ; Z: "
                        + String.valueOf(hZ) + ">");
                hm.getHistionMap().get(0).getItems().forEach(c -> {
                    Cell newCell = new Cell(c, newHistion.getId());
                    c.getItems().forEach(p -> {

                        Part newP = new Part(p.getId(), p);
                        newP.getPointData().clear();
                        p.getPointData().forEach(point -> {
                            TetgenPoint newPoint = new TetgenPoint(point);
                            newPoint.setY(point.getY() - deltaY);
                            newP.getPointData().add(newPoint);
                        });
                        newP.setAvgNode();
                        newCell.addChild(newP);


                    });


                    newCell.setYCoordinate(newCell.getYCoordinate() - deltaY);


                    newHistion.addChild(newCell);
                });
                newHistion.setYCoordinate(hY - deltaY);
                hm.addHistion(newHistion);
                hY -= deltaY;
            }
            hY = hm.getHistionMap().get(0).getYCoordinate();
        }

        if ((zUpperLimit.get() >= 0) && (zLowerLimit.get() >= 0)) {
            while (hZ < zUpperBoundary) {
                Histion newHistion = new Histion(hm.getHistionMap().get(0));
                newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                hm.getHistionMap().get(0).getItems().forEach(c -> {
                    Cell newCell = new Cell(c, newHistion.getId());
                    c.getItems().forEach(p -> {

                        Part newP = new Part(p.getId(), p);
                        newP.getPointData().clear();
                        p.getPointData().forEach(point -> {
                            TetgenPoint newPoint = new TetgenPoint(point);
                            newPoint.setZ(point.getZ() + deltaZ);
                            newP.getPointData().add(newPoint);
                        });
                        newP.setAvgNode();
                        newCell.addChild(newP);


                    });


                    newCell.setZCoordinate(newCell.getZCoordinate() + deltaZ);


                    newHistion.addChild(newCell);
                });
                newHistion.setZCoordinate(hZ + deltaZ);
                hm.addHistion(newHistion);
                hZ += deltaZ;
            }
            hZ = hm.getHistionMap().get(0).getZCoordinate();
            while (hZ > -zLowerBoundary) {
                Histion newHistion = new Histion(hm.getHistionMap().get(0));
                newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                hm.getHistionMap().get(0).getItems().forEach(c -> {
                    Cell newCell = new Cell(c, newHistion.getId());
                    c.getItems().forEach(p -> {

                        Part newP = new Part(p.getId(), p);
                        newP.getPointData().clear();
                        p.getPointData().forEach(point -> {
                            TetgenPoint newPoint = new TetgenPoint(point);
                            newPoint.setZ(point.getZ() - deltaZ);
                            newP.getPointData().add(newPoint);
                        });
                        newP.setAvgNode();
                        newCell.addChild(newP);


                    });


                    newCell.setZCoordinate(newCell.getZCoordinate() - deltaZ);


                    newHistion.addChild(newCell);
                });
                newHistion.setZCoordinate(hZ - deltaZ);
                hm.addHistion(newHistion);
                hZ -= deltaZ;
            }
            hZ = hm.getHistionMap().get(0).getZCoordinate();
        }

        int num = 0;
        if (((xUpperLimit.get() >= 0) && (xLowerLimit.get() >= 0))
                && ((zUpperLimit.get() >= 0) && (zLowerLimit.get() >= 0))) {
            while (hX < xUpperBoundary) {
                hZ = hm.getHistionMap().get(0).getZCoordinate() + num * zShift.get();
                while (hZ - num * zShift.get() < zUpperBoundary) {
                    Histion newHistion = new Histion(hm.getHistionMap().get(0));
                    newHistion.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                    hm.getHistionMap().get(0).getItems().forEach(c -> {
                        Cell newCell = new Cell(c, newHistion.getId());
                        c.getItems().forEach(p -> {

                            Part newP = new Part(p.getId(), p);
                            newP.getPointData().clear();
                            p.getPointData().forEach(point -> {
                                TetgenPoint newPoint = new TetgenPoint(point);
                                newPoint.setX(point.getX() + deltaX + xShift.get());
                                newPoint.setZ(point.getZ() + deltaZ + zShift.get());
                                newP.getPointData().add(newPoint);
                            });
                            newP.setAvgNode();
                            newCell.addChild(newP);


                        });

                        newCell.setXCoordinate(newCell.getXCoordinate() + deltaX + xShift.get());
                        newCell.setZCoordinate(newCell.getZCoordinate() + deltaZ + zShift.get());

                        newHistion.addChild(newCell);
                    });
                    newHistion.setZCoordinate(hZ + deltaZ + zShift.get());
                    newHistion.setXCoordinate(hX + deltaX + xShift.get());
                    hm.addHistion(newHistion);
                    hZ += deltaZ;
                }
                hZ = hm.getHistionMap().get(0).getZCoordinate() + num * zShift.get();
                while (hZ - num * zShift.get() > -zLowerBoundary) {
                    Histion newHistion = new Histion(hm.getHistionMap().get(0));
                    newHistion.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                    hm.getHistionMap().get(0).getItems().forEach(c -> {
                        Cell newCell = new Cell(c, newHistion.getId());
                        c.getItems().forEach(p -> {

                            Part newP = new Part(p.getId(), p);
                            newP.getPointData().clear();
                            p.getPointData().forEach(point -> {
                                TetgenPoint newPoint = new TetgenPoint(point);
                                newPoint.setX(point.getX() + deltaX + xShift.get());
                                newPoint.setZ(point.getZ() - deltaZ + zShift.get());
                                newP.getPointData().add(newPoint);
                            });
                            newP.setAvgNode();
                            newCell.addChild(newP);


                        });


                        newCell.setXCoordinate(newCell.getXCoordinate() + deltaX + xShift.get());
                        newCell.setZCoordinate(newCell.getZCoordinate() - deltaZ + zShift.get());


                        newHistion.addChild(newCell);
                    });
                    newHistion.setZCoordinate(hZ - deltaZ + zShift.get());
                    newHistion.setXCoordinate(hX + deltaX + xShift.get());
                    hm.addHistion(newHistion);
                    hZ -= deltaZ;
                }
                hX += deltaX + xShift.get();
                num++;
            }
            hX = hm.getHistionMap().get(0).getXCoordinate();
            num = 0;
            while (hX > -xLowerBoundary) {
                hZ = hm.getHistionMap().get(0).getZCoordinate() - num * zShift.get();
                while (hZ + num * zShift.get() < zUpperBoundary) {
                    Histion newHistion = new Histion(hm.getHistionMap().get(0));
                    newHistion.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                    hm.getHistionMap().get(0).getItems().forEach(c -> {
                        Cell newCell = new Cell(c, newHistion.getId());
                        c.getItems().forEach(p -> {

                            Part newP = new Part(p.getId(), p);
                            newP.getPointData().clear();
                            p.getPointData().forEach(point -> {
                                TetgenPoint newPoint = new TetgenPoint(point);
                                newPoint.setX(point.getX() - deltaX - xShift.get());
                                newPoint.setZ(point.getZ() + deltaZ - zShift.get());
                                newP.getPointData().add(newPoint);
                            });
                            newP.setAvgNode();
                            newCell.addChild(newP);


                        });
                        newCell.setXCoordinate(newCell.getXCoordinate() - deltaX - xShift.get());
                        newCell.setZCoordinate(newCell.getZCoordinate() + deltaZ - zShift.get());
                        newHistion.addChild(newCell);
                    });
                    newHistion.setZCoordinate(hZ + deltaZ - zShift.get());
                    newHistion.setXCoordinate(hX - deltaX - xShift.get());
                    hm.addHistion(newHistion);
                    hZ += deltaZ;
                }
                hZ = hm.getHistionMap().get(0).getZCoordinate() - num * zShift.get();
                while (hZ + num * zShift.get() > -zLowerBoundary) {
                    Histion newHistion = new Histion(hm.getHistionMap().get(0));
                    newHistion.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                    hm.getHistionMap().get(0).getItems().forEach(c -> {
                        Cell newCell = new Cell(c, newHistion.getId());
                        c.getItems().forEach(p -> {

                            Part newP = new Part(p.getId(), p);
                            newP.getPointData().clear();
                            p.getPointData().forEach(point -> {
                                TetgenPoint newPoint = new TetgenPoint(point);
                                newPoint.setX(point.getX() - deltaX - xShift.get());
                                newPoint.setZ(point.getZ() - deltaZ - zShift.get());
                                newP.getPointData().add(newPoint);
                            });
                            newP.setAvgNode();
                            newCell.addChild(newP);


                        });


                        newCell.setXCoordinate(newCell.getXCoordinate() - deltaX - xShift.get());
                        newCell.setZCoordinate(newCell.getZCoordinate() - deltaZ - zShift.get());


                        newHistion.addChild(newCell);
                    });
                    newHistion.setZCoordinate(hZ - deltaZ - zShift.get());
                    newHistion.setXCoordinate(hX - deltaX - xShift.get());
                    hm.addHistion(newHistion);
                    hZ -= deltaZ;
                }
                hX -= deltaX + xShift.get();
                num++;
            }
        }

        if (((yUpperLimit.get() >= 0) && (yLowerLimit.get() >= 0))
                && ((zUpperLimit.get() >= 0) && (zLowerLimit.get() >= 0))) {
            num = 0;
            while (hY < yUpperBoundary) {
                num = 0;
                hZ = hm.getHistionMap().get(0).getZCoordinate();
                while (hZ < zUpperBoundary) {
                    Histion newHistion = new Histion(hm.getHistionMap().get(0));
                    newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                    hm.getHistionMap().get(0).getItems().forEach(c -> {
                        Cell newCell = new Cell(c, newHistion.getId());
                        c.getItems().forEach(p -> {

                            Part newP = new Part(p.getId(), p);
                            newP.getPointData().clear();
                            p.getPointData().forEach(point -> {
                                TetgenPoint newPoint = new TetgenPoint(point);
                                newPoint.setY(point.getY() + deltaY);
                                newPoint.setZ(point.getZ() + deltaZ);
                                newP.getPointData().add(newPoint);
                            });
                            newP.setAvgNode();
                            newCell.addChild(newP);


                        });


                        newCell.setYCoordinate(newCell.getYCoordinate() + deltaY);
                        newCell.setZCoordinate(newCell.getZCoordinate() + deltaZ);


                        newHistion.addChild(newCell);
                    });
                    newHistion.setZCoordinate(hZ + deltaZ);
                    newHistion.setYCoordinate(hY + deltaY);
                    hm.addHistion(newHistion);

                    if ((xUpperLimit.get() >= 0) && (xLowerLimit.get() >= 0)) {
                        hX = hm.getHistionMap().get(0).getXCoordinate();
                        while (hX < xUpperBoundary) {
                            Histion newHist = new Histion(hm.getHistionMap().get(0));
                            newHist.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                            hm.getHistionMap().get(0).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHist.getId());
                                c.getItems().forEach(p -> {

                                    Part newP = new Part(p.getId(), p);
                                    newP.getPointData().clear();
                                    p.getPointData().forEach(point -> {
                                        TetgenPoint newPoint = new TetgenPoint(point);
                                        newPoint.setX(point.getX() + deltaX + xShift.get());
                                        newPoint.setY(point.getY() + deltaY);
                                        newPoint.setZ(point.getZ() + deltaZ + zShift.get());
                                        newP.getPointData().add(newPoint);
                                    });
                                    newP.setAvgNode();
                                    newCell.addChild(newP);


                                });


                                newCell.setXCoordinate(newCell.getXCoordinate() + deltaX + xShift.get());
                                newCell.setYCoordinate(newCell.getYCoordinate() + deltaY);
                                newCell.setZCoordinate(newCell.getZCoordinate() + deltaZ + zShift.get());


                                newHist.addChild(newCell);
                            });
                            newHist.setXCoordinate(hX + deltaX + xShift.get());
                            newHist.setZCoordinate(hZ + deltaZ + zShift.get());
                            newHist.setYCoordinate(hY + deltaY);
                            hm.addHistion(newHist);
                            hX += deltaX + xShift.get();
                            hZ += zShift.get();
                        }
                        hX = hm.getHistionMap().get(0).getXCoordinate();
                        hZ = hm.getHistionMap().get(0).getZCoordinate() + deltaZ * num;
                        while (hX > -xLowerBoundary) {
                            Histion newHist = new Histion(hm.getHistionMap().get(0));
                            newHist.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                            hm.getHistionMap().get(0).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHist.getId());
                                c.getItems().forEach(p -> {

                                    Part newP = new Part(p.getId(), p);
                                    newP.getPointData().clear();
                                    p.getPointData().forEach(point -> {
                                        TetgenPoint newPoint = new TetgenPoint(point);
                                        newPoint.setX(point.getX() - deltaX - xShift.get());
                                        newPoint.setY(point.getY() + deltaY);
                                        newPoint.setZ(point.getZ() + deltaZ - zShift.get());
                                        newP.getPointData().add(newPoint);
                                    });
                                    newP.setAvgNode();
                                    newCell.addChild(newP);


                                });


                                newCell.setXCoordinate(newCell.getXCoordinate() - deltaX - xShift.get());
                                newCell.setYCoordinate(newCell.getYCoordinate() + deltaY);
                                newCell.setZCoordinate(newCell.getZCoordinate() + deltaZ - zShift.get());


                                newHist.addChild(newCell);
                            });
                            newHist.setXCoordinate(hX - deltaX - xShift.get());
                            newHist.setZCoordinate(hZ + deltaZ - zShift.get());
                            newHist.setYCoordinate(hY + deltaY);
                            hm.addHistion(newHist);
                            hX -= deltaX + xShift.get();
                            hZ -= zShift.get();
                        }
                    }
                    num++;
                    hZ = hm.getHistionMap().get(0).getZCoordinate() + deltaZ * num;
                }
                hZ = hm.getHistionMap().get(0).getZCoordinate();
                num = 0;
                while (hZ > -zLowerBoundary) {
                    Histion newHistion = new Histion(hm.getHistionMap().get(0));
                    newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                    hm.getHistionMap().get(0).getItems().forEach(c -> {
                        Cell newCell = new Cell(c, newHistion.getId());
                        c.getItems().forEach(p -> {

                            Part newP = new Part(p.getId(), p);
                            newP.getPointData().clear();
                            p.getPointData().forEach(point -> {
                                TetgenPoint newPoint = new TetgenPoint(point);
                                newPoint.setY(point.getY() + deltaY);
                                newPoint.setZ(point.getZ() - deltaZ);
                                newP.getPointData().add(newPoint);
                            });
                            newP.setAvgNode();
                            newCell.addChild(newP);


                        });


                        newCell.setYCoordinate(newCell.getYCoordinate() + deltaY);
                        newCell.setZCoordinate(newCell.getZCoordinate() - deltaZ);


                        newHistion.addChild(newCell);
                    });
                    newHistion.setZCoordinate(hZ - deltaZ);
                    newHistion.setYCoordinate(hY + deltaY);
                    hm.addHistion(newHistion);
                    if ((xUpperLimit.get() >= 0) && (xLowerLimit.get() >= 0)) {
                        hX = hm.getHistionMap().get(0).getXCoordinate();
                        while (hX < xUpperBoundary) {
                            Histion newHist = new Histion(hm.getHistionMap().get(0));
                            newHist.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                            hm.getHistionMap().get(0).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHist.getId());
                                c.getItems().forEach(p -> {

                                    Part newP = new Part(p.getId(), p);
                                    newP.getPointData().clear();
                                    p.getPointData().forEach(point -> {
                                        TetgenPoint newPoint = new TetgenPoint(point);
                                        newPoint.setX(point.getX() + deltaX + xShift.get());
                                        newPoint.setY(point.getY() + deltaY);
                                        newPoint.setZ(point.getZ() - deltaZ + zShift.get());
                                        newP.getPointData().add(newPoint);
                                    });
                                    newP.setAvgNode();
                                    newCell.addChild(newP);


                                });


                                newCell.setXCoordinate(newCell.getXCoordinate() + deltaX + xShift.get());
                                newCell.setYCoordinate(newCell.getYCoordinate() + deltaY);
                                newCell.setZCoordinate(newCell.getZCoordinate() - deltaZ + zShift.get());


                                newHist.addChild(newCell);
                            });
                            newHist.setXCoordinate(hX + deltaX + xShift.get());
                            newHist.setZCoordinate(hZ - deltaZ + zShift.get());
                            newHist.setYCoordinate(hY + deltaY);
                            hm.addHistion(newHist);
                            hX += deltaX + xShift.get();
                            hZ += zShift.get();
                        }
                        hX = hm.getHistionMap().get(0).getXCoordinate();
                        hZ = hm.getHistionMap().get(0).getZCoordinate() - deltaZ * num;
                        while (hX > -xLowerBoundary) {
                            Histion newHist = new Histion(hm.getHistionMap().get(0));
                            newHist.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                            hm.getHistionMap().get(0).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHist.getId());
                                c.getItems().forEach(p -> {

                                    Part newP = new Part(p.getId(), p);
                                    newP.getPointData().clear();
                                    p.getPointData().forEach(point -> {
                                        TetgenPoint newPoint = new TetgenPoint(point);
                                        newPoint.setX(point.getX() - deltaX - xShift.get());
                                        newPoint.setY(point.getY() + deltaY);
                                        newPoint.setZ(point.getZ() - deltaZ - zShift.get());
                                        newP.getPointData().add(newPoint);
                                    });
                                    newP.setAvgNode();
                                    newCell.addChild(newP);


                                });


                                newCell.setXCoordinate(newCell.getXCoordinate() - deltaX - xShift.get());
                                newCell.setYCoordinate(newCell.getYCoordinate() + deltaY);
                                newCell.setZCoordinate(newCell.getZCoordinate() - deltaZ - zShift.get());


                                newHist.addChild(newCell);
                            });
                            newHist.setXCoordinate(hX - deltaX - xShift.get());
                            newHist.setZCoordinate(hZ - deltaZ - zShift.get());
                            newHist.setYCoordinate(hY + deltaY);
                            hm.addHistion(newHist);
                            hX -= deltaX + xShift.get();
                            hZ -= zShift.get();
                        }
                    }
                    num++;
                    hZ = hm.getHistionMap().get(0).getZCoordinate() - deltaZ * num;
                }
                hY += deltaY;
            }
            hY = hm.getHistionMap().get(0).getYCoordinate();
            while (hY > -yLowerBoundary) {
                num = 0;
                hZ = hm.getHistionMap().get(0).getZCoordinate();
                while (hZ < zUpperBoundary) {
                    Histion newHistion = new Histion(hm.getHistionMap().get(0));
                    newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                    hm.getHistionMap().get(0).getItems().forEach(c -> {
                        Cell newCell = new Cell(c, newHistion.getId());
                        c.getItems().forEach(p -> {

                            Part newP = new Part(p.getId(), p);
                            newP.getPointData().clear();
                            p.getPointData().forEach(point -> {
                                TetgenPoint newPoint = new TetgenPoint(point);
                                newPoint.setY(point.getY() - deltaY);
                                newPoint.setZ(point.getZ() + deltaZ);
                                newP.getPointData().add(newPoint);
                            });
                            newP.setAvgNode();
                            newCell.addChild(newP);


                        });


                        newCell.setYCoordinate(newCell.getYCoordinate() - deltaY);
                        newCell.setZCoordinate(newCell.getZCoordinate() + deltaZ);


                        newHistion.addChild(newCell);
                    });
                    newHistion.setZCoordinate(hZ + deltaZ);
                    newHistion.setYCoordinate(hY - deltaY);
                    hm.addHistion(newHistion);

                    if ((xUpperLimit.get() >= 0) && (xLowerLimit.get() >= 0)) {
                        hX = hm.getHistionMap().get(0).getXCoordinate();
                        while (hX < xUpperBoundary) {
                            Histion newHist = new Histion(hm.getHistionMap().get(0));
                            newHist.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                            hm.getHistionMap().get(0).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHist.getId());
                                c.getItems().forEach(p -> {

                                    Part newP = new Part(p.getId(), p);
                                    newP.getPointData().clear();
                                    p.getPointData().forEach(point -> {
                                        TetgenPoint newPoint = new TetgenPoint(point);
                                        newPoint.setX(point.getX() + deltaX + xShift.get());
                                        newPoint.setY(point.getY() - deltaY);
                                        newPoint.setZ(point.getZ() + deltaZ + zShift.get());
                                        newP.getPointData().add(newPoint);
                                    });
                                    newP.setAvgNode();
                                    newCell.addChild(newP);


                                });


                                newCell.setXCoordinate(newCell.getXCoordinate() + deltaX + xShift.get());
                                newCell.setYCoordinate(newCell.getYCoordinate() - deltaY);
                                newCell.setZCoordinate(newCell.getZCoordinate() + deltaZ + zShift.get());


                                newHist.addChild(newCell);
                            });
                            newHist.setXCoordinate(hX + deltaX + xShift.get());
                            newHist.setZCoordinate(hZ + deltaZ + zShift.get());
                            newHist.setYCoordinate(hY - deltaY);
                            hm.addHistion(newHist);
                            hX += deltaX + xShift.get();
                            hZ += zShift.get();
                        }
                        hX = hm.getHistionMap().get(0).getXCoordinate();
                        hZ = hm.getHistionMap().get(0).getZCoordinate() + deltaZ * num;
                        while (hX > -xLowerBoundary) {
                            Histion newHist = new Histion(hm.getHistionMap().get(0));
                            newHist.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                            hm.getHistionMap().get(0).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHist.getId());
                                c.getItems().forEach(p -> {

                                    Part newP = new Part(p.getId(), p);
                                    newP.getPointData().clear();
                                    p.getPointData().forEach(point -> {
                                        TetgenPoint newPoint = new TetgenPoint(point);
                                        newPoint.setX(point.getX() - deltaX - xShift.get());
                                        newPoint.setY(point.getY() - deltaY);
                                        newPoint.setZ(point.getZ() + deltaZ - zShift.get());
                                        newP.getPointData().add(newPoint);
                                    });
                                    newP.setAvgNode();
                                    newCell.addChild(newP);


                                });


                                newCell.setXCoordinate(newCell.getXCoordinate() - deltaX - xShift.get());
                                newCell.setYCoordinate(newCell.getYCoordinate() - deltaY);
                                newCell.setZCoordinate(newCell.getZCoordinate() + deltaZ - zShift.get());


                                newHist.addChild(newCell);
                            });
                            newHist.setXCoordinate(hX - deltaX - xShift.get());
                            newHist.setZCoordinate(hZ + deltaZ - zShift.get());
                            newHist.setYCoordinate(hY - deltaY);
                            hm.addHistion(newHist);
                            hX -= deltaX + xShift.get();
                            hZ -= zShift.get();
                        }
                    }

                    num++;
                    hZ = hm.getHistionMap().get(0).getZCoordinate() + deltaZ * num;

                }
                hZ = hm.getHistionMap().get(0).getZCoordinate();
                num = 0;
                while (hZ > -zLowerBoundary) {
                    Histion newHistion = new Histion(hm.getHistionMap().get(0));
                    newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                    hm.getHistionMap().get(0).getItems().forEach(c -> {
                        Cell newCell = new Cell(c, newHistion.getId());
                        c.getItems().forEach(p -> {

                            Part newP = new Part(p.getId(), p);
                            newP.getPointData().clear();
                            p.getPointData().forEach(point -> {
                                TetgenPoint newPoint = new TetgenPoint(point);
                                newPoint.setY(point.getY() - deltaY);
                                newPoint.setZ(point.getZ() - deltaZ);
                                newP.getPointData().add(newPoint);
                            });
                            newP.setAvgNode();
                            newCell.addChild(newP);


                        });


                        newCell.setYCoordinate(newCell.getYCoordinate() - deltaY);
                        newCell.setZCoordinate(newCell.getZCoordinate() - deltaZ);


                        newHistion.addChild(newCell);
                    });
                    newHistion.setZCoordinate(hZ - deltaZ);
                    newHistion.setYCoordinate(hY - deltaY);
                    hm.addHistion(newHistion);

                    if ((xUpperLimit.get() >= 0) && (xLowerLimit.get() >= 0)) {
                        hX = hm.getHistionMap().get(0).getXCoordinate();
                        while (hX < xUpperBoundary) {
                            Histion newHist = new Histion(hm.getHistionMap().get(0));
                            newHist.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                            hm.getHistionMap().get(0).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHist.getId());
                                c.getItems().forEach(p -> {

                                    Part newP = new Part(p.getId(), p);
                                    newP.getPointData().clear();
                                    p.getPointData().forEach(point -> {
                                        TetgenPoint newPoint = new TetgenPoint(point);
                                        newPoint.setX(point.getX() + deltaX + xShift.get());
                                        newPoint.setY(point.getY() - deltaY);
                                        newPoint.setZ(point.getZ() - deltaZ + zShift.get());
                                        newP.getPointData().add(newPoint);
                                    });
                                    newP.setAvgNode();
                                    newCell.addChild(newP);


                                });


                                newCell.setXCoordinate(newCell.getXCoordinate() + deltaX + xShift.get());
                                newCell.setYCoordinate(newCell.getYCoordinate() - deltaY);
                                newCell.setZCoordinate(newCell.getZCoordinate() - deltaZ + zShift.get());

                                newHist.addChild(newCell);
                            });
                            newHist.setXCoordinate(hX + deltaX + xShift.get());
                            newHist.setZCoordinate(hZ - deltaZ + zShift.get());
                            newHist.setYCoordinate(hY - deltaY);
                            hm.addHistion(newHist);
                            hX += deltaX + xShift.get();
                            hZ += zShift.get();
                        }
                        hX = hm.getHistionMap().get(0).getXCoordinate();
                        hZ = hm.getHistionMap().get(0).getZCoordinate() - deltaZ * num;
                        while (hX > -xLowerBoundary) {
                            Histion newHist = new Histion(hm.getHistionMap().get(0));
                            newHist.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                            hm.getHistionMap().get(0).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHist.getId());
                                c.getItems().forEach(p -> {

                                    Part newP = new Part(p.getId(), p);
                                    newP.getPointData().clear();
                                    p.getPointData().forEach(point -> {
                                        TetgenPoint newPoint = new TetgenPoint(point);
                                        newPoint.setX(point.getX() - deltaX - xShift.get());
                                        newPoint.setY(point.getY() - deltaY);
                                        newPoint.setZ(point.getZ() - deltaZ - zShift.get());
                                        newP.getPointData().add(newPoint);
                                    });
                                    newP.setAvgNode();
                                    newCell.addChild(newP);


                                });


                                newCell.setXCoordinate(newCell.getXCoordinate() - deltaX - xShift.get());
                                newCell.setYCoordinate(newCell.getYCoordinate() - deltaY);
                                newCell.setZCoordinate(newCell.getZCoordinate() - deltaZ - zShift.get());


                                newHist.addChild(newCell);
                            });
                            newHist.setXCoordinate(hX - deltaX - xShift.get());
                            newHist.setZCoordinate(hZ - deltaZ - zShift.get());
                            newHist.setYCoordinate(hY - deltaY);
                            hm.addHistion(newHist);
                            hX -= deltaX + xShift.get();
                            hZ -= zShift.get();
                        }
                    }

                    num++;
                    hZ = hm.getHistionMap().get(0).getZCoordinate() - deltaZ * num;
                }
                hY -= deltaY;
            }
            hX = hm.getHistionMap().get(0).getXCoordinate();
            hZ = hm.getHistionMap().get(0).getZCoordinate();
        }

        if (((xUpperLimit.get() >= 0) && (xLowerLimit.get() >= 0))
                && ((yUpperLimit.get() >= 0) && (yLowerLimit.get() >= 0))) {
            while (hX < xUpperBoundary) {
                hY = hm.getHistionMap().get(0).getYCoordinate();
                while (hY < yUpperBoundary) {
                    Histion newHistion = new Histion(hm.getHistionMap().get(0));
                    newHistion.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                    hm.getHistionMap().get(0).getItems().forEach(c -> {
                        Cell newCell = new Cell(c, newHistion.getId());
                        c.getItems().forEach(p -> {

                            Part newP = new Part(p.getId(), p);
                            newP.getPointData().clear();
                            p.getPointData().forEach(point -> {
                                TetgenPoint newPoint = new TetgenPoint(point);
                                newPoint.setX(point.getX() + deltaX + xShift.get());
                                newPoint.setY(point.getY() + deltaY);
                                newPoint.setZ(point.getZ() + zShift.get());
                                newP.getPointData().add(newPoint);
                            });
                            newP.setAvgNode();
                            newCell.addChild(newP);


                        });


                        newCell.setXCoordinate(newCell.getXCoordinate() + deltaX + xShift.get());
                        newCell.setYCoordinate(newCell.getYCoordinate() + deltaY);
                        newCell.setZCoordinate(newCell.getZCoordinate() + zShift.get());


                        newHistion.addChild(newCell);
                    });
                    newHistion.setYCoordinate(hY + deltaY);
                    newHistion.setXCoordinate(hX + deltaX + xShift.get());
                    newHistion.setZCoordinate(hZ + zShift.get());
                    hm.addHistion(newHistion);
                    hY += deltaY;
                }
                hY = hm.getHistionMap().get(0).getYCoordinate();
                while (hY > -yLowerBoundary) {
                    Histion newHistion = new Histion(hm.getHistionMap().get(0));
                    newHistion.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                    hm.getHistionMap().get(0).getItems().forEach(c -> {
                        Cell newCell = new Cell(c, newHistion.getId());
                        c.getItems().forEach(p -> {

                            Part newP = new Part(p.getId(), p);
                            newP.getPointData().clear();
                            p.getPointData().forEach(point -> {
                                TetgenPoint newPoint = new TetgenPoint(point);
                                newPoint.setX(point.getX() + deltaX + xShift.get());
                                newPoint.setY(point.getY() - deltaY);
                                newPoint.setZ(point.getZ() + zShift.get());
                                newP.getPointData().add(newPoint);
                            });
                            newP.setAvgNode();
                            newCell.addChild(newP);


                        });


                        newCell.setXCoordinate(newCell.getXCoordinate() + deltaX + xShift.get());
                        newCell.setYCoordinate(newCell.getYCoordinate() - deltaY);
                        newCell.setZCoordinate(newCell.getZCoordinate() + zShift.get());


                        newHistion.addChild(newCell);
                    });
                    newHistion.setYCoordinate(hY - deltaY);
                    newHistion.setXCoordinate(hX + deltaX + xShift.get());
                    newHistion.setZCoordinate(hZ + zShift.get());
                    hm.addHistion(newHistion);
                    hY -= deltaY;
                }
                hX += deltaX + xShift.get();
                hZ += zShift.get();
            }
            hX = hm.getHistionMap().get(0).getXCoordinate();
            hZ = hm.getHistionMap().get(0).getXCoordinate();
            while (hX > -xLowerBoundary) {

                hY = hm.getHistionMap().get(0).getYCoordinate();
                while (hY < yUpperBoundary) {
                    Histion newHistion = new Histion(hm.getHistionMap().get(0));
                    newHistion.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                    hm.getHistionMap().get(0).getItems().forEach(c -> {
                        Cell newCell = new Cell(c, newHistion.getId());
                        c.getItems().forEach(p -> {

                            Part newP = new Part(p.getId(), p);
                            newP.getPointData().clear();
                            p.getPointData().forEach(point -> {
                                TetgenPoint newPoint = new TetgenPoint(point);
                                newPoint.setX(point.getX() - deltaX - xShift.get());
                                newPoint.setY(point.getY() + deltaY);
                                newPoint.setZ(point.getZ() - zShift.get());
                                newP.getPointData().add(newPoint);
                            });
                            newP.setAvgNode();
                            newCell.addChild(newP);


                        });


                        newCell.setXCoordinate(newCell.getXCoordinate() - deltaX - xShift.get());
                        newCell.setYCoordinate(newCell.getYCoordinate() + deltaY);
                        newCell.setZCoordinate(newCell.getZCoordinate() - zShift.get());


                        newHistion.addChild(newCell);
                    });
                    newHistion.setYCoordinate(hY + deltaY);
                    newHistion.setXCoordinate(hX - deltaX - xShift.get());
                    newHistion.setZCoordinate(hZ - zShift.get());
                    hm.addHistion(newHistion);
                    hY += deltaY;
                }
                hY = hm.getHistionMap().get(0).getYCoordinate();
                while (hY > -yLowerBoundary) {
                    Histion newHistion = new Histion(hm.getHistionMap().get(0));
                    newHistion.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                    hm.getHistionMap().get(0).getItems().forEach(c -> {
                        Cell newCell = new Cell(c, newHistion.getId());
                        c.getItems().forEach(p -> {

                            Part newP = new Part(p.getId(), p);
                            newP.getPointData().clear();
                            p.getPointData().forEach(point -> {
                                TetgenPoint newPoint = new TetgenPoint(point);
                                newPoint.setX(point.getX() - deltaX - xShift.get());
                                newPoint.setY(point.getY() - deltaY);
                                newPoint.setZ(point.getZ() - zShift.get());
                                newP.getPointData().add(newPoint);
                            });
                            newP.setAvgNode();
                            newCell.addChild(newP);


                        });


                        newCell.setXCoordinate(newCell.getXCoordinate() - deltaX - xShift.get());
                        newCell.setYCoordinate(newCell.getYCoordinate() - deltaY);
                        newCell.setZCoordinate(newCell.getZCoordinate() - zShift.get());


                        newHistion.addChild(newCell);
                    });
                    newHistion.setYCoordinate(hY - deltaY);
                    newHistion.setXCoordinate(hX - deltaX - xShift.get());
                    newHistion.setZCoordinate(hZ - zShift.get());
                    hm.addHistion(newHistion);
                    hY -= deltaY;
                }
                hX -= deltaX + xShift.get();
                hZ -= zShift.get();
            }
        }
        disableEverything.set(true);
        pastePartDisabledProperty.set(true);
        pasteCellDisabledProperty.set(true);
    }
}