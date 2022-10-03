/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.database;

import intCount.Global;

import intCount.model.FinancialYear;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.*;

import org.apache.derby.tools.ij;

/**
 *
 * @author
 */
public abstract class Database {
	private static final String DB_NAME = "db";
	private final static Logger logger = Logger.getLogger(Database.class.getName());
	static PrintSql p = new PrintSql();

	private static Connection activeYearConnection = null;

	public static boolean openAsActiveYear(FinancialYear year) {
		activeYearConnection = getConnection(year, false);
		if (activeYearConnection == null) {
			return false;
		}

		Global.setActiveFinancialYear(year);
		return true;
	}

	public static Connection getActiveYearConnection() {
		return activeYearConnection;
	}

	public static Connection getConnection(FinancialYear year, boolean createDatabase) {
		String path = Global.getExtendedAppDataPath() + File.separator + year.toEpochMillis();
		return getConnection(path, createDatabase);
	}

	public static Connection getConnection(String pathToDatabase) {
		return getConnection(pathToDatabase, false);
	}

	public static Connection getConnection(String pathToDatabase, boolean createDatabase) {

		final String connectionString = "jdbc:derby:" + pathToDatabase + File.separator + DB_NAME;
		Properties properties = getProperties(createDatabase);

		Connection connection = null;

		try {
			connection = DriverManager.getConnection(connectionString, properties);
		} catch (Exception e) {
			logger.logp(Level.SEVERE, Database.class.getName(), "getConnection", "Error in connecting to database", e);
			return null;
		}

		if (!setSchema(connection)) {
			return null;
		}

		return connection;

	}

	private static Properties getProperties(boolean createDatabase) {

		Properties properties = new Properties();
		properties.setProperty("user", "dinesh");
		properties.setProperty("password", "Inferno");

		if (!createDatabase) {
			/*
			 * Property to upgrade the database to the connecting version of
			 * Derby, if it is newer
			 */
			properties.setProperty("upgrade", "true");
		} else {
			// property to create a database
			properties.setProperty("create", "true");
			// set the locale to Indian
			properties.setProperty("territory", "ar_MA");

			/*
			 * Create a case- and accent-insensitive database PRIMARY means
			 * case-insensitive and accent-insensitive SECONDARY means
			 * case-insensitive but accent-sensitive TERTIARY means
			 * case-sensitive and accent-sensitive
			 */
			properties.setProperty("collation", "TERRITORY_BASED:PRIMARY");
		}

		return properties;
	}

	private static boolean setSchema(Connection connection) {

		final String sql = "set schema APP";
		try (Statement s = connection.createStatement()) {
			s.execute(sql);
		} catch (SQLException ex) {
			logger.logp(Level.SEVERE, Database.class.getName(), "setSchema", "Error in setting the default schema", ex);
			return false;
		}

		return true;

	}

	/**
	 * Shuts down the database
	 */
	public static boolean shutDown(String pathToDatabase) {

		String connectionString = "jdbc:derby:";
		if (pathToDatabase != null) {
			connectionString += pathToDatabase + File.separator + DB_NAME;
		}

		Properties properties = getProperties(false);
		properties.remove("upgrade");
		properties.put("shutdown", "true");

		try {
			DriverManager.getConnection(connectionString, properties);
		} catch (Exception e) {
			if (e instanceof SQLException) {
				String sqlState = ((SQLException) e).getSQLState();
				/*
				 * SQL State XJ015 is returned when the Derby Engine is
				 * successfully shutdown, while SQL State 08006 is returned with
				 * an individual database is successfully shutdown.
				 */
				if (!(sqlState.equalsIgnoreCase("XJ015") || sqlState.equalsIgnoreCase("08006"))) {
					// database or the Derby Engine was not shut down properly
					logger.logp(Level.SEVERE, Database.class.getName(), "shutDown",
							"Error in shutting down the Derby database", e);
					return false;
				}
			}
		}

		return true;
	}

