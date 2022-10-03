/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.database;

import java.math.*;
import java.time.format.*;
import java.util.*;
import java.sql.*;
import java.util.logging.*;

import intCount.model.*;

/**
 *
 * @author
 */
public class InvoiceSearch {

	private final static Logger logger = Logger.getLogger(InvoiceSearch.class.getName());

	public static List<InvoiceSearchResult> searchInvoice(InvoiceSearchCriteria criteria) throws Exception {

		String sql = getQuerySQL(criteria);
		List<InvoiceSearchResult> results = null;

		try {
			Connection connection = Database.getActiveYearConnection();
			try (Statement s = connection.createStatement()) {
				try (ResultSet rs = s.executeQuery(sql)) {
					results = new ArrayList<>(50);
					InvoiceSearchResult result = null;
					Customer customer = null;
					BigDecimal amount = null;
					String name = null;
					String city = null;
					while (rs.next()) {
						result = new InvoiceSearchResult();
						result.setInvoiceNumber(Integer.toString(rs.getInt(1)));
						result.setInvoiceType(rs.getString(2));
						result.setInvoiceDate(rs.getDate(3).toLocalDate());
						customer = new Customer();
						name = rs.getString(4);
						if (!rs.wasNull()) {
							customer.setName(name);
							System.out.println(name);
							city = rs.getString(5);
							if (!rs.wasNull()) {
								customer.setCity(city);
							}
						} // else {
							// customer.setName("CASH");
							// }
						result.setCustomer(customer);
						amount = rs.getBigDecimal(6);
						result.setInvoiceAmount(amount);
						results.add(result);
					}
				}
			}
		} catch (Exception e) {
			logger.logp(Level.SEVERE, InvoiceSearch.class.getName(), "searchInvoice",
					"Error in getting the invoice list", e);
			throw e;
		}

		return results;

	}

	private static String getQuerySQL(InvoiceSearchCriteria criteria) {

		StringBuilder sql = new StringBuilder(300);
		sql.append("SELECT id,invoicetype, invoicedate, name, city, invoice_total(id) ")
				.append("FROM invoices AS i LEFT JOIN Customers AS c ").append("ON i.customerid = c.cust_id  WHERE ");

		if (criteria.getInvoiceNumber() != null) {
			sql.append("id = ").append(criteria.getInvoiceNumber());
		} else {
			boolean conditionApplied = false;
//invoiceType
			if (criteria.getTypeInvoice() != null) {
				conditionApplied = true;
				System.out.println();
				if(criteria.getTypeInvoice().equals("('Facture','Avoir')")) {
					sql.append("invoicetype IN ").append(criteria.getTypeInvoice());
				} else if (criteria.getTypeInvoice().equals("'Devis'")) {
					sql.append("invoicetype = ").append(criteria.getTypeInvoice());
				}
				

			}
			// date
			if (criteria.getStartDate() != null) {
				conditionApplied = true;
				DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");
				String startDateString = criteria.getStartDate().format(dateFormatter);
				String endDateString = criteria.getEndDate().format(dateFormatter);

				sql.append("invoicedate BETWEEN '").append(startDateString).append("' AND '").append(endDateString)
						.append("' ");
			} // end of date criteria

			// cash or credit invoice
			List<Customer> customers = criteria.getCustomers();
			if (criteria.isCashInvoice() || (customers != null && customers.size() > 0)) {
				if (conditionApplied) {
					sql.append(" AND ");
				}
				conditionApplied = true;

				// if (criteria.isCashInvoice()) {
				// sql.append("name is null ");
				// } else {
				sql.append("cust_id in (");
				for (Customer customer : customers) {
					sql.append(customer.getId()).append(",");
				}
				sql.deleteCharAt(sql.length() - 1); // remove the trailing comma
				sql.append(") ");
				// }
			}

			// amount
			BigDecimal amount = criteria.getStartAmount();
			if (amount != null) {
				if (conditionApplied) {
					sql.append(" AND ");
				}
				conditionApplied = true;
				sql.append(" invoice_total(id) BETWEEN ").append(amount.toPlainString()).append(" AND ")
						.append(criteria.getEndAmount().toPlainString());

			}

		}
		System.out.println(sql.toString());
		return sql.toString();
	}

	public static void deleteInvoice(int invoiceNumber) throws Exception {
		String sql = "DELETE FROM invoices WHERE ID = ?";
		Connection connection = Database.getActiveYearConnection();

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, invoiceNumber);
			ps.executeUpdate();
		} catch (Exception ex) {
			logger.logp(Level.SEVERE, InvoiceSearch.class.getName(), "deleteInvoice", "Error in deleting the invoice",
					ex);
			throw ex;
		}

	}
}
