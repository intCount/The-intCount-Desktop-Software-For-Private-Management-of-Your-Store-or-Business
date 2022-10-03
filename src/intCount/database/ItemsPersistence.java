/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.database;

import java.util.*;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;

import intCount.model.*;
import intCount.utility.Utility;
import javafx.stage.Stage;

import java.util.logging.*;

/**
 *
 * @author
 */
public abstract class ItemsPersistence {

	private final static Logger logger = Logger.getLogger(ItemsPersistence.class.getName());
	@SuppressWarnings("unused")
	private static Stage mainWindow;

	public static List<Item> getItems() throws Exception {

		ArrayList<Item> items = null;
		Connection connection = Database.getActiveYearConnection();
		int rowCount = getNumberOfItems(connection);

		items = new ArrayList<>(rowCount);
		Item item = null;

		try (Statement s = connection.createStatement()) {
			String sql = "SELECT * FROM items ORDER BY name";
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
				System.out.println(items);
			}
		} catch (Exception e) {
			logger.logp(Level.SEVERE, ItemsPersistence.class.getName(), "getItems", "Error in getting the items list",
					e);
			throw e;
		}

		return items;
	}

	public static BigDecimal getHt(String name) {
		Connection connection = Database.getActiveYearConnection();

		Item item = null;
		try (Statement s = connection.createStatement()) {
			String sql = "SELECT * FROM items where name = '" + name + "'";
			try (ResultSet rs = s.executeQuery(sql)) {
				while (rs.next()) {

					item = new Item();

					item.setHt(rs.getBigDecimal(3));

				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return item.getHt();
	}

	public static boolean checkItem(String name) {
		Connection connection = Database.getActiveYearConnection();
        boolean x = true;
		try (Statement s = connection.createStatement()) {
			String sql = "SELECT name from items WHERE name =  '" + name + "'" ;
			try (ResultSet rs = s.executeQuery(sql)) {
				if (!rs.next()) {
					x= false;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return x;

	}

	public static BigDecimal getTva(String name) {
		Connection connection = Database.getActiveYearConnection();

		Item item = null;
		try (Statement s = connection.createStatement()) {
			String sql = "SELECT * FROM items where name = '" + name + "' ";
			try (ResultSet rs = s.executeQuery(sql)) {
				while (rs.next()) {

					item = new Item();

					item.setTva(rs.getBigDecimal(4));

				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return item.getTva();
	}

	public static ArrayList<BigDecimal> getTvas() throws Exception {

		ArrayList<BigDecimal> items = null;
		Connection connection = Database.getActiveYearConnection();
		int rowCount = getNumberOfItems(connection);

		items = new ArrayList<>(rowCount);
		Item item = null;

		try (Statement s = connection.createStatement()) {
			String sql = "SELECT * FROM items";
			try (ResultSet rs = s.executeQuery(sql)) {
				while (rs.next()) {

					item = new Item();

					item.setTva(rs.getBigDecimal(4));
					items.add(item.getTva());
				}

			}

		} catch (Exception e) {
			logger.logp(Level.SEVERE, ItemsPersistence.class.getName(), "getItems", "Error in getting the items list",
					e);
			throw e;
		}
		// System.out.println(items);

		return items;
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

	/**
	 *
	 * @param add
	 *            - The list of items to insert into the database
	 * @param update
	 *            - The list of item records to be updated in the database
	 * @param delete
	 *            - The list of items to be deleted from the database
	 * @return - The list of auto-generated items' identity numbers
	 * @throws SQLException
	 */
	public static int[] saveItems(List<? extends Item> add, List<? extends Item> update, List<? extends Item> delete)
			throws SQLException {

		Connection connection = Database.getActiveYearConnection();

		String insertSQL = "INSERT INTO items VALUES (DEFAULT, ?,?,?,?,?)";
		String updateSQL = "UPDATE items SET name = ?, ht=?, tva=?, stock_init=?, date_entree=? " + "WHERE id = ?";
		String deleteSQL = "DELETE FROM items WHERE id = ?";
		int[] autoIDs = null;

		try {
			connection.setAutoCommit(false);

			if (!add.isEmpty()) {
				try (PreparedStatement psInsert = connection.prepareStatement(insertSQL, new String[] { "ID" })) {
					autoIDs = new int[add.size()];
					int i = 0;
					for (Item c : add) {
						psInsert.setString(1, c.getItemName());
						psInsert.setBigDecimal(2, c.getHt());
						psInsert.setBigDecimal(3, c.getTva());
						psInsert.setInt(4, c.getStockInit());
						psInsert.setDate(5, new Date(Utility.getEpochMilli(c.getDateEntree())));

						psInsert.executeUpdate();
						try (ResultSet rs = psInsert.getGeneratedKeys()) {
							while (rs.next()) {
								autoIDs[i++] = rs.getInt(1);
							}
						}
					} // end of for..each loop

				} // end of try with resources block for the insert statements
			}

			if (!update.isEmpty()) {
				try (PreparedStatement psUpdate = connection.prepareStatement(updateSQL)) {
					for (Item c : update) {
						psUpdate.setString(1, c.getItemName());
						psUpdate.setBigDecimal(2, c.getHt());
						psUpdate.setBigDecimal(3, c.getTva());
						psUpdate.setInt(4, c.getStockInit());
						psUpdate.setDate(5, new Date(Utility.getEpochMilli(c.getDateEntree())));
						psUpdate.setInt(6, c.getItemId());
						psUpdate.addBatch();
					}
					psUpdate.executeBatch();
				}
			}

			if (!delete.isEmpty()) {
				try (PreparedStatement psDelete = connection.prepareStatement(deleteSQL)) {
					for (Item c : delete) {
						psDelete.setInt(1, c.getItemId());
						psDelete.addBatch();
					}
					psDelete.executeBatch();
				}
			}

			connection.commit();

		} catch (Exception e) {
			logger.logp(Level.SEVERE, ItemsPersistence.class.getName(), "saveItems", "Error in saving the items list",
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

		return autoIDs;
	}

}