	public static boolean shutDownActiveYearDatabase() {

		try {
			if (activeYearConnection != null) {
				activeYearConnection.close();
			}
		} catch (Exception e) {
			logger.logp(Level.SEVERE, Database.class.getName(), "shutDownActiveYearDatabase",
					"Error in closing the connection to the active financial year", e);
			return false;
		}

		activeYearConnection = null;

		if (Global.getActiveFinancialYear() != null) {
			final String pathToDatabase = Global.getExtendedAppDataPath() + File.separator
					+ Global.getActiveFinancialYear().toEpochMillis();
			Global.setActiveFinancialYear(null);
			return shutDown(pathToDatabase);
		}

		return true;

	}

	public static boolean closeConnection(final Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (Exception e) {
			logger.logp(Level.SEVERE, Database.class.getName(), "closeConnection",
					"Error in closing the connection to the database", e);
			return false;
		}

		return true;
	}

	public static boolean createDatabaseSchema(final Connection connection) {
		@SuppressWarnings("unused")
		PrintWriter out = null;
		InputStream sql = null;

		// String s = System.getProperty("user.dir").concat("/done.sql");
/*
		FileWriter writer = null;
		try {
			writer = new FileWriter(System.getProperty("user.dir").concat("/src/resources/sql/test.sql") ,false);
			//writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try (PrintWriter printWriter = new PrintWriter(writer)) {

			printWriter.write(p.readencryptedFile());

		}
	*/

		// path to the resource included in the application jar file
		final String resourcePath = "/resources/sql/test.sql";
		// get the default encoding of the JVM
		final String fileEncoding = "UTF-8";

		try {
			sql = new BufferedInputStream(Database.class.getResourceAsStream(resourcePath));

			/*
			 * invoke the command to run DDL statements contained in the
			 * resource stream against the database connected.
			 */
			try {
				int errorsEncountered = ij.runScript(connection, sql, fileEncoding, System.out, null);

				if (errorsEncountered > 0) {
					final String str = errorsEncountered + " errors encountered while " + "running the sql script";
					throw new RuntimeException(str);
				}
			} catch (Exception e) {
				logger.logp(Level.SEVERE, Database.class.getName(), "createDatabaseSchema",
						"Error in running the sql script", e);
				return false;
			}
			sql.close();

		} catch (Exception ex) {
			logger.logp(Level.SEVERE, Database.class.getName(), "createDatabaseSchema",
					"Error in accessing the SQL file resource", ex);
			return false;
		}

		return true;

	}

	public static boolean createDatabase(final FinancialYear year) {

		try (Connection connection = getConnection(year, true)) {
			if (connection == null) {
				return false;
			}

			return createDatabaseSchema(connection);

		} catch (SQLException ex) {
			return false;
		}

	}

	public static boolean transferData(final FinancialYear newYear, final FinancialYear existingYear) {

		try (Connection newDB = getConnection(newYear, false)) {
			if (newDB == null) {
				return false;
			}

			try (Connection existingDB = getConnection(existingYear, false)) {
				if (existingDB == null) {
					return false;
				}

				transferFirmDetails(newDB, existingDB);

				transferItems(newDB, existingDB);
				resetAutoIDColumn(newDB, "ITEMS", "ID");

				transferMeasurementUnits(newDB, existingDB);
				resetAutoIDColumn(newDB, "MEASUREMENT_UNITS", "ID");

				transferCustomers(newDB, existingDB);
			}

		} catch (Exception ex) {
			logger.logp(Level.SEVERE, Database.class.getName(), "transferData",
					"Error in inter-database data transfer.", ex);
			return false;
		}
		return true;
	}

	private static void transferFirmDetails(final Connection newDB, final Connection existingDB) throws Exception {

		final String tempFile = Global.getTempDirectoryPath() + File.separator + "firm_details.dat";
		Files.deleteIfExists(Paths.get(tempFile));

		// command to export the table contents to a disk file
		StringBuilder sb = new StringBuilder("{CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE(").append("'APP', 'FIRM_DETAILS', '")
				.append(tempFile).append("', '~', null, null)}");

		try (CallableStatement cs = existingDB.prepareCall(sb.toString())) {
			// export the data to a disk file
			cs.execute();
		}

		sb = new StringBuilder("{CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE(").append("'APP', 'FIRM_DETAILS', '")
				.append(tempFile).append("', '~', null, null, 1)}");

		try (CallableStatement cs = newDB.prepareCall(sb.toString())) {
			// import the data from a disk file
			cs.execute();
		}

	}

