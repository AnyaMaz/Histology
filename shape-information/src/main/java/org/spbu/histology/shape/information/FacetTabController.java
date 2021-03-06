package org.spbu.histology.shape.information;

import org.spbu.histology.model.EditCell;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.spbu.histology.fxyz.Line3D;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.model.TwoIntegers;

public class FacetTabController implements Initializable {
    
    @FXML
    private VBox facetVBox;
    
    @FXML
    private TableView<TwoIntegers> table;
    
    @FXML
    private TableColumn < TwoIntegers, Integer > p1Column;
    
    @FXML
    private TableColumn < TwoIntegers, Integer > p2Column;
    
    private ObservableList<TwoIntegers> data;
    
    private HistionManager hm = null;
    
    BooleanProperty change = new SimpleBooleanProperty(false);
    
    Group root;
    private ArrayList<Line3D> lineList = new ArrayList<>();
    
    double width, height;
    IntegerProperty count;
    private ObservableList<TwoIntegers> lineData = FXCollections.observableArrayList();
    
    public void setCount(IntegerProperty count) {
        this.count = count;
    }
    
    public void setRoot(Group root) {
        this.root = root;
    }
    
    public void setPaneSize(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    public void setLineList(ArrayList<Line3D> lineList) {//,
        this.lineList = lineList;
    }
    
    public void setLineData(ObservableList<TwoIntegers> lineData) {
        this.lineData = lineData;
    }
    
    public void setTableHeight(double height) {
        table.setPrefHeight(height);
    }
    
    public void setData(ObservableList<TwoIntegers> data) {
        this.data = data;
        table.setItems(data);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }
        
        facetVBox.setSpacing(10);
        facetVBox.setPadding(new Insets(10, 10, 10, 10));
        setTableEditable();
        table.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                table.getSelectionModel().clearSelection();
            }
        });
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (oldSelection != null) {
                lineList.get(oldSelection.getId() - 1).setColor(Color.BLACK);
            }
            
            if (newSelection != null) {
                lineList.get(newSelection.getId() - 1).setColor(Color.RED);
            }
        });
        MenuItem deletePoint = new MenuItem("Удалить ребро");
        deletePoint.setOnAction((ActionEvent event) -> {
            TwoIntegers item = table.getSelectionModel().getSelectedItem();
            data.remove(item.getId() - 1);
            root.getChildren().remove(lineList.get(item.getId() - 1).getMeshView());
            lineList.remove(item.getId() - 1);
            lineData.remove(item.getId() - 1);
            count.set(count.get() - 1);
            for (int i = 0; i < data.size(); i++)
                data.get(i).setId(i + 1);
            table.getSelectionModel().clearSelection();
        });

        ContextMenu menu = new ContextMenu();
        menu.getItems().add(deletePoint);
        table.setContextMenu(menu);
        table.setStyle("-fx-focus-color: transparent;\n" +
            "    -fx-faint-focus-color: transparent;");
    }
    
    public void addRecord(TwoIntegers p) {
        data.add(p);
    }
    
    private void setTableEditable() {
        table.setEditable(true);
        table.getSelectionModel().cellSelectionEnabledProperty().set(true);
        table.setOnKeyPressed(event -> {
            if (event.getCode().isDigitKey()) {
                editFocusedCell();
            } else if (event.getCode() == KeyCode.RIGHT ||
                event.getCode() == KeyCode.TAB) {
                table.getSelectionModel().selectNext();
                event.consume();
            } else if (event.getCode() == KeyCode.LEFT) {
                selectPrevious();
                event.consume();
            }
        });
    }
    
    private void setupP1Column() {
        p1Column.setCellFactory(
            EditCell. <TwoIntegers, Integer > forTableColumn(
                new MyIntegerStringConverter()));
        p1Column.setOnEditCommit(event -> {
            TwoIntegers item = table.getSelectionModel().getSelectedItem();
            change.set(true);
            final Integer value = event.getNewValue() != null ?
            event.getNewValue() : event.getOldValue();
            ((TwoIntegers) event.getTableView().getItems()
                .get(event.getTablePosition().getRow())).setPoint1(value);
            table.refresh();
        });
    }
    
    private void setupP2Column() {
        p2Column.setCellFactory(
            EditCell. <TwoIntegers, Integer > forTableColumn(
                new MyIntegerStringConverter()));
        p2Column.setOnEditCommit(event -> {
            TwoIntegers item = table.getSelectionModel().getSelectedItem();
            change.set(true);
            final Integer value = event.getNewValue() != null ?
            event.getNewValue() : event.getOldValue();
            ((TwoIntegers) event.getTableView().getItems()
                .get(event.getTablePosition().getRow())).setPoint2(value);
            table.refresh();
        });
    }
    
    private void editFocusedCell() {
        final TablePosition <TwoIntegers, ? > focusedCell = table
            .focusModelProperty().get().focusedCellProperty().get();
        table.edit(focusedCell.getRow(), focusedCell.getTableColumn());
    }
    
    private void selectPrevious() {
        if (table.getSelectionModel().isCellSelectionEnabled()) {
            TablePosition <TwoIntegers, ? > pos = table.getFocusModel()
                .getFocusedCell();
            if (pos.getColumn() - 1 >= 0) {
                table.getSelectionModel().select(pos.getRow(),
                    getTableColumn(pos.getTableColumn(), -1));
            } else if (pos.getRow() < table.getItems().size()) {
                table.getSelectionModel().select(pos.getRow() - 1,
                    table.getVisibleLeafColumn(
                        table.getVisibleLeafColumns().size() - 1));
            }
        } else {
            int focusIndex = table.getFocusModel().getFocusedIndex();
            if (focusIndex == -1) {
                table.getSelectionModel().select(table.getItems().size() - 1);
            } else if (focusIndex > 0) {
                table.getSelectionModel().select(focusIndex - 1);
            }
        }
    }
    
    private TableColumn <TwoIntegers, ? > getTableColumn(
        final TableColumn <TwoIntegers, ? > column, int offset) {
        int columnIndex = table.getVisibleLeafIndex(column);
        int newColumnIndex = columnIndex + offset;
        return table.getVisibleLeafColumn(newColumnIndex);
    }
    
}
