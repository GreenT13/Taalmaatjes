package com.apon.taalmaatjes.backend.log;

import com.apon.taalmaatjes.Taalmaatjes;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

public class Log {
    private static final Logger logger = Logger.getLogger(Taalmaatjes.class);

    public static void logError(String message) {
        logger.log(Priority.ERROR, message);
    }

    public static void error(String message, Exception e) {
        logger.log(Priority.ERROR, message, e);
    }

    public static void logDebug(String message) {
        logger.log(Priority.DEBUG, message);
    }
}
