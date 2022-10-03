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

/**
 * @author
 */
public class InvoiceSearchResult {
    private String invoiceNumber;
    private String invoiceType;
    private ObjectProperty<Customer> customer;
    private ObjectProperty<LocalDate> invoiceDate;
    private ObjectProperty<BigDecimal> invoiceAmount;

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String val) {
        invoiceNumber = val;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public Customer getCustomer() {
        if (customer == null) {
            customer = new SimpleObjectProperty<>();
        }

        return customer.get();
    }

    public void setCustomer(Customer val) {
        if (customer == null) {
            customer = new SimpleObjectProperty<>();
        }
        customer.set(val);
    }

    public ObjectProperty<Customer> customerProperty() {
        if (customer == null) {
            customer = new SimpleObjectProperty<>();
        }
        return customer;
    }

    public LocalDate getInvoiceDate() {
        if (invoiceDate == null) {
            invoiceDate = new SimpleObjectProperty<>();
        }

        return invoiceDate.get();
    }

    public void setInvoiceDate(LocalDate val) {
        if (invoiceDate == null) {
            invoiceDate = new SimpleObjectProperty<>();
        }

        invoiceDate.set(val);
    }

    public ObjectProperty<LocalDate> invoiceDateProperty() {
        if (invoiceDate == null) {
            invoiceDate = new SimpleObjectProperty<>();
        }
        return invoiceDate;
    }

    public BigDecimal getInvoiceAmount() {
        if (invoiceAmount == null) {
            invoiceAmount = new SimpleObjectProperty<>();
        }

        return invoiceAmount.get();
    }

    public void setInvoiceAmount(BigDecimal val) {
        if (invoiceAmount == null) {
            invoiceAmount = new SimpleObjectProperty<>();
        }

        invoiceAmount.set(val);
    }

    public ObjectProperty<BigDecimal> invoiceAmountProperty() {
        if (invoiceAmount == null) {
            invoiceAmount = new SimpleObjectProperty<>();
        }

        return invoiceAmount;
    }

}
