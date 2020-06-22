package org.spbu.histology.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.*;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;

public abstract class HistologyObject<T extends HistologyObject<?>> {

    public HistologyObject(Integer id, String name) {
        setName(name);
        this.id = id;
    }

    private final StringProperty name = new SimpleStringProperty();

    public final StringProperty nameProperty() {
        return this.name;
    }

    public final String getName() {
        return this.nameProperty().get();
    }

    public final void setName(final String name) {
        this.nameProperty().set(name);
    }

    private Integer id;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private final ObservableMap<Integer, T> itemMap
            = FXCollections.observableMap(new ConcurrentHashMap());

    public ObservableMap<Integer, T> getItemMap() {
        return itemMap;
    }

    public ObservableList<T> getItems() {
        ObservableList<T> copyList = FXCollections.observableArrayList();
        copyList.addAll(itemMap.values());
        return copyList.sorted(Comparator.comparingInt(i -> i.getId()));
    }

    public abstract void addChild(T obj);

    public abstract void deleteChild(Integer id);
}
