package org.spbu.histology.cross.section.viewer.util;

public class Vertex {

    private double x;
    private double y;
    private String id;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getId() {
        return id;
    }

    public Vertex(double x, double y, String id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }
}
