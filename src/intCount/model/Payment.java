/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author
 */
public class Payment {
    private final IntegerProperty paymentId;
    private final ObjectProperty<PaymentMode> paymentMode;
    private final ObjectProperty<Customer> customer;
    private final ObjectProperty<LocalDate> paymentDate;
    private final ObjectProperty<BigDecimal> amount;
    private final StringProperty instrumentNumber;
    private final ObjectProperty<LocalDate> instrumentDate;
    private final StringProperty drawnAtBank;
    private final BooleanProperty instrumentRealised;

    public Payment() {
        paymentId = new SimpleIntegerProperty(0);
        paymentMode = new SimpleObjectProperty<>(null);
        customer = new SimpleObjectProperty<>(null);
        paymentDate = new SimpleObjectProperty<>(null);
        amount = new SimpleObjectProperty<>(null);
        instrumentNumber = new SimpleStringProperty(null);
        instrumentDate = new SimpleObjectProperty<>(null);
        drawnAtBank = new SimpleStringProperty(null);
        instrumentRealised = new SimpleBooleanProperty(false);
    }

    public int getPaymentId() {
        return paymentId.getValue();
    }

    public void setPaymentId(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("L'ID de paiement doit être un nombre entier positif.");
        }

        paymentId.set(value);
    }

    public PaymentMode getPaymentMode() {
        return paymentMode.get();
    }

    public void setPaymentMode(PaymentMode value) {
        if (value == null) {
            throw new IllegalArgumentException("Le mode de paiement ne peut pas se voir attribuer une valeur nulle.");
        }
        paymentMode.set(value);
    }

    public ObjectProperty<PaymentMode> paymentModeProperty() {
        return paymentMode;
    }

    public Customer getCustomer() {
        return customer.get();
    }

    public void setCustomer(Customer value) {
        if (value == null) {
            throw new IllegalArgumentException("Le client ne peut pas se voir attribuer une valeur nulle.");
        }

        customer.set(value);
    }

    public ObjectProperty<Customer> customerProperty() {
        return customer;
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

    public BigDecimal getAmount() {
        return amount.get();
    }

    public void setAmount(BigDecimal value) {
        amount.set(value);
    }

    public ObjectProperty<BigDecimal> amountProperty() {
        return amount;
    }

    public String getInstrumentNumber() {
        return instrumentNumber.get();
    }

    public void setInstrumentNumber(String value) {
        instrumentNumber.set(value);
    }

    public StringProperty instrumentNumberProperty() {
        return instrumentNumber;
    }

    public LocalDate getInstrumentDate() {
        return instrumentDate.get();
    }

    public void setInstrumentDate(LocalDate value) {
        instrumentDate.set(value);
    }

    public ObjectProperty<LocalDate> instrumentDateProperty() {
        return instrumentDate;
    }

    public String getDrawnAtBank() {
        return drawnAtBank.get();
    }

    public void setDrawnAtBank(String value) {
        drawnAtBank.set(value);
    }

    public StringProperty drawnAtBankProperty() {
        return drawnAtBank;
    }

    public boolean getInstrumentRealised() {
        return instrumentRealised.get();
    }

    public void setInstrumentRealised(boolean value) {
        instrumentRealised.set(value);
    }

    public BooleanProperty instrumentRealisedProperty() {
        return instrumentRealised;
    }

}
