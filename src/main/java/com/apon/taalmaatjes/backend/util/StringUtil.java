package com.apon.taalmaatjes.backend.util;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class StringUtil {

    /**
     * Return false if holds:
     * 1. The string is null
     * 2. The string has length 0
     * 3. The string consists of only spaces.
     * @param string String to process
     * @return boolean
     */
    private static boolean isEmpty(String string) {
        return string == null || string.length() == 0 || string.trim().length() == 0;
    }

    public static String getDatabaseString(String string) {
        if (isEmpty(string)) {
            return null;
        }

        return string;
    }

    public static String getOutputString(String string) {
        if (isEmpty(string)) {
            return "<Niet gevuld>";
        }

        return string;
    }

    public static String getOutputString(Boolean b) {
        if (b == null) {
            return getOutputString((String) null);
        } else if (b) {
            return "Ja";
        } else {
            return "Nee";
        }
    }

    public static String getOutputString(Date date) {
        if (date == null) {
            return getOutputString((String) null);
        }

        // Format date to dd-MM-yyyy.
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        return getOutputString(format.format(date));
    }

    public static String convertOutputSexToDb(String sex) {
        switch (sex) {
            case "Man":
                return "M";
            case "Vrouw":
                return "F";
            default:
                return null;
        }
    }

    public static String convertDbSexToOutput(String sex) {
        switch (sex) {
            case "M":
                return "Man";
            case "F":
                return "Vrouw";
            default:
                return "";
        }
    }
}
