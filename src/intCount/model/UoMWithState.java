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
public final class UoMWithState extends MeasurementUnit {
    private final ReadOnlyObjectWrapper<UpdateState> updateState =
            new ReadOnlyObjectWrapper<>(this, "updateState");

    public UoMWithState() {
        super();
    }

    public UoMWithState(MeasurementUnit unit) {
        super(unit);
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

    public static List<UoMWithState> fromUnits(List<MeasurementUnit> units) {
        ArrayList<UoMWithState> list = new ArrayList<>(units.size());

        for (MeasurementUnit unit : units) {
            list.add(new UoMWithState(unit));
        }

        return list;
    }
}
