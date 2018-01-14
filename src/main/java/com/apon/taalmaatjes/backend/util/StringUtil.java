package com.apon.taalmaatjes.backend.util;

import java.sql.Date;

public class StringUtil {

    /**
     * Return false if holds:
     * 1. The string is null
     * 2. The string has length 0
     * 3. The string consists of only spaces.
     * @param string
     * @return
     */
    public static boolean isEmpty(String string) {
        if (string == null || string.length() == 0 || string.trim().length() == 0) {
            return true;
        }

        return false;
    }

    public static String getDatabaseString(String string) {
        if (isEmpty(string)) {
            return null;
        }

        return string;
    }

    public static String getOutputString(String string) {
        if (isEmpty(string)) {
            return "Niet gevuld";
        }

        return string;
    }

    public static String getOutputString(Date date) {
        if (date == null) {
            return getOutputString((String) null);
        }

        return getOutputString(date.toString());
    }
}
