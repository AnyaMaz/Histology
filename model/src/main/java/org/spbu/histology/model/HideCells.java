package org.spbu.histology.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class HideCells {
    
    private static ObservableList<Integer> cellIdToHideList = FXCollections.observableArrayList();

    private static ObservableList<Integer> cellIdToShowInOneViewerList = FXCollections.observableArrayList();

    public static ObservableList<Integer> getCellIdToHideList() {
        return cellIdToHideList;
    }

    public static ObservableList<Integer> getCellIdToShowInOneViewerList(){ return cellIdToShowInOneViewerList;}

    public static void addCellIdToHide(Integer id) {
        cellIdToHideList.add(id);
    }

    public static void addCellIdToShowInOneViewer(Integer id) {
        cellIdToShowInOneViewerList.add(id);
    }

    public static void removeCellIdToHide(Integer id) {
        if (cellIdToHideList.contains(id)) {
            cellIdToHideList.remove(id);
        }
    }

    public static void removeCellIdToShowInOneViewer(Integer id) {
        if (cellIdToShowInOneViewerList.contains(id)) {
            cellIdToShowInOneViewerList.remove(id);
        }
    }

}
