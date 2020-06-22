package org.spbu.histology.space.editor.WIP;

import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class FlatRepresentationOfCell extends Circle {

    private long id;
    private List<Long> idOfConnectedCells;
    private CellType type;

    public void setType(CellType type) {
        this.type = type;
    }

    public List<Long> getIdOfConnectedCells() {
        return idOfConnectedCells;
    }

    public CellType getType() {
        return type;
    }

    public FlatRepresentationOfCell(long id, double centerX, double centerY, double radius) {
        super(centerX, centerY, radius);
        this.id = id;
        idOfConnectedCells = new ArrayList<>();
    }

    public void addConnection(long id) {
        if (idOfConnectedCells.indexOf(id) == -1) {
            idOfConnectedCells.add(id);
        }
    }
}
