/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.database;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.*;

import intCount.model.AccountEntry;
import intCount.model.AccountLookupCriteria;
import intCount.model.PaymentMode;

/**
 *
 * @author 
 */
public abstract class AccountLookup {

    private static final Logger logger = 
            Logger.getLogger(AccountLookup.class.getName());

    public static List<AccountEntry> getAccountEntries(AccountLookupCriteria criteria)
            throws Exception {

        List<AccountEntry> result = new ArrayList<>(21);
        List<AccountEntry> entries = null;
        Connection connection = Database.getActiveYearConnection();

        AccountEntry openingBalanceEntry = getCustomerOpeningBalance(criteria);
        result.add(openingBalanceEntry);

        entries = getInvoiceEntries(criteria, connection);
        if (entries != null && entries.size() > 0) {
            result.addAll(entries);
        }

        entries = getPaymentEntries(criteria, connection);
        if (entries != null && entries.size() > 0) {
            result.addAll(entries);
        }

        //sort the entries collection by transaction date
        Collections.sort(result);

        return result;
    }

    private static AccountEntry getCustomerOpeningBalance(
            AccountLookupCriteria criteria) throws Exception {

        LocalDate date = criteria.getEntriesSince();
        BigDecimal amount = null;
        int customerId = criteria.getCustomer().getId();

        if (date != null) {
            date = date.minusDays(1);
            amount = CustomersPersistence.getCustomerBalance(customerId, date);
        } else {
            amount = CustomersPersistence.getCustomerOpeningBalance(customerId);
        }

        AccountEntry entry = new AccountEntry();
        entry.setEntryDescription("Opening balance");
        if (amount.signum() == -1) {
            entry.setDebitAmount(amount.abs());
        } else {
            entry.setCreditAmount(amount);
        }

        return entry;

    }

    private static List<AccountEntry> getInvoiceEntries(AccountLookupCriteria criteria,
            Connection connection) throws Exception {

        String sql = "SELECT id,invoicetype, invoicedate, invoice_total(id) "
                + "FROM invoices WHERE customerid = "
                + criteria.getCustomer().getId()+ "AND invoicetype IN ('Facture','Avoir')";

        if (criteria.getEntriesSince() != null) {
            LocalDate date = criteria.getEntriesSince();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            sql += " AND invoicedate >= '" + date.format(formatter) + "'";
        }

        List<AccountEntry> entries = new ArrayList<>(10);
        AccountEntry entry = null;
        int invoiceId = 0;
        try (Statement s = connection.createStatement()) {
            try (ResultSet rs = s.executeQuery(sql)) {
                while (rs.next()) {
                    entry = new AccountEntry();
                    invoiceId = rs.getInt(1);
                    entry.setInvoiceId(invoiceId);
                    entry.setInvoiceType(rs.getString(2));
                    System.out.println(rs.getString(2));
                    entry.setEntryDate(rs.getDate(3).toLocalDate());
                    entry.setEntryDescription("Facture #" + invoiceId + " issue");
                    entry.setDebitAmount(rs.getBigDecimal(4));
                    entries.add(entry);
                }
            }
        } catch (Exception ex) {
            logger.logp(Level.SEVERE, AccountLookup.class.getName(),
                    "getInvoiceEntries", "Error in getting invoice entries", ex);
            throw ex;
        }

        return entries;
    }

    private static List<AccountEntry> getPaymentEntries(AccountLookupCriteria criteria,
            Connection connection) throws Exception {
        String firstQuery = "SELECT id, paymentdate, amount, paymentmode "
                + "FROM payments WHERE customerid = "
                + criteria.getCustomer().getId()
                + " AND lower(paymentmode) in ('cash', 'banktransfer') ";
        String secondQuery = "SELECT id, paymentDate, amount, paymentmode "
                + "FROM payments p inner join payment_details pd "
                + "on id = paymentid WHERE customerid = " + criteria.getCustomer().getId()
                + " AND lower(paymentmode) in ('cheque', 'dd') AND amountrealised = true ";

        if (criteria.getEntriesSince() != null) {
            LocalDate date = criteria.getEntriesSince();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String dateClause = " AND paymentdate >= '" + date.format(formatter) + "'";
            firstQuery += dateClause;
            secondQuery += dateClause;
        }

        String sql = firstQuery + " UNION " + secondQuery;

        List<AccountEntry> entries = new ArrayList<>(10);
        AccountEntry entry = null;
        String modeString = null;
        PaymentMode mode = null;

        try (Statement s = connection.createStatement()) {
            try (ResultSet rs = s.executeQuery(sql)) {
                while (rs.next()) {
                    entry = new AccountEntry();
                    entry.setPaymentId(rs.getInt(1));
                    entry.setEntryDate(rs.getDate(2).toLocalDate());
                    entry.setCreditAmount(rs.getBigDecimal(3));
                    modeString = rs.getString(4);
                    mode = PaymentMode.valueOf(modeString.toUpperCase());
                    entry.setEntryDescription(mode.toString() + " payment");
                    entries.add(entry);
                }
            }
        } catch (Exception ex) {
            logger.logp(Level.SEVERE, AccountLookup.class.getName(),
                    "getPaymentEntries", "Error in getting payment entries", ex);
            throw ex;
        }

        return entries;
    }
}
