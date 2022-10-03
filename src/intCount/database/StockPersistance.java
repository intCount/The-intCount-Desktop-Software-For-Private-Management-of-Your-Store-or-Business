package intCount.database;

import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import intCount.model.Stock;

public class StockPersistance {

	private final static Logger logger = Logger.getLogger(StockPersistance.class.getName());

	public static List<Stock> getStock() throws Exception {
		Connection connection = Database.getActiveYearConnection();
		int rowCount = getNumberOfItems(connection);
		System.out.println(rowCount);
		ArrayList<Stock> stockData = new ArrayList<>(rowCount);
		String sql = "SELECT name, MAX(dateEntree), MAX(stockInit), sum(case when type ='facture '  then quantity else 0-quantity end ) quantityFact, MAX(date) from "
				+ "((SELECT items.name as name, items.date_entree as dateEntree, items.stock_init as stockInit, quantity, date, type, items.id as id from "
				+ "(SELECT invoices.invoicedate as date, invoices.invoicetype as type, invoice_items.invoice_id as invId, invoice_items.item_id as itemId, invoice_items.quantity as quantity from invoices "
				+ "INNER JOIN invoice_items ON invoices.id= invoice_items.invoice_id ) As inv"
				+ " INNER JOIN items ON inv.itemId= items.Id) As table2) group by name";

		Stock stock = null;

		try (Statement s = connection.createStatement()) {
			try (ResultSet rs = s.executeQuery(sql)) {
				while (rs.next()) {

					stock = new Stock();

					stock.setItemName(rs.getString(1));
					stock.setDateEntree(rs.getDate(2).toLocalDate());
					stock.setStockInit(rs.getInt(3));
					stock.setStockEnMvt(rs.getInt(4));
					stock.setDateSortie(rs.getDate(5).toLocalDate());
					stockData.add(stock);
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
