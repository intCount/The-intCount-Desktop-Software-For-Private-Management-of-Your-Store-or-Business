/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.database;

import java.sql.*;


import java.time.LocalDate;
import java.util.logging.*;

import intCount.model.Customer;
import intCount.model.Payment;
import intCount.model.PaymentMode;
import intCount.utility.Utility;

/**
 *
 * @author
 */
public class PaymentPersistence {

	private final static Logger logger = Logger.getLogger(PaymentPersistence.class.getName());

	public static void savePayment(Payment payment) throws Exception {
		Connection connection = null;
		int paymentId = 0;

		try {
			connection = Database.getActiveYearConnection();
			connection.setAutoCommit(false);

			if (payment.getPaymentId() > 0) { // updating payment
				deleteInstrumentDetails(payment.getPaymentId(), connection);
				updatePayment(payment, connection);
			} else {
				paymentId = saveNewPayment(payment, connection);
				payment.setPaymentId(paymentId);
			}

			PaymentMode mode = payment.getPaymentMode();
			if (mode == PaymentMode.CASH || mode == PaymentMode.DD || mode == PaymentMode.CHEQUE
					|| mode == PaymentMode.BANKTRANSFER) {
				saveInstrumentDetails(payment, connection);
			}

			connection.commit();
		} catch (Exception e) {
			logger.logp(Level.SEVERE, PaymentPersistence.class.getName(), "savePayment", "Error in saving the payment",
					e);
			if (connection != null) {
				connection.rollback();
			}
			throw e;
		} finally {
			if (connection != null) {
				connection.setAutoCommit(true);
			}
		}

	}

	private static int saveNewPayment(Payment payment, Connection connection) throws SQLException {
		String sql = "INSERT INTO payments VALUES (default, ?, ?, ?, ?)";
		int paymentId = 0;

		try (PreparedStatement ps = connection.prepareStatement(sql, new String[] { "ID" })) {
			ps.setInt(1, payment.getCustomer().getId());
			LocalDate localDate = payment.getPaymentDate();
			Date date = new Date(Utility.getEpochMilli(localDate));
			ps.setDate(2, date);
			ps.setBigDecimal(3, payment.getAmount());
			ps.setString(4, payment.getPaymentMode().getValue());
			ps.executeUpdate();

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					paymentId = rs.getInt(1);
				}
			}
		}

		return paymentId;
	}

	private static void saveInstrumentDetails(Payment payment, Connection connection) throws SQLException {
		String sql = "INSERT INTO payment_details VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, payment.getPaymentId());
			ps.setString(2, payment.getInstrumentNumber());
			Date date = new Date(Utility.getEpochMilli(payment.getInstrumentDate()));
			ps.setDate(3, date);
			String drawnAt = payment.getDrawnAtBank();
			if (drawnAt != null && !drawnAt.isEmpty()) {
				ps.setString(4, drawnAt);
			} else {
				ps.setNull(4, Types.VARCHAR);
			}
			if (payment.getInstrumentRealised()) {
				ps.setBoolean(5, true);
			} else {
				ps.setBoolean(5, false);
			}

			ps.executeUpdate();
		}
	}

	private static void updatePayment(Payment payment, Connection connection) throws SQLException {
		String sql = "UPDATE payments SET customerid = ?, paymentdate = ?, "
				+ "amount = ?, paymentmode = ? WHERE id = ?";

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, payment.getCustomer().getId());
			LocalDate localDate = payment.getPaymentDate();
			Date date = new Date(Utility.getEpochMilli(localDate));
			ps.setDate(2, date);
			ps.setBigDecimal(3, payment.getAmount());
			ps.setString(4, payment.getPaymentMode().getValue());
			ps.setInt(5, payment.getPaymentId());
			ps.executeUpdate();

		}
	}

	private static void deleteInstrumentDetails(int paymentId, Connection connection) throws SQLException {
		String sql = "DELETE FROM payment_details WHERE paymentid = " + paymentId;

		try (Statement s = connection.createStatement()) {
			s.executeUpdate(sql);
		}
	}

	public static Payment getPayment(int paymentId) throws SQLException {

		StringBuilder sql = new StringBuilder(300);
		sql.append("SELECT p.*, c.name, pd.* ").append("FROM payments p INNER JOIN customers c ")
				.append("ON p.customerid = c.cust_id ").append("LEFT JOIN payment_details pd ")
				.append("ON p.id = pd.paymentid ").append("WHERE p.id = ").append(paymentId);

		Connection connection = null;
		Payment payment = null;
		try {
			connection = Database.getActiveYearConnection();
			try (Statement s = connection.createStatement()) {
				try (ResultSet rs = s.executeQuery(sql.toString())) {
					if (rs.next()) {
						payment = new Payment();
						payment.setPaymentId(paymentId);
						Customer customer = new Customer();
						customer.setId(rs.getInt("customerid"));
						customer.setName(rs.getString("name"));
						payment.setCustomer(customer);
						LocalDate date = rs.getDate("paymentdate").toLocalDate();
						payment.setPaymentDate(date);
						payment.setAmount(rs.getBigDecimal("amount"));
						String modeString = rs.getString("paymentmode");
						for (PaymentMode mode : PaymentMode.values()) {
							if (mode.getValue().equalsIgnoreCase(modeString)) {
								payment.setPaymentMode(mode);
								break;
							}
						}

						if (payment.getPaymentMode() == PaymentMode.CHEQUE || payment.getPaymentMode() == PaymentMode.DD
								|| payment.getPaymentMode() == PaymentMode.CASH
								|| payment.getPaymentMode() == PaymentMode.BANKTRANSFER) {
							payment.setInstrumentNumber(rs.getString("instrumentnumber"));
							date = rs.getDate("instrumentdate").toLocalDate();
							payment.setInstrumentDate(date);
							String drawnAt = rs.getString("drawanatbank");
							if (!rs.wasNull()) {
								payment.setDrawnAtBank(drawnAt);
							}
							payment.setInstrumentRealised(rs.getBoolean("amountrealised"));
						}
					}
				}
			}
		} catch (SQLException e) {
			logger.logp(Level.SEVERE, PaymentPersistence.class.getName(), "getPayment",
					"Error in getting the payment details", e);
			throw e;
		}

		return payment;

	}
}
