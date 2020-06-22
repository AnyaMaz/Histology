/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spbu.histology.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.openide.util.Lookup;

import java.io.*;
import java.util.ArrayList;

/**
 * @author gleb3
 */
public class RestoreModel {
    private static HistionManager histionManager = Lookup.getDefault().lookup(HistionManager.class);

    public static Cell histionFromFile(File file, boolean isReplace) {
        if (isReplace) {
            clearScene();
        }
        return loadModelFromFile(file.getAbsolutePath(), true);
    }

    public static Cell cellFromFile(File file) {
        clearScene();
        return loadModelFromFile(file.getAbsolutePath(), false);
    }

    public static void autoLoadCell(String path) {
        clearScene();
        loadModelFromFile(path, false);
    }

    public static Cell loadModelFromFile(String pathToModel, boolean isHistion) {
        if (histionManager == null) {
            System.out.println("HISTION MANAGER IS NULL");
            histionManager = Lookup.getDefault().lookup(HistionManager.class);
        }

        Cell c = null;
        try {
            Histion main = histionManager.getHistionMap().get(0);
            BufferedReader br = new BufferedReader(new FileReader(pathToModel));
            String line = br.readLine();

            line = line.substring(line.indexOf(" ") + 1);
            line = line.substring(line.indexOf(" ") + 1);
            main.setXCoordinate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
            line = line.substring(line.indexOf(" ") + 1);
            main.setYCoordinate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
            line = line.substring(line.indexOf(" ") + 1);
            main.setZCoordinate(Double.parseDouble(line));

            if (isHistion) {
                line = br.readLine();
                RecurrenceShifts.setXShift(Double.parseDouble(line));
                line = br.readLine();
                RecurrenceShifts.setZShift(Double.parseDouble(line));
            }

            line = br.readLine();
            int cellNum = Integer.parseInt(line);
            for (int i = 0; i < cellNum; i++) {
                ObservableList<ArrayList<Integer>> facetData = FXCollections.observableArrayList();
                c = new Cell("Name", 0, 0, 0, 0, 0, FXCollections.observableArrayList(),
                        Color.BLUE, Color.LIGHTBLUE, 0, true);
                double r, g, b;
                line = br.readLine();

                String name = line;
                name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                int count = 1;
                while (Names.containsCellName(name)) {
                    name = line;
                    name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                    name += "(" + count + ")";
                    count++;
                }
                c.setName("Клетка <" + name + ">");

                line = br.readLine();
                c.setXRotate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                line = line.substring(line.indexOf(" ") + 1);
                c.setYRotate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                line = line.substring(line.indexOf(" ") + 1);
                c.setXCoordinate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                line = line.substring(line.indexOf(" ") + 1);
                c.setYCoordinate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                line = line.substring(line.indexOf(" ") + 1);
                c.setZCoordinate(Double.parseDouble(line));

                line = br.readLine();
                r = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                line = line.substring(line.indexOf(" ") + 1);
                g = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                line = line.substring(line.indexOf(" ") + 1);
                b = Double.parseDouble(line);
                c.setDiffuseColor(Color.color(r, g, b));

                line = br.readLine();
                r = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                line = line.substring(line.indexOf(" ") + 1);
                g = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                line = line.substring(line.indexOf(" ") + 1);
                b = Double.parseDouble(line);
                c.setSpecularColor(Color.color(r, g, b));

                line = br.readLine();
                c.setShow(Boolean.parseBoolean(line));

                line = br.readLine();
                int partNum = Integer.parseInt(line);

                ArrayList<TetgenPoint> pd = new ArrayList<>();
                int num = 1;
                for (int j = 0; j < partNum; j++) {
                    ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
                    Part p = new Part("Part", FXCollections.observableArrayList(), c.getId());
                    line = br.readLine();
                    name = line;
                    name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                    p.setName("Слой <" + name + ">");

                    System.out.println(p.getName());

                    line = br.readLine();
                    int pointNum = Integer.parseInt(line);
                    for (int q = 0; q < pointNum; q++) {
                        line = br.readLine();
                        r = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                        line = line.substring(line.indexOf(" ") + 1);
                        g = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                        line = line.substring(line.indexOf(" ") + 1);
                        b = Double.parseDouble(line);
                        pointData.add(new TetgenPoint(q + 1, r, g, b));
                        pd.add(new TetgenPoint(num, r, g, b));
                        num++;
                    }
                    p.setPointData(pointData);
                    p.setAvgNode();
                    c.addChild(p);
                }
                line = br.readLine();
                int facetNum = Integer.parseInt(line);
                for (int j = 0; j < facetNum; j++) {
                    ArrayList<Integer> list = new ArrayList<>();
                    line = br.readLine();
                    while (line.contains(" ")) {
                        list.add(Integer.parseInt(line.substring(0, line.indexOf(" "))));
                        line = line.substring(line.indexOf(" ") + 1);
                    }
                    list.add(Integer.parseInt(line));
                    facetData.add(list);
                }
                c.setFacetData(facetData);

                ArrayList<TwoIntegers> lineList = new ArrayList<>();
                for (ArrayList<Integer> f : facetData) {
                    for (int j = 1; j < f.size(); j++) {
                        TwoIntegers ti = new TwoIntegers(j, f.get(j - 1), f.get(j));
                        if (!lineList.contains(ti)) {
                            lineList.add(ti);
                        }
                    }
                    TwoIntegers ti = new TwoIntegers(f.size(), f.get(f.size() - 1), f.get(0));
                    if (!lineList.contains(ti)) {
                        lineList.add(ti);
                    }
                }

                ArrayList<Line> lines = new ArrayList<>();
                for (int j = 0; j < lineList.size(); j++) {

                    TetgenPoint point1 = pd.get(lineList.get(j).getPoint1() - 1);
                    TetgenPoint point2 = pd.get(lineList.get(j).getPoint2() - 1);
                    if (Math.abs(point1.getY() - point2.getY()) < 0.0001) {
                        lines.add(new Line(new Node(point1.getX(), point1.getZ(), point1.getY()),
                                new Node(point2.getX(), point2.getZ(), point2.getY())));
                    }
                }
                LineEquations.addLine(c.getId(), lines);

                main.addChild(c);
                name = c.getName();
                Names.addCellName(name.substring(name.indexOf("<") + 1, name.lastIndexOf(">")));
            }
            br.close();
            return c;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
//            ex.printStackTrace();
            throw new RuntimeException(ex.getCause());
        }
    }

    private static void clearScene() {
        System.out.println("SCENE WAS CLEARED");
        histionManager.getHistionMap().get(0).getItems().forEach(c -> {
            String name = c.getName();
            Names.removeCellName(name.substring(name.indexOf("<") + 1, name.lastIndexOf(">")));
            histionManager.getHistionMap().get(0).deleteChild(c.getId());
        });
    }
}
