/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * @author
 */
public class Item {
    private int itemId;
    private final ReadOnlyObjectWrapper<String> itemName;
    private final ReadOnlyObjectWrapper<BigDecimal> ht;
    private final ReadOnlyObjectWrapper<BigDecimal> tva;
    private final ReadOnlyObjectWrapper<LocalDate> dateEntree;
    private final ReadOnlyObjectWrapper<Integer> stockInit;

    public BigDecimal getHt() {
        return ht.get();
    }

    public void setHt(BigDecimal value) {
        ht.set(value);
    }

    public ReadOnlyObjectProperty<BigDecimal> hTProperty() {
        return ht.getReadOnlyProperty();
    }

    public BigDecimal getTva() {
        return tva.get();
    }

    public void setTva(BigDecimal value) {
        tva.set(value);
    }

    public ReadOnlyObjectProperty<BigDecimal> tvaProperty() {
        return tva.getReadOnlyProperty();
    }

    public LocalDate getDateEntree() {
        return dateEntree.get();
    }

    public void setDateEntree(LocalDate date) {
        dateEntree.set(date);
    }

    public ReadOnlyObjectProperty<LocalDate> dateEntreeProperty() {
        return dateEntree.getReadOnlyProperty();
    }

    public Integer getStockInit() {
        return stockInit.get();
    }

    public void setStockInit(Integer value) {
        stockInit.set(value);
    }

    public ReadOnlyObjectProperty<Integer> stockInitProperty() {
        return stockInit.getReadOnlyProperty();
    }

    public Item() {
        itemId = 0;
        itemName = new ReadOnlyObjectWrapper<String>(this, "itemName");
        ht = new ReadOnlyObjectWrapper<>(this, "ht");
        tva = new ReadOnlyObjectWrapper<>(this, "tva");
        dateEntree = new ReadOnlyObjectWrapper<>(this, "dateEntree");
        stockInit = new ReadOnlyObjectWrapper<>(this, "stockInit");
    }

    public Item(Item item) {
        this();
        itemId = item.getItemId();
        itemName.set(item.getItemName());
        ht.set(item.getHt());
        tva.set(item.getTva());
        dateEntree.set(item.getDateEntree());
        stockInit.set(item.getStockInit());

    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("La valeur de l'ID d'article doit être un nombre entier positif.");
        }
        itemId = value;
    }

    public String getItemName() {
        return itemName.get();
    }

    public void setItemName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'élément doit être une valeur non nulle et non vide.");
        }

        itemName.set(name);
    }

    public ReadOnlyObjectProperty<String> itemNameProperty() {
        return itemName.getReadOnlyProperty();
    }

    @Override
    public String toString() {
        String name = itemName.get();
        return (name == null ? "<Nom d'article non défini>" : name);
    }

    @Override
    public int hashCode() {

        int id = getItemId();
        if (id != 0) {
            return id;
        }

        String name = itemName.get();
        if (name == null) {
            return super.hashCode();
        }

        return name.toLowerCase().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Item)) {
            return false;
        }

        Item item = (Item) obj;
        if (this.getItemId() != 0 && item.getItemId() != 0) {
            return (this.getItemId() == item.getItemId());
        }

        String name = item.getItemName();
        String thisName = itemName.get();

        if (name.isEmpty() || thisName.isEmpty()) {
            return false;
        }

        return thisName.equalsIgnoreCase(name);
    }

}
