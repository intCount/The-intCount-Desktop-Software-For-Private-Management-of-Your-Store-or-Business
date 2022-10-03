package intCount.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import intCount.model.Item;

public class CsvPersistence {

	private final static Logger logger = Logger.getLogger(ItemsPersistence.class.getName());

	public static void joinCsvItem() {
		Connection connection = Database.getActiveYearConnection();
		try {

			Statement s = connection.createStatement();
			String sql = "Insert into ITEMS (NAME,HT,TVA,STOCK_INIT,DATE_ENTREE) Select NAME, HT,TVA,STOCK_INIT, DATE_ENTREE from CSV EXCEPT Select NAME,HT,TVA,STOCK_INIT,DATE_ENTREE from ITEMS  ";

			s.executeUpdate(sql);

		} catch (Exception e) {
			logger.logp(Level.SEVERE, CsvPersistence.class.getName(), "getItems", "Error in getting the items list", e);
		}

	}

	public static void selectCsv() {

		ArrayList<Item> items = null;
		Connection connection = Database.getActiveYearConnection();
		int rowCount = 0;
		try {
			rowCount = getNumberOfItems(connection);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		items = new ArrayList<>(rowCount);
		Item item = null;
		try {

			Statement s = connection.createStatement();
			String sql = "select * from Csv ";

			try (ResultSet rs = s.executeQuery(sql)) {
				while (rs.next()) {
					item = new Item();
					item.setItemId(rs.getInt(1));
					item.setItemName(rs.getString(2));
					item.setHt(rs.getBigDecimal(3));
					item.setTva(rs.getBigDecimal(4));
					item.setStockInit(rs.getInt(5));
					item.setDateEntree(rs.getDate(6).toLocalDate());

					items.add(item);
				}
				System.out.println("items from Csv " + items);
			}
		} catch (Exception e) {
			logger.logp(Level.SEVERE, CsvPersistence.class.getName(), "getItems", "Error in getting the items list", e);
		}

	}

	public static int getNumberOfItems(Connection connection) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			String sql = "SELECT count(id) FROM ITEMS";
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