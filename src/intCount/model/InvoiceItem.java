/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.model;

import java.math.BigDecimal;

import javafx.beans.property.*;

/**
 * @author
 */
public class InvoiceItem {
    private ObjectProperty<Item> item;
    private ObjectProperty<MeasurementUnit> unit;
    private ObjectProperty<BigDecimal> rate;
    private ObjectProperty<BigDecimal> quantity;
    private ObjectProperty<BigDecimal> tva;

    public BigDecimal getTva() {
        return tva.get();
    }

    public void setTva(BigDecimal value) {
        tva.set(value);
    }

    public ObjectProperty<BigDecimal> tvaProperty() {
        return tva;
    }

    public InvoiceItem() {
        item = new SimpleObjectProperty<Item>(null);
        unit = new SimpleObjectProperty<>(null);
        rate = new SimpleObjectProperty<>(null);
        quantity = new SimpleObjectProperty<>(null);
        tva = new SimpleObjectProperty<>(null);
    }

    public Item getItem() {
        return item.get();
    }

    public void setItem(Item value) {
        item.set(value);
    }

    public ObjectProperty<Item> itemProperty() {
        return item;
    }

    public MeasurementUnit getUnit() {
        return unit.get();
    }

    public void setUnit(MeasurementUnit value) {
        unit.set(value);
    }

    public ObjectProperty<MeasurementUnit> unitProperty() {
        return unit;
    }

    public BigDecimal getRate() {
        return rate.get();
    }

    public void setRate(BigDecimal value) {
        rate.set(value);
    }

    public ObjectProperty<BigDecimal> rateProperty() {
        return rate;
    }

    public BigDecimal getQuantity() {
        return quantity.get();
    }

    public void setQuantity(BigDecimal value) {
        quantity.set(value);
    }

    public ObjectProperty<BigDecimal> quantityProperty() {
        return quantity;
    }

    @Override
    public String toString() {
        return item.get().getItemName();
    }

    @Override
    public int hashCode() {
        return item.get().hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof InvoiceItem)) {
            return false;
        }

        InvoiceItem arg = (InvoiceItem) obj;
        return item.get().equals(arg.getItem());
    }

}
