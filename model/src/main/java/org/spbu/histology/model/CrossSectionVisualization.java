package org.spbu.histology.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CrossSectionVisualization {

    private static final ObservableMap<Integer, ArrayList<Polygon>> polygonMap
            = FXCollections.observableMap(new ConcurrentHashMap());

    private static final ObservableMap<Integer, Color> polygonColorMap
            = FXCollections.observableMap(new ConcurrentHashMap());

    private static final ObservableMap<Integer, ArrayList<Line>> lineMap
            = FXCollections.observableMap(new ConcurrentHashMap());


    private static Map<Polygon, Integer> vertexToClickCount = new HashMap();

    public static Map<Polygon, Integer> getVertexToClickCount() {
        return vertexToClickCount;
    }

    public static void addVertex(Polygon p, int clickCount) {
        vertexToClickCount.put(p, clickCount);
    }

    private static int cellId;

    public static ObservableMap<Integer, ArrayList<Polygon>> getPolygonMap() {
        return polygonMap;
    }

    public static void addPolygon(Integer id, ArrayList<Polygon> p) {
        polygonMap.put(id, p);
    }

    public static void removePolygon(Integer id) {
        polygonMap.remove(id);
    }

    public static ObservableMap<Integer, Color> getPolygonColorMap() {
        return polygonColorMap;
    }

    public static void addPolygonColor(Integer id, Color pc) {
        polygonColorMap.put(id, pc);
    }

    public static void removePolygonColor(Integer id) {
        polygonColorMap.remove(id);
    }

    public static ObservableMap<Integer, ArrayList<Line>> getLineMap() {
        return lineMap;
    }

    public static void addLine(Integer id, ArrayList<Line> l) {
        lineMap.put(id, l);
    }

    public static void removeLine(Integer id) {
        lineMap.remove(id);
    }

}
