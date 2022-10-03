package intCount.utility;

import java.io.FileNotFoundException;



import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

/**
 * 
 * @author 
 * 
 */
public class CSVLoader {

	private static final String SQL_INSERT = "INSERT INTO ${table}(${keys}) VALUES(${values})";
	private static final String TABLE_REGEX = "\\$\\{table\\}";
	private static final String KEYS_REGEX = "\\$\\{keys\\}";
	private static final String VALUES_REGEX = "\\$\\{values\\}";

	private Connection connection;
	private char seprator;
	// ArrayList<Item> items = null;

	/**
	 * Public constructor to build CSVLoader object with Connection details. The
	 * connection is closed on success or failure.
	 * 
	 * @param connection
	 */
	public CSVLoader(Connection connection) {
		this.connection = connection;
		// Set default separator
		this.seprator = ',';
	}

	/**
	 * Parse CSV file using OpenCSV library and load in given database table.
	 * 
	 * @param csvFile
	 *            Input CSV file
	 * @param tableName
	 *            Database table name to import data
	 * @param truncateBeforeLoad
	 *            Truncate the table before inserting new records.
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public void loadCSV(String csvFile, String tableName, boolean truncateBeforeLoad) throws Exception {

		CSVReader csvReader = null;
		if (null == this.connection) {
			throw new Exception("Pas une connexion valide.");
		}
		try {

			csvReader = new CSVReader(new FileReader(csvFile), this.seprator);

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Une erreur s'est produite lors de l'exécution du fichier. " + e.getMessage());
		}

		String[] headerRow = csvReader.readNext();

		if (null == headerRow) {
			throw new FileNotFoundException(
					"Aucune colonne définie dans le fichier CSV donné.\" + \"Veuillez vérifier le format du fichier CSV.");
		}

		String questionmarks = StringUtils.repeat("?,", headerRow.length);
		questionmarks = (String) questionmarks.subSequence(0, questionmarks.length() - 1);

		String query = SQL_INSERT.replaceFirst(TABLE_REGEX, tableName);
		query = query.replaceFirst(KEYS_REGEX, StringUtils.join(headerRow, ","));
		query = query.replaceFirst(VALUES_REGEX, questionmarks);

		System.out.println("Query: " + query);

		String[] nextLine;
		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = this.connection;
			con.setAutoCommit(false);
			ps = con.prepareStatement(query);

				//delete data from table before loading csv
				con.createStatement().execute("DELETE FROM " + tableName);
			
			final int batchSize = 1000;
			int count = 0;
			Date date = null;
			while ((nextLine = csvReader.readNext()) != null) {

				if (null != nextLine) {
					int index = 1;
					for (String string : nextLine) {
						date = DateUtil.convertToDate(string);
						if (null != date) {
							ps.setDate(index++, new java.sql.Date(date.getTime()));
						} else {
							ps.setString(index++, string);
						}
					}
					ps.addBatch();
				}
				if (++count % batchSize == 0) {
					ps.executeBatch();
				}
			}
			ps.executeBatch(); // insert remaining records
			con.commit();
			// saveItems(items);
		} catch (Exception e) {
			con.rollback();
			e.printStackTrace();
			throw new Exception("An error occurred while loading data from file to database." + e.getMessage());
		} finally {
			if (null != ps)
				ps.close();
			if (null != con)
				con.setAutoCommit(true);

			csvReader.close();
		}
	}

	public char getSeprator() {
		return seprator;
	}

	public void setSeprator(char seprator) {
		this.seprator = seprator;
	}
}
