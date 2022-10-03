package intCount.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

public class Stock {

    private final ReadOnlyObjectWrapper<LocalDate> dateSortie;
    private final ReadOnlyIntegerWrapper stockEnMvt;
    private final ReadOnlyStringWrapper itemName;
    private final ReadOnlyObjectWrapper<LocalDate> dateEntree;
    private final ReadOnlyIntegerWrapper stockInit;


    public LocalDate getDateSortie() {
        return dateSortie.get();
    }

    public void setDateSortie(LocalDate date) {
        dateSortie.set(date);
    }

    public ReadOnlyObjectProperty<LocalDate> dateSortieProperty() {
        return dateSortie.getReadOnlyProperty();
    }

    public Integer getStockEnMvt() {
        return stockEnMvt.get();
    }

    public void setStockEnMvt(Integer value) {
        stockEnMvt.set(value);
    }

    public ReadOnlyIntegerProperty stockEnMvtProperty() {
        return stockEnMvt.getReadOnlyProperty();
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

    public ReadOnlyIntegerProperty stockInitProperty() {
        return stockInit.getReadOnlyProperty();
    }

    public Stock() {
        dateSortie = new ReadOnlyObjectWrapper<>(this, "dateSortie");
        stockEnMvt = new ReadOnlyIntegerWrapper(this, "stockEnMvt");
        itemName = new ReadOnlyStringWrapper(this, "itemName");

        dateEntree = new ReadOnlyObjectWrapper<>(this, "dateEntree");
        stockInit = new ReadOnlyIntegerWrapper(this, "stockInit", 0);
    }

    public Stock(Stock stock) {
        this();
        dateSortie.set(stock.getDateSortie());
        stockEnMvt.set(stock.getStockEnMvt());

        itemName.set(stock.getItemName());

        dateEntree.set(stock.getDateEntree());
        stockInit.set(stock.getStockInit());

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

    public ReadOnlyStringProperty itemNameProperty() {
        return itemName.getReadOnlyProperty();
    }

    public static List<Stock> fromItems(List<Stock> list2) {
        ArrayList<Stock> list = new ArrayList<>(list2.size());

        list2.stream().forEach((stock) -> {
            list.add(new Stock(stock));
        });

        return list;
    }

}
