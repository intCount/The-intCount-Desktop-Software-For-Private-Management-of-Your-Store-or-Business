/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.database;

import java.util.*;
import java.sql.*;
import java.util.logging.*;

import intCount.model.*;

/**
 *
 * @author 
 */
public abstract class MeasurementUnitPersistence {
    
    private final static Logger logger = 
            Logger.getLogger(MeasurementUnitPersistence.class.getName());

    public static List<MeasurementUnit> getMeasurementUnits() throws Exception {
        ArrayList<MeasurementUnit> units = new ArrayList<>(10);
        
        Connection connection = Database.getActiveYearConnection();
        MeasurementUnit unit = null;
        String abbreviation = null;
        
      try (Statement s = connection.createStatement()) {
            String sql = "SELECT * FROM measurement_units ORDER BY name";
            try (ResultSet rs = s.executeQuery(sql)) {
                while (rs.next()) {
                    unit = new MeasurementUnit();
                    unit.setUnitId(rs.getInt(1));
                    unit.setUnitName(rs.getString(2));
                    abbreviation = rs.getString(3);
                    if (! rs.wasNull()) {
                        unit.setAbbreviation(abbreviation);
                    }
                    units.add(unit);
                }
            }
        } catch (Exception e) {
           logger.logp(Level.SEVERE, MeasurementUnitPersistence.class.getName(),
                    "getMeasurementUnits", 
                    "Error in getting the list of measurment units", e);
            throw e;
        }

        return units;
    }

    /**
     *
     * @param add - The list of units to insert into the database
     * @param update - The list of units to be updated in the
     * database
     * @param delete - The list of units to be deleted from the database
     * @return - The list of auto-generated identity numbers
     * @throws SQLException
     */
    public static int[] saveUnits(List<? extends MeasurementUnit> add,
            List<? extends MeasurementUnit> update,
            List<? extends MeasurementUnit> delete) throws SQLException {

        Connection connection = Database.getActiveYearConnection();

        String insertSQL = "INSERT INTO measurement_units VALUES (DEFAULT, ?, ?)";
        String updateSQL = "UPDATE measurement_units SET name = ?, abbreviation = ? "
                + "WHERE id = ?";
        String deleteSQL = "DELETE FROM measurement_units WHERE id = ?";
        int[] autoIDs = null;

        try {
            connection.setAutoCommit(false);

            if (!add.isEmpty()) {
                try (PreparedStatement psInsert = connection.prepareStatement(insertSQL,
                    new String[]{"ID"})) {
                    autoIDs = new int[add.size()];
                    int i = 0;

                    for (MeasurementUnit unit : add) {
                        fillParameterValues(psInsert, unit, false);
                        psInsert.executeUpdate();
                        try (ResultSet rs = psInsert.getGeneratedKeys()) {
                            if (rs != null) {
                                while (rs.next()) {
                                    autoIDs[i++] = rs.getInt(1);
                                }
                            }
                        }
                    } // end of for..each loop

                } //end of try with resources block for the insert statements
            }
            
            if (! update.isEmpty()) {
                try (PreparedStatement psUpdate = connection.prepareStatement(updateSQL)) {
                    for (MeasurementUnit unit : update) {
                        fillParameterValues(psUpdate, unit, true);
                        psUpdate.addBatch();
                    }
                    psUpdate.executeBatch();
                }
            }

            if (!delete.isEmpty()) {
                try (PreparedStatement psDelete = connection.prepareStatement(deleteSQL)) {
                    for (MeasurementUnit unit : delete) {
                        psDelete.setInt(1, unit.getUnitId());
                        psDelete.addBatch();
                    }
                    psDelete.executeBatch();
                }
            }
          

            connection.commit();

        } catch (Exception e) {
           logger.logp(Level.SEVERE, MeasurementUnitPersistence.class.getName(),
                    "saveUnits", "Error in saving the measurement units list", e);
            connection.rollback();
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
        }

        return autoIDs;
    }

    private static void fillParameterValues(PreparedStatement ps, 
            MeasurementUnit unit,
            boolean isUpdate) throws SQLException {

        ps.setString(1, unit.getUnitName());

        String abbreviation = unit.getAbbreviation();
        if (abbreviation.isEmpty()) {
            ps.setNull(2, Types.VARCHAR);
        } else {
            ps.setString(2, abbreviation);
        }
       
        if (isUpdate) {
            ps.setInt(3, unit.getUnitId());
        }
    }
}
