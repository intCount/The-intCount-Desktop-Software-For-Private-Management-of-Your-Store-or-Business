/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.model;

import java.time.Instant;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.logging.*;

/**
 * @author
 */
public class FinancialYear implements Comparable<FinancialYear> {
    private LocalDate startDate;
    private LocalDate endDate;

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private final static Logger logger = Logger.getLogger(FinancialYear.class.getName());

    public FinancialYear(LocalDate startDate, LocalDate endDate) {
        if (endDate.isEqual(startDate) || endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("La date de fin doit être postérieure à la date de début !");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return startDate.format(formatter) + " \u2014 " + endDate.format(formatter);
    }

    @Override
    public int hashCode() {
        return startDate.hashCode() + endDate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FinancialYear other = (FinancialYear) obj;
        if (!startDate.isEqual(other.getStartDate())) {
            return false;
        }
        if (!endDate.isEqual(other.getEndDate())) {
            return false;
        }
        return true;
    }

    public boolean isYearOverLapped(FinancialYear year) {
        // LocalDate otherStartDate = year.getStartDate();
        // LocalDate otherEndDate = year.getEndDate();

        if (year.getStartDate().getYear() == startDate.getYear() || year.getEndDate().getYear() == endDate.getYear()) {
            return true;
        }

        return false;
    }

    public static FinancialYear getOverlappingYear(List<FinancialYear> years, FinancialYear year) {

        for (FinancialYear fy : years) {
            if (fy.isYearOverLapped(year)) {
                return fy;
            }
        }

        return null;
    }

    /**
     * @param other - The specified financial year to compare against
     * @return If this financial year comes chronologically before the specified
     * financial year
     */
    public boolean isBefore(FinancialYear other) {
        return startDate.isBefore(other.getStartDate());
    }

    public static FinancialYear parse(String encryptedName) {
        String[] nameParts = encryptedName.split("-");
        if (nameParts.length != 2)
            return null;

        LocalDate[] dates = new LocalDate[2];
        Long millis = null;

        for (int i = 0; i < 2; i++) {
            // get milliseconds since Epoch
            try {
                millis = Long.parseUnsignedLong(nameParts[i]);
            } catch (NumberFormatException e) {
                logger.logp(Level.SEVERE, FinancialYear.class.getName(), "parse",
                        "Erreur lors de l'analyse d'une chaîne pour un nom d'exercice", e);
                return null;
            }

            // convert milliseconds to a LocalDate object
            dates[i] = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Kolkata")).toLocalDate();
        }

        return new FinancialYear(dates[0], dates[1]);
    }

    /**
     * @return - The number of milliseconds since Epoch (01-Jan-1970 Midnight)
     */
    public String toEpochMillis() {

        long startMillis = startDate.atTime(LocalTime.MIDNIGHT).atZone(ZoneId.of("Asia/Kolkata")).toInstant()
                .toEpochMilli();

        long endMillis = endDate.atTime(LocalTime.MIDNIGHT).atZone(ZoneId.of("Asia/Kolkata")).toInstant()
                .toEpochMilli();

        return (String.valueOf(startMillis) + "-" + String.valueOf(endMillis));
    }

    @Override
    public int compareTo(FinancialYear o) {
        if (this.isBefore(o)) {
            return -1;
        } else if (o.isBefore(this)) {
            return 1;
        } else {
            return 0;
        }
    }

    public static void sort(final List<FinancialYear> years, boolean sortAscending) {

        if (sortAscending) {
            Collections.<FinancialYear>sort(years);
        } else {
            Collections.<FinancialYear>sort(years, Collections.<FinancialYear>reverseOrder());
        }
    }

}
