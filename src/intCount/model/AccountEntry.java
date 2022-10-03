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
public class AccountEntry implements Comparable<AccountEntry> {
    private final ObjectProperty<LocalDate> entryDate;
    private final StringProperty entryDescription;
    private final ObjectProperty<BigDecimal> debitAmount;
    private final ObjectProperty<BigDecimal> creditAmount;
    private final StringProperty invoiceType;

    private int invoiceId = 0;
    private int paymentId = 0;

    public String getInvoiceType() {
        return invoiceType.get();
    }

    public void setInvoiceType(String value) {
        invoiceType.set(value);
    }

    public AccountEntry() {
        entryDate = new SimpleObjectProperty<>();
        entryDescription = new SimpleStringProperty();
        debitAmount = new SimpleObjectProperty<>();
        creditAmount = new SimpleObjectProperty<>();
        invoiceType = new SimpleStringProperty();
    }

    public LocalDate getEntryDate() {
        return entryDate.get();
    }

    public void setEntryDate(LocalDate date) {
        entryDate.setValue(date);
    }

    public ObjectProperty<LocalDate> entryDateProperty() {
        return entryDate;
    }

    public String getEntryDescription() {
        return entryDescription.get();
    }

    public void setEntryDescription(String value) {
        entryDescription.set(value);
    }

    public StringProperty entryDescriptionProperty() {
        return entryDescription;
    }

    public BigDecimal getDebitAmount() {
        return debitAmount.get();
    }

    public void setDebitAmount(BigDecimal amount) {
        debitAmount.set(amount);
    }

    public ObjectProperty<BigDecimal> debitAmountProperty() {
        return debitAmount;
    }

    public BigDecimal getCreditAmount() {
        return creditAmount.get();
    }

    public void setCreditAmount(BigDecimal amount) {
        creditAmount.set(amount);
    }

    public ObjectProperty<BigDecimal> creditAmountProperty() {
        return creditAmount;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int id) {
        invoiceId = id;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int id) {
        paymentId = id;
    }

    @Override
    public int compareTo(AccountEntry o) {
        if (entryDate.get() == null) {
            return -1;
        } else if (o.getEntryDate() == null) {
            return 1;
        } else {
            return entryDate.get().compareTo(o.getEntryDate());
        }
    }


}
