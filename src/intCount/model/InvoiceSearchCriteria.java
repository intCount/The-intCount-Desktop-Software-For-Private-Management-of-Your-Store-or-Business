/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author 
 */
public class InvoiceSearchCriteria {
    private String invoiceNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Customer> customers;
    private BigDecimal startAmount;
    private BigDecimal endAmount;
    private boolean cashInvoice = false;
    private String typeInvoice; 
    
	public boolean isCashInvoice() {
        return cashInvoice;
    }
    
    public void setCashInvoice(boolean value) {
        cashInvoice  = value;
    }
    
    public String getInvoiceNumber() {
        return invoiceNumber;
    }
    
    public void setInvoiceNumber(String val) {
        invoiceNumber = val;
    }
    
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
    
    public void setCustomers(List<Customer> list)  {
        customers = list;
    }
    
    public BigDecimal getStartAmount() {
        return startAmount;
    }
    
    public void setStartAmount(BigDecimal val) {
        startAmount = val;
    }
    
    public BigDecimal getEndAmount() {
        return endAmount;
    }
    
    public void setEndAmount(BigDecimal val) {
        endAmount = val;
    }
    
   public String getTypeInvoice() {
		return typeInvoice;
	}

	public void setTypeInvoice(String typeInvoice) {
		this.typeInvoice = typeInvoice;
	}
    
}