	private static void transferItems(final Connection newDB, final Connection existingDB) throws Exception {

		final String tempFile = Global.getTempDirectoryPath() + File.separator + "item_details.dat";
		Files.deleteIfExists(Paths.get(tempFile));

		// command to export the SELECt query contents to a disk file
		StringBuilder sb = new StringBuilder("{CALL SYSCS_UTIL.SYSCS_EXPORT_QUERY(")
				.append("'SELECT NAME FROM APP.ITEMS', '").append(tempFile).append("', '~', null, null)}");

		try (CallableStatement cs = existingDB.prepareCall(sb.toString())) {
			// export the data to a disk file
			cs.execute();
		}

		sb = new StringBuilder("{CALL SYSCS_UTIL.SYSCS_IMPORT_DATA(").append("'APP', 'ITEMS', 'NAME', '1', '")
				.append(tempFile).append("', '~', null, null, 1)}");

		try (CallableStatement cs = newDB.prepareCall(sb.toString())) {
			// import the data from a disk file
			cs.execute();
		}

	}

	private static void resetAutoIDColumn(final Connection connection, final String tableName,
			final String columnToAlter) throws Exception {

		try (Statement s = connection.createStatement()) {

			String sql = "SELECT MAX(" + columnToAlter + ") FROM " + tableName;
			int maxID = 0;

			try (ResultSet rs = s.executeQuery(sql)) {
				rs.next();
				maxID = rs.getInt(1);
			}

			sql = "ALTER TABLE " + tableName + " ALTER COLUMN " + columnToAlter + " RESTART WITH " + (maxID + 1);
			s.executeUpdate(sql);
		}
	}

	private static void transferMeasurementUnits(final Connection newDB, final Connection existingDB) throws Exception {

		final String tempFile = Global.getTempDirectoryPath() + File.separator + "measurement_units.dat";
		Files.deleteIfExists(Paths.get(tempFile));

		// command to export the SELECt query contents to a disk file
		StringBuilder sb = new StringBuilder("{CALL SYSCS_UTIL.SYSCS_EXPORT_QUERY(")
				.append("'SELECT NAME, ABBREVIATION FROM APP.MEASUREMENT_UNITS', '").append(tempFile)
				.append("', '~', null, null)}");

		try (CallableStatement cs = existingDB.prepareCall(sb.toString())) {
			// export the data to a disk file
			cs.execute();
		}

		sb = new StringBuilder("{CALL SYSCS_UTIL.SYSCS_IMPORT_DATA(")
				.append("'APP', 'MEASUREMENT_UNITS', 'NAME,ABBREVIATION', '1,2', '").append(tempFile)
				.append("', '~', null, null, 1)}");

		try (CallableStatement cs = newDB.prepareCall(sb.toString())) {
			// import the data from a disk file
			cs.execute();
		}

	}

	private static void transferCustomers(final Connection newDB, final Connection existingDB) throws Exception {

		final String querySQL = "SELECT name, city, phone_numbers, " + "customer_balance(cust_id, null) FROM customers";
		final String insertSQL = "INSERT INTO customers values (DEFAULT, " + "?, ?, ?, ?, ?)";

		String val = null;
		BigDecimal balance = null;
		int signNum = 0;

		try (Statement s = existingDB.createStatement()) {
			try (ResultSet rs = s.executeQuery(querySQL)) {
				try (PreparedStatement ps = newDB.prepareStatement(insertSQL)) {
					while (rs.next()) {
						ps.clearParameters();

						ps.setString(1, rs.getString(1)); // customer name

						val = rs.getString(2);// city
						if (!rs.wasNull()) {
							ps.setString(2, val);
						} else {
							ps.setNull(2, Types.VARCHAR);
						}

						val = rs.getString(3); // phone numbers
						if (!rs.wasNull()) {
							ps.setString(3, val);
						} else {
							ps.setNull(3, Types.VARCHAR);
						}

						balance = rs.getBigDecimal(4); // customer's account
														// balance
						ps.setBigDecimal(4, balance.abs());

						signNum = balance.signum();
						if (signNum == 0) {
							ps.setNull(5, Types.DECIMAL);
						} else if (signNum == -1) {
							ps.setString(5, "d");
						} else {
							ps.setString(5, "c");
						}

						ps.addBatch();
					}
					ps.executeBatch();
				}
			}
		}
	}

