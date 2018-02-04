package com.apon.taalmaatjes.backend.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;

public class DateTimeUtil {

    public static Timestamp convertDateToTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }

    /**
     * Create a java.sql.Date object from a date in a string, formatted as 'yyyy-MM-dd'.
     * @param date
     * @return
     */
    public static Date parseDate(String date) {
        try {
            return new Date(new SimpleDateFormat("yyyy-MM-dd").parse(date).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date convertLocalDateToSqlDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return Date.valueOf(date);
    }

    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static Date getCurrentDate() {
        return Date.valueOf(LocalDate.now());
    }

    public static Integer determineAge(Date dateOfBirth) {
        return determineAge(dateOfBirth.toLocalDate());
    }

    public static Integer determineAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, getCurrentDate().toLocalDate()).getYears();
    }

    /**
     * Determine whether dateStart lies between dateStart and dateEnd (where null is +-infinity).
     * @param dateStart
     * @param dateEnd
     * @return
     */
    public static boolean isActiveToday(Date dateStart, Date dateEnd) {
        return isBetween(getCurrentDate(), dateStart, dateEnd);
    }

    /**
     * Determine whether dateStart lies between dateStart and dateEnd (where null is +-infinity).
     * However, when dateEnd equals today, it counts as not active.
     * @param dateStart
     * @param dateEnd
     * @return
     */
    public static boolean isActiveTodayMinusOne(Date dateStart, Date dateEnd) {
        if (!isActiveToday(dateStart, dateEnd)) {
            return false;
        }

        // We already know that we are active, so only check if dateEnd is today.
        if (dateEnd != null && dateEnd.compareTo(DateTimeUtil.getCurrentDate()) == 0) {
            return false;
        }

        return true;
    }

    /**
     * Determine whether date d lies inside the range [r1,r2].
     * @param d Date
     * @param r1 Start date of the range.
     * @param r2 End date of the range.
     * @return
     */
    public static boolean isBetween(Date d, Date r1, Date r2) {
        if (d == null) {
            return false;
        }

        if (r1 != null && r1.compareTo(d) > 0) {
            return false;
        }

        if (r2 != null && r2.compareTo(d) < 0) {
            return false;
        }

        return true;
    }

    /**
     * Return whether the days [d1, d2] has at least one day overlap with [e1, e2].
     * @param d1 Start range 1.
     * @param d2 End range 1.
     * @param e1 Start range 1.
     * @param e2 End range 1.
     * @return boolean
     */
    public static boolean isOverlap(Date d1, Date d2, Date e1, Date e2) {
        if (isBetween(e1, d1, d2)) {
            return true;
        }

        if (isBetween(e2, d1, d2)) {
            return true;
        }

        if (isBetween(d1, e1, e2)) {
            return true;
        }

        if (isBetween(d2, e1, e2)) {
            return true;
        }

        return false;
    }
}
