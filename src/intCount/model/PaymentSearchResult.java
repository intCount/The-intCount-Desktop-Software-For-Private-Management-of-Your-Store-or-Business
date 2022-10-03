/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.model;


import java.math.BigDecimal;
import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author
 */
public class PaymentSearchResult {

    private Boolean paymentRealised;

    private final ObjectProperty<LocalDate> paymentDate;
    private final ObjectProperty<Customer> customer;
    private final ObjectProperty<BigDecimal> amount;
    private final ObjectProperty<PaymentMode> paymentMode;
    private final StringProperty realisationStatus;
    private int paymentId;

    public PaymentSearchResult() {
        paymentDate = new SimpleObjectProperty<>();
        customer = new SimpleObjectProperty<>();
        amount = new SimpleObjectProperty<>();
        paymentMode = new SimpleObjectProperty<>();
        realisationStatus = new SimpleStringProperty();
    }

    public void setPaymentId(int id) {
        paymentId = id;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentDate(LocalDate date) {
        paymentDate.set(date);
    }

    public LocalDate getPaymentDate() {
        return paymentDate.get();
    }

    public ObjectProperty<LocalDate> paymentDateProperty() {
        return paymentDate;
    }

    public void setCustomer(Customer cust) {
        customer.setValue(cust);
    }

    public ObjectProperty<Customer> getCustomer() {
        return customer;
    }

    public ObjectProperty<Customer> customerProperty() {
        return customer;
    }

    public void setAmount(BigDecimal money) {
        amount.set(money);
    }

    public BigDecimal getAmount() {
        return amount.get();
    }

    public ObjectProperty<BigDecimal> amountProperty() {
        return amount;
    }

    public void setPaymentMode(PaymentMode mode) {
        paymentMode.set(mode);
    }

    public PaymentMode getPaymentMode() {
        return paymentMode.get();
    }

    public ObjectProperty<PaymentMode> paymentModeProperty() {
        return paymentMode;
    }

    public void setPaymentRealised(Boolean value) {
        paymentRealised = value;
        PaymentMode mode = paymentMode.get();
        if (mode == PaymentMode.CHEQUE || mode == PaymentMode.DD || mode == PaymentMode.CASH || mode == PaymentMode.BANKTRANSFER) {
            if (paymentRealised.equals(Boolean.TRUE)) {
                realisationStatus.set("Oui");
            } else {
                realisationStatus.set("Non");
            }
        } else {
            realisationStatus.set("N/A");
        }
    }

    public StringProperty realisationStatusProperty() {
        return realisationStatus;
    }
}
