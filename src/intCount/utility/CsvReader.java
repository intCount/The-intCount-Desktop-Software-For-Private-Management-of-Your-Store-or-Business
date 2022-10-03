package intCount.utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CsvReader {

	private Connection connection;
	@SuppressWarnings("unused")
	private char seprator;

	public CsvReader(Connection connection) {
		this.connection = connection;
		// Set default separator
		this.seprator = ',';

	}

	@SuppressWarnings("null")
	public void CsvR() {

		String sql = " INSERT INTO ITEMS(NAME,HT,TVA,STOCK_INIT,DATE_ENTREE) VALUES(?,?,?,?,?) ";

		try {
			BufferedReader bReader = new BufferedReader(
					new FileReader(System.getProperty("user.dir").concat("/intCount/items.csv")));
			String line = "";
			try {
				while ((line = bReader.readLine()) != null) {
					try {

						if (line != null) {
							String[] array = line.split(",");
							
							System.out.println(array[0]);
							System.out.println(array[1]);
							for (String result : array) {
								System.out.println(result);

								Connection con = null;
								con = this.connection;
								con.setAutoCommit(false);
								PreparedStatement ps = con.prepareStatement(sql);

								try {
									ps.setString(1, array[0]);
									ps.setBigDecimal(2, new BigDecimal(array[1]));
									System.out.println(new BigDecimal(array[1]));
									ps.setBigDecimal(3, new BigDecimal(array[2]));
									ps.setInt(4, Integer.parseInt(array[3]));
									ps.setDate(5, Date.valueOf(array[4]));
									ps.executeUpdate();
									ps.close();
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								// Assuming that your line from file after split
								// will folllow that sequence

							}
						}
					} finally {
						if (bReader == null) {
							bReader.close();
						}
					}
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}

	}
}
