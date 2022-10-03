/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * @author
 */
public class ItemWithState extends Item {

    private final ReadOnlyObjectWrapper<UpdateState> updateState = new ReadOnlyObjectWrapper<>(this, "updateState");

    public ItemWithState() {
        super();
    }

    public ItemWithState(Item item) {
        super(item);
    }

    public UpdateState getUpdateState() {
        return updateState.get();
    }

    public void setUpdateState(UpdateState value) {
        updateState.set(value);
    }

    public ReadOnlyObjectProperty<UpdateState> updateStateProperty() {
        return updateState.getReadOnlyProperty();
    }

    public static List<ItemWithState> fromItems(List<Item> items) {
        ArrayList<ItemWithState> list = new ArrayList<>(items.size());

        items.stream().forEach((item) -> {
            list.add(new ItemWithState(item));
        });

        return list;
    }
}
