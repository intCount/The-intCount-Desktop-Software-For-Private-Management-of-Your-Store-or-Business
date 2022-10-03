package intCount.database;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CountPersistance {

	private final static Logger logger = Logger.getLogger(FirmDetailsPersistence.class.getName());

	public static void saveData(int firmCount) throws SQLException {
		Connection connection = Database.getActiveYearConnection();
		int recordCount = getRecordCount(connection);
		String sql = null;
		if (recordCount == 0) {
			sql = "INSERT INTO firm_count VALUES (?)";
		} else {
			sql = "UPDATE firm_count SET firm_count = ?";
		}

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, firmCount);

			ps.executeUpdate();
		} catch (Exception e) {
			logger.logp(Level.SEVERE, FirmDetailsPersistence.class.getName(), "saveData", "Error in saving firm count",
					e);
			throw e;
		}

	}

	public static int getData() throws SQLException {

		String sql = "SELECT * FROM firm_count";
		Connection connection = Database.getActiveYearConnection();

		int firmCount = 0;
		try (Statement s = connection.createStatement()) {

			try (ResultSet result = s.executeQuery(sql)) {
				if (!result.next()) { // record may not exist
					return 0;
				}

				firmCount = result.getInt(1);

			}
		} catch (Exception e) {
			logger.logp(Level.SEVERE, FirmDetailsPersistence.class.getName(), "getData",
					"Error in getting firm count", e);
			throw e;
		}

		return firmCount;

	}

	private static int getRecordCount(Connection connection) throws SQLException {
		try (Statement s = connection.createStatement()) {
			String sql = "SELECT COUNT(firm_count) FROM firm_count";
			try (ResultSet resultSet = s.executeQuery(sql)) {
				resultSet.next();
				return resultSet.getInt(1);
			}
		} catch (Exception e) {
			logger.logp(Level.SEVERE, FirmDetailsPersistence.class.getName(), "getRecordCount",
					"Error in getting record count", e);
			throw e;
		}
	}

}
