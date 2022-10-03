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
public class Customer {
    private final ReadOnlyIntegerWrapper id;
    private final ReadOnlyStringWrapper name;
    private final ReadOnlyStringWrapper city;
    private final ReadOnlyStringWrapper phoneNumbers;
    private final ReadOnlyStringWrapper taxeProfessionnel;
    private final ReadOnlyStringWrapper ice;
    private final ReadOnlyObjectWrapper<BigDecimal> openingBalance;
    private final ReadOnlyObjectWrapper<BalanceType> balanceType;


    public Customer() {
        id = new ReadOnlyIntegerWrapper(this, "id", 0);
        name = new ReadOnlyStringWrapper(this, "name");
        city = new ReadOnlyStringWrapper(this, "city");
        phoneNumbers = new ReadOnlyStringWrapper(this, "phoneNumbers");
        taxeProfessionnel = new ReadOnlyStringWrapper(this, "taxeProfessionnel");
        ice = new ReadOnlyStringWrapper(this, "ice");
        openingBalance = new ReadOnlyObjectWrapper<>(this, "openingBalance");
        balanceType = new ReadOnlyObjectWrapper<>(this, "balanceType");
    }

    public Customer(Customer customer) {
        this();
        id.set(customer.getId());
        name.set(customer.getName());
        city.set(customer.getCity());
        phoneNumbers.set(customer.getPhoneNumbers());
        taxeProfessionnel.set(customer.getTaxeProfessionnel());
        ice.set(customer.getIce());
        openingBalance.set(customer.getOpeningBalance());
        balanceType.set(customer.getBalanceType());
    }

    public int getId() {
        return id.get();
    }

    public void setId(int value) {
        id.set(value);
    }

    public ReadOnlyIntegerProperty idProperty() {
        return id.getReadOnlyProperty();
    }

    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyProperty();
    }

    public String getPhoneNumbers() {
        return phoneNumbers.get();
    }

    public void setPhoneNumbers(String value) {
        phoneNumbers.set(value);
    }

    public ReadOnlyStringProperty phoneNumbersProperty() {
        return phoneNumbers.getReadOnlyProperty();
    }

    public String getTaxeProfessionnel() {
        return taxeProfessionnel.get();
    }

    public void setTaxeProfessionnel(String value) {
        taxeProfessionnel.set(value);
    }

    public ReadOnlyStringProperty taxeProfessionnelProperty() {
        return taxeProfessionnel.getReadOnlyProperty();
    }

    public String getCity() {
        return city.get();
    }

    public void setCity(String value) {
        city.set(value);
    }

    public ReadOnlyStringProperty cityProperty() {
        return city.getReadOnlyProperty();
    }

    public String getIce() {
        return ice.get();
    }

    public void setIce(String value) {
        ice.set(value);
    }

    public ReadOnlyStringProperty iceProperty() {
        return ice.getReadOnlyProperty();
    }


    public BigDecimal getOpeningBalance() {
        return openingBalance.get();
    }

    public void setOpeningBalance(BigDecimal value) {
        openingBalance.set(value);
    }

    public ReadOnlyObjectProperty<BigDecimal> openingBalanceProperty() {
        return openingBalance.getReadOnlyProperty();
    }

    public BalanceType getBalanceType() {
        return balanceType.get();
    }

    public void setBalanceType(BalanceType value) {
        balanceType.set(value);
    }

    public ReadOnlyObjectProperty<BalanceType> balanceTypeProperty() {
        return balanceType.getReadOnlyProperty();
    }

    @Override
    public String toString() {
        if (name.get() != null) {
            return name.get();
        }
        return super.toString();
    }

    @Override
    public int hashCode() {
        int code = id.get();
        if (code != 0) {
            return 0;
        }

        String custName = name.get();
        if (custName != null) {
            return custName.hashCode();
        }

        return super.hashCode();

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Customer)) {
            return false;
        }

        Customer cust = (Customer) obj;
        if (this.getId() == 0 && cust.getId() == 0) {
            return this.getName().equalsIgnoreCase(cust.getName());
        }

        return (this.getId() == cust.getId());

    }


}


