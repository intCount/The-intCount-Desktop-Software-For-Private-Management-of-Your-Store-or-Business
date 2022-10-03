/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

import intCount.model.Customer;
import intCount.model.PaymentMode;
import intCount.model.PaymentSearchCriteria;
import intCount.model.PaymentSearchResult;

/**
 *
 * @author
 */
public class PaymentSearch {

	private final static Logger logger = Logger.getLogger(PaymentSearch.class.getName());

	public static List<PaymentSearchResult> searchPayments(PaymentSearchCriteria criteria) throws SQLException {
		String sql = getQuerySQL(criteria);
		List<PaymentSearchResult> results = null;
		Connection connection = null;
		PaymentSearchResult result = null;
		Customer customer = null;
		LocalDate date = null;
		String mode = null;

		try {
			connection = Database.getActiveYearConnection();
			try (Statement s = connection.createStatement()) {
				try (ResultSet rs = s.executeQuery(sql)) {
					results = new ArrayList<>(20);
					while (rs.next()) {
						result = new PaymentSearchResult();
						result.setPaymentId(rs.getInt(1));
						customer = new Customer();
						customer.setId(rs.getInt(2));
						customer.setName(rs.getString(7));
						result.setCustomer(customer);
						date = rs.getDate(3).toLocalDate();
						result.setPaymentDate(date);
						result.setAmount(rs.getBigDecimal(4));
						mode = rs.getString(5);
						for (PaymentMode m : PaymentMode.values()) {
							if (m.getValue().equalsIgnoreCase(mode)) {
								result.setPaymentMode(m);
								break;
							}
						}
						if (result.getPaymentMode() == PaymentMode.CHEQUE || result.getPaymentMode() == PaymentMode.DD
								|| result.getPaymentMode() == PaymentMode.CASH
								|| result.getPaymentMode() == PaymentMode.BANKTRANSFER) {
							result.setPaymentRealised(Boolean.valueOf(rs.getBoolean(6)));
						} else {
							result.setPaymentRealised(null);
						}
						results.add(result);
					} // end of while loop
				}
			}
		} catch (SQLException e) {
			logger.logp(Level.SEVERE, PaymentSearch.class.getName(), "searchPayments",
					"Error in searching the payments", e);
			throw e;
		}
		return results;
	}

	private static String getQuerySQL(PaymentSearchCriteria criteria) {

		StringBuilder sql = new StringBuilder(300);
		sql.append("SELECT p.*, pd.amountrealised, c.name ").append("FROM payments p inner join customers c ")
				.append("ON p.customerid = c.cust_id ");
		sql.append(" LEFT JOIN payment_details pd ON p.id = pd.paymentid ");

		Boolean realisedPaymentsOnly = criteria.isRealisedPaymentsOnly();
		boolean conditionApplied = false;

		// date criterion
		if (criteria.getStartDate() != null) {
			conditionApplied = true;
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate startDate = criteria.getStartDate();
			LocalDate endDate = criteria.getEndDate();
			sql.append(" WHERE p.paymentdate BETWEEN '").append(startDate.format(formatter)).append("' AND '")
					.append(endDate.format(formatter)).append("' ");
		}

		// customers criterion
		List<Customer> customers = criteria.getCustomers();
		if (customers != null) {
			if (!conditionApplied) {
				sql.append(" WHERE ");
				conditionApplied = true;
			} else {
				sql.append(" AND ");
			}
			sql.append("customerid in (");
			for (Customer customer : customers) {
				sql.append(customer.getId()).append(", ");
			}
			sql.toString();
			sql.delete(sql.length() - 2, sql.length());
			sql.append(") ");

		}

		// payment mode criterion
		List<PaymentMode> modes = criteria.getPaymentModes();
		if (modes != null) {
			if (!conditionApplied) {
				conditionApplied = true;
				sql.append(" WHERE ");
			} else {
				sql.append(" AND ");
			}

			sql.append("paymentmode IN (");
			for (PaymentMode mode : modes) {
				sql.append("'").append(mode.getValue()).append("', ");
			}

			// delete command and the trailing space
			sql.delete(sql.length() - 2, sql.length());
			sql.append(") ");

			if (realisedPaymentsOnly != null && realisedPaymentsOnly.equals(Boolean.TRUE)) {
				sql.append(" AND amountrealised is null or amountrealised = true ");
			}
		} // end of payment modes criterion

		sql.append(" ORDER BY paymentdate");

		return sql.toString();
	}

	public static void deletePayment(int paymentId) throws SQLException {

		Connection connection = null;
		String sql = "DELETE FROM payments WHERE id = " + paymentId;

		try {
			connection = Database.getActiveYearConnection();
			try (Statement s = connection.createStatement()) {
				s.executeUpdate(sql);
			}
		} catch (SQLException e) {
			logger.logp(Level.SEVERE, PaymentSearch.class.getName(), "deletePayment", "Error in deleting the payment",
					e);
			throw e;
		}
	}

}
