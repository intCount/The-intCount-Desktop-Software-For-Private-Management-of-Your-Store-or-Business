/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.*;

/**
 * @author
 */
public class CustomerWithState extends Customer {
    private ReadOnlyObjectWrapper<UpdateState> updateState =
            new ReadOnlyObjectWrapper<>(this, "updateState");

    public UpdateState getUpdateState() {
        return updateState.get();
    }

    public void setUpdateState(UpdateState value) {
        updateState.set(value);
    }

    public ReadOnlyObjectProperty<UpdateState> updateStateProperty() {
        return updateState.getReadOnlyProperty();
    }

    public CustomerWithState() {
        super();
    }

    public CustomerWithState(Customer customer) {
        super(customer);
    }

    public static List<CustomerWithState> fromCustomers(List<Customer> customers) {
        ArrayList<CustomerWithState> list = new ArrayList<>(customers.size());
        customers.stream().forEach((c) -> {
            list.add(new CustomerWithState(c));
        });
        return list;
    }
}
