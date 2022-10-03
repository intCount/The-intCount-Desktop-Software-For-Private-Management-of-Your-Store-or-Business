/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.database;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.logging.*;

import intCount.model.FirmDetails;

/**
 *
 * @author
 */
public abstract class FirmDetailsPersistence {
	private final static Logger logger = Logger.getLogger(FirmDetailsPersistence.class.getName());

	public static void saveData(FirmDetails firmDetails) throws SQLException {
		Connection connection = Database.getActiveYearConnection();
		int recordCount = getRecordCount(connection);
		String sql = null;
		if (recordCount == 0) {
			sql = "INSERT INTO firm_details VALUES (?, ?, ?, ?, ?,?,?,?,?,?,?)";
		} else {
			sql = "UPDATE firm_details SET firm_name = ?, address = ?, "
					+ "phone_numbers = ?, email_address = ?, logo = ?, taxe_professionnel=?, registre_de_commerce=?, bank_account=?, identification_fiscal=?, numero_de_cnss=?, ice=? ";
		}

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, firmDetails.getFirmName());
			ps.setString(2, firmDetails.getAddress());
			String phoneNumbers = firmDetails.getPhoneNumbers();
			if (phoneNumbers == null) {
				ps.setNull(3, java.sql.Types.VARCHAR);
			} else {
				ps.setString(3, phoneNumbers);
			}
			String emailAddress = firmDetails.getEmailAddress();
			if (emailAddress == null) {
				ps.setNull(4, java.sql.Types.VARCHAR);
			} else {
				ps.setString(4, emailAddress);
			}

			byte[] bytes = firmDetails.getLogo();
			if (bytes == null) {
				ps.setNull(5, java.sql.Types.BLOB);
			} else {
				ps.setBlob(5, new ByteArrayInputStream(bytes), bytes.length);
			}

			String taxeProfessionnel = firmDetails.getTaxeProfessionnel();
			if (taxeProfessionnel == null) {
				ps.setNull(6, java.sql.Types.VARCHAR);
			} else {
				ps.setString(6, taxeProfessionnel);
			}

			String registreDeCommerce = firmDetails.getRegistreDeCommerce();
			if (registreDeCommerce == null) {
				ps.setNull(7, java.sql.Types.VARCHAR);
			} else {
				ps.setString(7, registreDeCommerce);
			}

			String bankAccount = firmDetails.getBankAccount();
			if (bankAccount == null) {
				ps.setNull(8, java.sql.Types.VARCHAR);
			} else {
				ps.setString(8, bankAccount);
			}

			String identificationFiscal = firmDetails.getIdentificationFiscal();
			if (identificationFiscal == null) {
				ps.setNull(9, java.sql.Types.VARCHAR);
			} else {
				ps.setString(9, identificationFiscal);
			}

			String numeroDeCnss = firmDetails.getNumeroDeCnss();
			if (numeroDeCnss == null) {
				ps.setNull(10, java.sql.Types.VARCHAR);
			} else {
				ps.setString(10, numeroDeCnss);
			}
			
			String ice = firmDetails.getIceField();
			if (ice == null) {
				ps.setNull(11, java.sql.Types.VARCHAR);
			} else {
				ps.setString(11, ice);
			}

			ps.executeUpdate();
		} catch (Exception e) {
			logger.logp(Level.SEVERE, FirmDetailsPersistence.class.getName(), "saveData",
					"Error in saving firm details", e);
			throw e;
		}

	}

	public static FirmDetails getData() throws SQLException {

		String sql = "SELECT * FROM firm_details";
		Connection connection = Database.getActiveYearConnection();

		FirmDetails firmDetails = null;
		try (Statement s = connection.createStatement()) {

			try (ResultSet result = s.executeQuery(sql)) {
				if (!result.next()) { // record may not exist
					return null;
				}
				firmDetails = new FirmDetails();
				firmDetails.setFirmName(result.getString(1));
				firmDetails.setAddress(result.getString(2));
				String text = result.getString(3); // phone numbers
				if (!result.wasNull()) {
					firmDetails.setPhoneNumbers(text);
				}
				text = result.getString(4); // email address
				if (!result.wasNull()) {
					firmDetails.setEmailAddress(text);
				}

				Blob blob = result.getBlob(5); // firm's logo
				if (!result.wasNull()) {
					int length = (int) blob.length();
					byte[] bytes = blob.getBytes(1L, length);
					firmDetails.setLogo(bytes);
				}
				
				text = result.getString(6); // email address
				if (!result.wasNull()) {
					firmDetails.setTaxeProfessionnel(text);
				}
				text = result.getString(7); // email address
				if (!result.wasNull()) {
					firmDetails.setRegistreDeCommerce(text);
				}
				text = result.getString(8); // email address
				if (!result.wasNull()) {
					firmDetails.setBankAccount(text);;
				}
				text = result.getString(9); // email address
				if (!result.wasNull()) {
					firmDetails.setIdentificationFiscal(text);
				}
				text = result.getString(10); // email address
				if (!result.wasNull()) {
					firmDetails.setNumeroDeCnss(text);
				}
				text = result.getString(11); // email address
				if (!result.wasNull()) {
					firmDetails.setIceField(text);
				}
				
			}
		} catch (Exception e) {
			logger.logp(Level.SEVERE, FirmDetailsPersistence.class.getName(), "getData",
					"Error in getting firm details", e);
			throw e;
		}

		return firmDetails;

	}

	public static int getRecordCount(Connection connection) throws SQLException {
		try (Statement s = connection.createStatement()) {
			String sql = "SELECT COUNT(firm_name) FROM firm_details";
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
