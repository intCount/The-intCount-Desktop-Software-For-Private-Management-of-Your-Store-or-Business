/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.model;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author 
 */
public class PaymentSearchCriteria {
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Customer> customers;
    private List<PaymentMode> paymentModes;
    private Boolean realisedPaymentsOnly;
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate date) {
        startDate = date;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate date) {
        endDate = date;
    }
    
    public List<Customer> getCustomers() {
        return customers;
    }
    
    public void setCustomers(List<Customer> customerList) {
        customers  = customerList;
    }
    
    public Boolean isRealisedPaymentsOnly() {
        return realisedPaymentsOnly;
    }
    
    public void setRealisedPaymentsOnly(Boolean value) {
        realisedPaymentsOnly = value;
    }
    
    public List<PaymentMode> getPaymentModes() {
        return paymentModes;
    }
    
    public void setPaymentModes(List<PaymentMode> modes) {
        paymentModes = modes;
    }
    
    
}
