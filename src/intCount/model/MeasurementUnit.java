/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.model;


import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

/**
 * @author
 */
public class MeasurementUnit {
    private final ReadOnlyIntegerWrapper unitId;
    private final ReadOnlyStringWrapper unitName;
    private final ReadOnlyStringWrapper abbreviation;

    public MeasurementUnit() {

        unitId = new ReadOnlyIntegerWrapper(this, "unitId", 0);
        unitName = new ReadOnlyStringWrapper(this, "unitName", "");
        abbreviation = new ReadOnlyStringWrapper(this, "abbreviation", "");
    }

    public MeasurementUnit(MeasurementUnit arg) {
        this();
        unitId.set(arg.getUnitId());
        unitName.set(arg.getUnitName());
        abbreviation.set(arg.getAbbreviation());
    }

    public Integer getUnitId() {
        return unitId.get();
    }

    public void setUnitId(int id) {
        unitId.set(id);
    }

    public String getUnitName() {
        return unitName.getValueSafe();
    }

    public void setUnitName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'unité doit être une valeur non vide.");
        }
        unitName.set(name);
    }

    public ReadOnlyStringProperty unitNameProperty() {
        return unitName.getReadOnlyProperty();
    }

    public String getAbbreviation() {
        return abbreviation.getValueSafe();
    }

    public void setAbbreviation(String name) {
        abbreviation.set(name);
    }

    public ReadOnlyStringProperty abbreviationProperty() {
        return abbreviation.getReadOnlyProperty();
    }

    @Override
    public int hashCode() {

        int id = this.getUnitId();
        if (id != 0) {
            return id;
        }

        if (!unitName.get().isEmpty()) {
            return unitName.get().toLowerCase().hashCode();
        }
        return super.hashCode(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof MeasurementUnit)) {
            return false;
        }

        MeasurementUnit unit = (MeasurementUnit) obj;

        if (this.getUnitId() != 0 && unit.getUnitId() != 0) {
            return (this.getUnitId().equals(unit.getUnitId()));
        }

        if (this.getUnitName().isEmpty() || unit.getUnitName().isEmpty()) {
            return false;
        }

        return this.unitName.get().equalsIgnoreCase(unit.getUnitName());
    }

    @Override
    public String toString() {

        if (getUnitName().isEmpty()) {
            return super.toString();
        } else {
            return getUnitName();
        }
    }


}
