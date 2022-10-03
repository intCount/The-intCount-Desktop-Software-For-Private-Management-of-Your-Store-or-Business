package intCount.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

public class Recovery {

    private final ReadOnlyStringWrapper customer;
    private final ReadOnlyObjectWrapper<BigDecimal> amount;
    private final ReadOnlyObjectWrapper<LocalDate> paymentDate;

    public Recovery() {

        customer = new ReadOnlyStringWrapper(this, "customer");
        amount = new ReadOnlyObjectWrapper<>(this, "amount");
        paymentDate = new ReadOnlyObjectWrapper<>(this, "paymentDate");

    }

    public Recovery(Recovery recovery) {
        this();
        customer.set(recovery.getCustomer());
        amount.set(recovery.getAmount());

        paymentDate.set(recovery.getPaymentDate());
    }

    public String getCustomer() {
        return customer.get();
    }

    public void setCustomer(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Le client ne peut pas se voir attribuer une valeur nulle.");
        }

        customer.set(value);
    }

    public ReadOnlyStringProperty customerProperty() {
        return customer.getReadOnlyProperty();
    }

    public BigDecimal getAmount() {
        return amount.get();
    }

    public void setAmount(BigDecimal value) {
        amount.set(value);
    }

    public ReadOnlyObjectProperty<BigDecimal> amountProperty() {
        return amount.getReadOnlyProperty();
    }

    public LocalDate getPaymentDate() {
        return paymentDate.get();
    }

    public void setPaymentDate(LocalDate value) {
        if (value == null) {
            throw new IllegalArgumentException("La date de paiement ne peut pas être nulle.");
        }

        paymentDate.set(value);
    }

    public ObjectProperty<LocalDate> paymentDateProperty() {
        return paymentDate;
    }

    public static List<Recovery> fromItems(List<Recovery> list2) {
        ArrayList<Recovery> list = new ArrayList<>(list2.size());

        list2.stream().forEach((stock) -> {
            list.add(new Recovery(stock));
        });

        return list;
    }

}
