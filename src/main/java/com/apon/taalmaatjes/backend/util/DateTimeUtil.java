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
        Date currentDate = getCurrentDate();
        if (dateStart != null && dateStart.compareTo(currentDate) > 0) {
            return false;
        }

        if (dateEnd != null && dateEnd.compareTo(currentDate) < 0) {
            return false;
        }

        return true;
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
}
