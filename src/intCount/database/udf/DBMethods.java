/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.database.udf;

import java.math.BigDecimal;

import java.math.RoundingMode;
import java.sql.*;

/**
 *
 * @author 
 */
public final class DBMethods {

    public static BigDecimal getInvoiceTotal(int invoiceNumber)
            throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:default:connection");
        BigDecimal invoiceTotal = getItemsTotal(connection, invoiceNumber);

        String sql = "SELECT discount, additionalcharge FROM invoices "
                + "WHERE id = " + invoiceNumber;

        try (Statement statement = connection.createStatement()) {
            try (ResultSet result = statement.executeQuery(sql)) {
                if (result.next()) {
                    BigDecimal amount = result.getBigDecimal(1); //discount
                    if (!result.wasNull()) {
                        invoiceTotal = invoiceTotal.subtract(amount);
                    }
                    
                    amount = result.getBigDecimal(2); // additional charge
                    if (!result.wasNull()) {
                        invoiceTotal = invoiceTotal.add(amount);
                    }
                }

            }
        }
        
        
        return invoiceTotal;

    }

    private static BigDecimal getItemsTotal(Connection connection,
            int invoiceNumber) throws SQLException {
        String sql = "SELECT rate * quantity FROM invoice_items WHERE "
                + "invoice_id = " + invoiceNumber;

        BigDecimal total = BigDecimal.ZERO;
        try (Statement statement = connection.createStatement()) {
            try (ResultSet result = statement.executeQuery(sql)) {
                BigDecimal amount = null;
                while (result.next()) {
                    amount = result.getBigDecimal(1).setScale(2,
                            RoundingMode.HALF_UP);
                    total = total.add(amount);
                }
            }
        }
        
        return total;
    }

  /**
   * 
   * @param customerId
   * @param endDateString The last date considered for transactions. It is required
   * to be in the format 'yyyy-MM-dd'.
   * @return Customer's account balance
   * @throws SQLException 
   */
    public static BigDecimal getCustomerBalance(int customerId, String endDateString) 
       throws SQLException {
          Connection connection = DriverManager.getConnection("jdbc:default:connection");
          
          BigDecimal balance = getCustomerOpeningBalance(customerId, connection);
          BigDecimal invoicesTotal = getCustomerInvoicesTotal(customerId, endDateString, connection);
          BigDecimal paymentsTotal = getCustomerPaymentsTotal(customerId, endDateString, connection);
          
          balance = balance.subtract(invoicesTotal).add(paymentsTotal);
          return balance;
    }
    
    public static BigDecimal getCustomerOpeningBalance(int customerId) 
        throws SQLException{
          Connection connection = 
                  DriverManager.getConnection("jdbc:default:connection");
         return getCustomerOpeningBalance(customerId, connection);
    }
    
    private static BigDecimal getCustomerOpeningBalance(int customerId, 
        Connection connection) throws SQLException {
        
        String sql = "SELECT opening_balance, balance_type FROM customers " +
                "WHERE cust_id = " + customerId;
        BigDecimal balance = BigDecimal.ZERO;
        
        try(Statement s = connection.createStatement()) {
            try(ResultSet rs = s.executeQuery(sql)) {
                if (rs.next()) {
                    BigDecimal amount = rs.getBigDecimal(1);
                    if (!rs.wasNull()) {
                        balance = amount;
                        if(balance.signum() == 1) { //if balance > 0.0
                            String balanceType = rs.getString(2).toLowerCase();
                            if(balanceType.equalsIgnoreCase("d")) { //if debit balance
                                balance = balance.negate();
                            }
                        }
                    }
                }
            }
        }
        
        return balance;
    }
    
    private static BigDecimal getCustomerInvoicesTotal(int customerId, 
        String endDateString,  Connection connection) throws SQLException {
        
        String sql = "SELECT SUM(invoice_total(id)) FROM invoices " +
                "WHERE customerid = " + customerId;
        
        if (endDateString != null && !endDateString.isEmpty()) {
            sql += " AND invoicedate <= '" + endDateString + "'";
        }
        BigDecimal total = BigDecimal.ZERO;
        
        try(Statement s = connection.createStatement()) {
           try(ResultSet rs = s.executeQuery(sql)) {
               if (rs.next()) {
                   BigDecimal amount = rs.getBigDecimal(1);
                   if (!rs.wasNull()) {
                       total = amount;
                   }
               }
           }
        }
        
        return total;
    }
    
    private static BigDecimal getCustomerPaymentsTotal(int customerId,
      String endDateString, Connection connection) throws SQLException {
        
        String sql = "SELECT SUM(amount) FROM payments WHERE customerid = " +
                customerId + " AND lower(paymentmode) in ('cash', 'banktransfer')";
        
         if (endDateString != null && !endDateString.isEmpty()) {
            sql += " AND paymentdate <= '" + endDateString + "'";
        }
         
        BigDecimal total = BigDecimal.ZERO;
        
        try(Statement s = connection.createStatement()) {
            try(ResultSet rs = s.executeQuery(sql)) {
                if (rs.next()) {
                    BigDecimal amount = rs.getBigDecimal(1);
                    if (!rs.wasNull()) {
                        total = amount;
                    }
                }
            }
        }
        
       sql = "SELECT SUM(amount) FROM payments p inner join payment_details pd " + 
               "on id = paymentid WHERE customerid = " + customerId + 
               "AND lower(paymentmode) in ('cheque', 'dd') AND amountrealised = true";
       
        if (endDateString != null && !endDateString.isEmpty()) {
            sql += " AND paymentdate <= '" + endDateString + "'";
        }
       
       try(Statement s = connection.createStatement()) {
           try(ResultSet rs = s.executeQuery(sql)) {
               if (rs.next()) {
                   BigDecimal amount = rs.getBigDecimal(1);
                   if (!rs.wasNull()) {
                       total = total.add(amount);
                   }
                   
               }
           }
       }
        
        return total;
    }

}