	public static boolean backupDatabase(final FinancialYear year, final String path) {

		Connection connection = null;
		final FinancialYear activeYear = Global.getActiveFinancialYear();
		if (activeYear != null && activeYear.equals(year)) {
			connection = getActiveYearConnection();
		} else {
			connection = getConnection(year, false);
			if (connection == null) {
				return false;
			}
		}

		final String sql = "{CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(?)}";

		try (CallableStatement cs = connection.prepareCall(sql)) {
			cs.setString(1, path);
			cs.execute();
		} catch (Exception ex) {
			logger.logp(Level.SEVERE, Database.class.getName(), "backupDatabase", "Error in backing up the database",
					ex);
			return false;
		} finally {
			if (activeYear == null || !activeYear.equals(year)) {
				closeConnection(connection);
			}
		}

		return true;
	}

	public static boolean restoreDatabase(final FinancialYear year, final String restoreFromPath) {

		final String restoreToPath = Global.getExtendedAppDataPath() + File.separator + year.toEpochMillis()
				+ File.separator + DB_NAME;

		final String sourcePath = restoreFromPath + File.separator + year.toEpochMillis() + File.separator + DB_NAME;

		final String connectionString = "jdbc:derby:" + restoreToPath + ";restoreFrom=" + sourcePath;

		final Properties properties = getProperties(false);

		Connection connection = null;

		try {
			connection = DriverManager.getConnection(connectionString, properties);
		} catch (Exception e) {
			logger.logp(Level.SEVERE, Database.class.getName(), "restoreDatabase",
					"Error in restoring the database for the year " + year.toString(), e);
			return false;
		} finally {
			if (connection != null) {
				closeConnection(connection);
			}
		}

		return true;
	}

	/**
	 * 
	 * @param connection
	 *            - An active database connection
	 * @return - The list of names of the user defined tables in the database
	 *         associated with the connection. If an error is encountered then a
	 *         null value is returned instead.
	 */
	private static List<String> getTableNames(final Connection connection) {

		final String sql = // 'T' means user defined tables
				"SELECT TABLENAME FROM SYS.SYSTABLES WHERE CAST(TABLETYPE AS VARCHAR(128)) ='T'";
		final List<String> tableNames = new ArrayList<>(10);

		try (Statement s = connection.createStatement()) {
			try (ResultSet rs = s.executeQuery(sql)) {
				while (rs.next()) {
					tableNames.add(rs.getString(1)); // table name
				}
			}
		} catch (Exception e) {
			logger.logp(Level.SEVERE, Database.class.getName(), "getTableNames",
					"An error occurred in fetching the table names from the database", e);
			return null;
		}

		return tableNames;
	}

	/**
	 * Compacts the currently active database
	 * 
	 * @return
	 */
	public static boolean compactDatabase() {
		final Connection connection = getActiveYearConnection();

		final List<String> tableNames = getTableNames(connection);
		if (tableNames == null) {
			return false;
		}

		final String sql = "{CALL SYSCS_UTIL.SYSCS_COMPRESS_TABLE(?, ?, ?)}";

		try (CallableStatement statement = connection.prepareCall(sql)) {

			for (String tableName : tableNames) {
				statement.clearParameters();
				statement.setString(1, "APP");
				statement.setString(2, tableName);
				statement.setShort(3, (short) 0); // 0 means concurrent mode

				statement.execute();
			}

		} catch (Exception e) {
			logger.logp(Level.SEVERE, Database.class.getName(), "compactDatabase", "Error in compacting the database",
					e);
			return false;
		}

		return true;
	}
}
