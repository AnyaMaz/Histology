package org.spbu.histology.space.editor;

public class Vertex {

    private double x;
    private double y;
    private String polygonId;

    public String getPolygonId() {
        return polygonId;
    }

    public Vertex(double x, double y, String polygonId) {
        this.x = x;
        this.y = y;
        this.polygonId = polygonId;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return (vertex.getX() == x && vertex.getY() == y);
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "x=" + x +
                ", y=" + y +
                ", polygonId='" + polygonId + '\'' +
                '}';
    }
}
