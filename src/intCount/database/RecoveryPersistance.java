package intCount.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import intCount.model.Recovery;

public class RecoveryPersistance {

	private final static Logger logger = Logger.getLogger(StockPersistance.class.getName());

	public static List<Recovery> getRecovery() throws Exception {
		Connection connection = Database.getActiveYearConnection();
		int rowCount = getNumberOfItems(connection);
		System.out.println(rowCount);
		ArrayList<Recovery> stockData = new ArrayList<>(rowCount);

		String sql = "SELECT name, SUM(amount),MAX(paymentdate) from payments inner JOIN customers on customers.CUST_ID= payments.CUSTOMERID group by name";

		Recovery recovery = null;

		try (Statement s = connection.createStatement()) {
			try (ResultSet rs = s.executeQuery(sql)) {
				while (rs.next()) {

					recovery = new Recovery();

					recovery.setCustomer(rs.getString(1));
					recovery.setAmount(rs.getBigDecimal(2));
					recovery.setPaymentDate(rs.getDate(3).toLocalDate());

					stockData.add(recovery);
				}

			}
		} catch (Exception ex) {
			logger.logp(Level.SEVERE, InvoicePersistence.class.getName(), "getInvoiceItems",
					"Error in getting invoice items", ex);
			throw ex;
		}
		// System.out.println(stock);
		return stockData;
	}

	public static int getNumberOfItems(Connection connection) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			String sql = "SELECT count(item_id) FROM invoice_items INNER JOIN invoices ON invoice_items.invoice_id=invoices.id where invoicetype= 'facture'";
			try (ResultSet rs = statement.executeQuery(sql)) {
				rs.next();
				return rs.getInt(1);
			}
		} catch (SQLException ex) {
			logger.logp(Level.SEVERE, ItemsPersistence.class.getName(), "getNumberOfItems",
					"Error in getting the number of items", ex);
			throw ex;
		}
	}

}
