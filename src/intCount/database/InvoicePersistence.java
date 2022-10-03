
package intCount.database;

import intCount.model.*;
import intCount.utility.Utility;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class InvoicePersistence {

	private final static Logger logger = Logger.getLogger(InvoicePersistence.class.getName());

	public static void saveInvoice(Invoice invoice) throws Exception {

		int invoiceId = invoice.getInvoiceId();
		boolean updateInvoice = (invoiceId != 0);
		Connection connection = null;

		try {
			connection = Database.getActiveYearConnection();
			connection.setAutoCommit(false);

			if (updateInvoice) {
				updateInvoice(invoice, connection);
				deleteInvoiceItems(invoice, connection);
				// saveInvoiceItems(invoice, connection);
			} else {
				int id = saveNewInvoice(invoice, connection);
				invoice.setInvoiceId(id);
			}

			saveInvoiceItems(invoice, connection);

			connection.commit();

		} catch (Exception e) {
			logger.logp(Level.SEVERE, InvoicePersistence.class.getName(), "saveInvoice", "Error in saving the invoice",
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

	private static void deleteInvoiceItems(Invoice invoice, Connection connection) throws Exception {
		String sql = "DELETE FROM invoice_items WHERE invoice_id = " + invoice.getInvoiceId();

		try (Statement s = connection.createStatement()) {
			s.executeUpdate(sql);
		} catch (Exception ex) {
			logger.logp(Level.SEVERE, InvoicePersistence.class.getName(), "deleteInvoiceItems",
					"Error in deleting invoice items", ex);
			throw ex;
		}
	}

	private static void updateInvoice(Invoice invoice, Connection connection) throws Exception {

		String sql = "UPDATE invoices set invoicedate = ?, iscashinvoice = ?, "
				+ "customerid = ?, discount = ?, additionalcharge = ?, invoicetype = ? " + "WHERE id = ?";

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setDate(1, new Date(Utility.getEpochMilli(invoice.getInvoiceDate())));
			ps.setBoolean(2, invoice.getIsCashInvoice());

			// if (!invoice.getIsCashInvoice()) {
			int id = invoice.getCustomer().getId();
			ps.setInt(3, id);
			// } else {
			// ps.setNull(3, Types.INTEGER);
			// }

			BigDecimal discount = invoice.getDiscount();
			if (discount == null) {
				ps.setNull(4, Types.DECIMAL);
			} else {
				ps.setBigDecimal(4, discount);
			}
			BigDecimal charge = invoice.getAdditionalCharge();
			if (charge == null) {
				ps.setNull(5, Types.DECIMAL);
			} else {
				ps.setBigDecimal(5, charge);
			}

			ps.setInt(7, invoice.getInvoiceId());
			ps.setString(6, invoice.getInvoiceType());

			ps.executeUpdate();
		} catch (Exception ex) {
			logger.logp(Level.SEVERE, InvoicePersistence.class.getName(), "updateInvoice",
					"Error in updating the invoice", ex);
			throw ex;
		}
	}

	private static void saveInvoiceItems(Invoice invoice, Connection connection) throws Exception {

		String itemSQL = "INSERT INTO invoice_items values (?, ?, ?, ?, ?,?)";
		int invoiceId = invoice.getInvoiceId();

		try (PreparedStatement ps = connection.prepareStatement(itemSQL)) {
			for (InvoiceItem item : invoice.getInvoiceItems()) {
				ps.setInt(1, invoiceId);
				ps.setInt(2, item.getItem().getItemId());
				ps.setBigDecimal(3, item.getRate());
				ps.setInt(4, item.getUnit().getUnitId());
				ps.setBigDecimal(5, item.getQuantity());
				ps.setBigDecimal(6, item.getTva());
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (Exception ex) {
			logger.logp(Level.SEVERE, InvoicePersistence.class.getName(), "saveInvoiceItems",
					"Error in saving invoice items", ex);
			throw ex;
		}
	}

	public static boolean getInvoiceItemsCount(Invoice invoice, Connection connection) throws Exception {

		String itemSQL = "select * from invoice_items where invoice_id = " + invoice.getInvoiceId();
		boolean sum = false;
		try (PreparedStatement ps = connection.prepareStatement(itemSQL)) {
			for (InvoiceItem item : invoice.getInvoiceItems()) {
				if (item.getTva().compareTo(BigDecimal.ZERO) != 0) {
					sum = true;
				}
			}
		
			return sum;
		} catch (Exception ex) {
			logger.logp(Level.SEVERE, InvoicePersistence.class.getName(), "saveInvoiceItems",
					"Error in checking if TVA is null", ex);
			throw ex;
		}

	}

	/**
	 * Saves a new invoice as oppose to updating an existing invoice
	 * 
	 * @param invoice    - Invoice data
	 * @param connection - Database connection
	 * @return - Id of the new invoice returned by the database
	 * @throws Exception
	 */
	public static int saveNewInvoice(Invoice invoice, Connection connection) throws Exception {

		String insertSQL = "INSERT INTO invoices (id,invoicedate,iscashinvoice,customerid,discount,additionalcharge,invoicetype) values (default, ?, ?, ?, ?, ?,?)";
		int invoiceId = 0;

		try (PreparedStatement ps = connection.prepareStatement(insertSQL, new String[] { "ID" })) {
			ps.setDate(1, new Date(Utility.getEpochMilli(invoice.getInvoiceDate())));
			ps.setBoolean(2, invoice.getIsCashInvoice());
			// if (!invoice.getIsCashInvoice()) {
			int id = invoice.getCustomer().getId();
			ps.setInt(3, id);
			// } else {
			// ps.setNull(3, Types.INTEGER);
			// }
			BigDecimal discount = invoice.getDiscount();
			if (discount == null) {
				ps.setNull(4, Types.DECIMAL);
			} else {
				ps.setBigDecimal(4, discount);
			}
			BigDecimal charge = invoice.getAdditionalCharge();
			if (charge == null) {
				ps.setNull(5, Types.DECIMAL);
			} else {
				ps.setBigDecimal(5, charge);
			}
			ps.setString(6, invoice.getInvoiceType());

			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				rs.next();
				invoiceId = rs.getInt(1);
			}
			// System.out.println(invoice.getInvoiceType());
		} catch (Exception ex) {
			logger.logp(Level.SEVERE, InvoicePersistence.class.getName(), "saveNewInvoice",
					"Error in saving the new invoice", ex);
			throw ex;
		}

		return invoiceId;
	}

	public static Invoice getInvoice(int invoiceNumber) throws Exception {
		Connection connection = Database.getActiveYearConnection();
		Invoice invoice = new Invoice();
		invoice.setInvoiceId(invoiceNumber);

		List<InvoiceItem> invoiceItems = getInvoiceItems(connection, invoiceNumber);
		invoice.setInvoiceItems(invoiceItems);

		String sql = "SELECT i.*, c.name, c.city, c.ICE FROM invoices i "
				+ "LEFT JOIN customers c ON i.customerid = c.cust_id " + " WHERE i.id = " + invoiceNumber;
		try (Statement s = connection.createStatement()) {
			try (ResultSet rs = s.executeQuery(sql)) {
				if (!rs.next()) {
					return null;
				}
		
				invoice.setInvoiceDate(rs.getDate(2).toLocalDate());
				invoice.setIsCashInvoice(rs.getBoolean(3));
				// if (!invoice.getIsCashInvoice()) {
				Customer customer = new Customer();
				customer.setId(rs.getInt(4));
				customer.setName(rs.getString(8));
				String city = rs.getString(9);
				if (!rs.wasNull()) {
					customer.setCity(city);
				}
				String ice = rs.getString(10);
				if (!rs.wasNull()) {
					customer.setIce(ice);
				}
					
				invoice.setCustomer(customer);
				// }
				BigDecimal amount = rs.getBigDecimal(5); // discount
				if (!rs.wasNull()) {
					invoice.setDiscount(amount);
				}
				amount = rs.getBigDecimal(6); // additional charge
				if (!rs.wasNull()) {
					invoice.setAdditionalCharge(amount);
				}
				invoice.setInvoiceType(rs.getString(7));
				// System.out.println(getTypeOfInvoice());

			}
		} catch (Exception ex) {
			logger.logp(Level.SEVERE, InvoicePersistence.class.getName(), "getInvoice",
					"Error in getting invoice details", ex);
			throw ex;
		}

		return invoice;

	}

	public static List<InvoiceItem> getInvoiceItems(Connection connection, int invoiceNumber) throws Exception {
		List<InvoiceItem> invoiceItems = new ArrayList<>(10);
		String sql = "SELECT inv.*, i.name, mu.name " + "FROM invoice_items inv "
				+ "INNER JOIN items i ON inv.item_id = i.id "
				+ "INNER JOIN measurement_units mu on inv.measurement_unit = mu.id " + " WHERE invoice_id = "
				+ invoiceNumber;

		InvoiceItem invoiceItem = null;
		Item item = null;
		MeasurementUnit unit = null;

		try (Statement s = connection.createStatement()) {
			try (ResultSet rs = s.executeQuery(sql)) {
				while (rs.next()) {
					invoiceItem = new InvoiceItem();
					item = new Item();
					item.setItemId(rs.getInt(1)); // item id
					item.setItemName(rs.getString(7)); // item name
					invoiceItem.setItem(item);
					invoiceItem.setRate(rs.getBigDecimal(3)); // rate
					unit = new MeasurementUnit();
					unit.setUnitId(rs.getInt(4)); // measurement unit
					unit.setUnitName(rs.getString(8)); // unit name
					invoiceItem.setUnit(unit);
					invoiceItem.setQuantity(rs.getBigDecimal(5)); // quantity
					invoiceItem.setTva(rs.getBigDecimal(6));
					invoiceItems.add(invoiceItem);
				}
			}
		} catch (Exception ex) {
			logger.logp(Level.SEVERE, InvoicePersistence.class.getName(), "getInvoiceItems",
					"Error in getting invoice items", ex);
			throw ex;
		}

		return invoiceItems;
	}

	public static int getRecordCount(Connection connection) throws SQLException {
		try (Statement s = connection.createStatement()) {
			String sql = "SELECT COUNT(id) FROM invoices";
			try (ResultSet resultSet = s.executeQuery(sql)) {
				resultSet.next();
				return resultSet.getInt(1);
			}
		} catch (Exception e) {
			logger.logp(Level.SEVERE, InvoicePersistence.class.getName(), "getRecordCount",
					"Error in getting record invoice", e);
			throw e;
		}
	}

	public static int getIdOfInvoice(Connection connection) throws Exception {
		try (Statement s = connection.createStatement()) {
			String sql = "SELECT MAX(id) FROM invoices ";
			try (ResultSet resultSet = s.executeQuery(sql)) {
				resultSet.next();
				return resultSet.getInt(1);
			}
		} catch (Exception e1) {
			logger.logp(Level.SEVERE, InvoicePersistence.class.getName(), "getIdOfInvoice",
					"Error in getting invoice id", e1);
			throw e1;
		}

	}

	public static String getTypeOfInvoice(Integer id) throws Exception {

		Connection connection = Database.getActiveYearConnection();
		try (Statement s = connection.createStatement()) {
			String sql = "SELECT invoicetype FROM invoices WHERE invoice_id = '" + id + "' ";
			try (ResultSet resultSet = s.executeQuery(sql)) {
				resultSet.next();
				String type = resultSet.getString(1);
				if (!resultSet.wasNull()) {
					System.out.println(type);
				}
				return type;
			}
		} catch (Exception e1) {
			logger.logp(Level.SEVERE, InvoicePersistence.class.getName(), "getTypeOfInvoice",
					"Error in getting invoice TypeOfInvoice", e1);
			throw e1;
		}

	}
}
