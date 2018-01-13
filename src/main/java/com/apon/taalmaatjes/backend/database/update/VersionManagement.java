package com.apon.taalmaatjes.backend.database.update;

import com.apon.taalmaatjes.backend.database.jooq.Context;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.ScriptlogPojo;
import com.apon.taalmaatjes.backend.database.mydao.ScriptlogMyDao;
import com.apon.taalmaatjes.backend.log.Log;
import com.apon.taalmaatjes.backend.util.DateTimeUtil;
import org.jooq.Query;
import org.jooq.exception.DataAccessException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class will execute the needed SQL-scripts to make sure the database is up-to-date.
 */
public class VersionManagement {
    private static VersionManagement ourInstance = new VersionManagement();

    public static VersionManagement getInstance() {
        return ourInstance;
    }

    private VersionManagement() { }

    private final static List<String> ALL_SCRIPT_NAMES = Arrays.asList(
            // Remove this testscript when done testing!
            "testscript");
    private final static String CREATE_TABLE_SCRIPT = "20180105_CreateTables";
    private final static String LOCATION = "/update_scripts";

    // Dao of the scriptlog that will be used often throughout the class.
    private static ScriptlogMyDao scriptlogMyDao;
    private static Context context;

    /**
     * Execute the following steps:
     * 1. If the table Scriptlog does not exist, we run CREATE_TABLE_SCRIPT.
     * 2. Run any SQL scrit from ALL_SCRIPT_NAMES that did not run yet.
     * @param context
     * @return {@code true} if successful, else {@code false}.
     */
    public boolean runUpdates(Context context) {
        // Initialize variables
        scriptlogMyDao = new ScriptlogMyDao(context.getConfiguration());
        VersionManagement.context = context;

        // Huge try-finally block to make sure the connection is rollbacked and closed.
        try {
            // If the table Scriptlog does not exist, we have to run the create tables script.
            List<ScriptlogPojo> runScripts = new ArrayList<ScriptlogPojo>();

            // Retrieve all scripts that have been run.
            try {
                runScripts = scriptlogMyDao.findAll();
            } catch (DataAccessException e) {
                // Table could not be found.
                if (!runFile(CREATE_TABLE_SCRIPT)) {
                    return false;
                }
            }

            ArrayList<String> scriptsToRun = new ArrayList<String>();

            // Use ints to make sure that the array is looped through in order.
            for (String scriptName : ALL_SCRIPT_NAMES) {
                // Verify that the item is not in the database.
                if (!containsScript(scriptName, runScripts)) {
                    // Add script to list
                    scriptsToRun.add(scriptName);
                }
            }

            // Run all scripts:
            for (String scriptName : scriptsToRun) {
                if (!runScript(scriptName)) {
                    return false;
                }
            }

            context.commit();
        } finally {
            Log.logDebug("Finally, doing some rollbacks.");
            // Always rollback, in case of errors.
            context.rollback();
        }

        return true;
    }

    /**
     * Check whether the list contains a scriptlog with the same scriptName as item.
     * @param item  The name of the scriptlog.
     * @param list  List of ScriptlogPojo's.
     * @return
     */
    private boolean containsScript(String item, List<ScriptlogPojo> list) {
        for (ScriptlogPojo scriptlog : list) {
            if (scriptlog.getScriptname().equals(item)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return the string to the location of a script.
     * @param scriptName
     * @return
     */
    private String getLocation(String scriptName) {
        return LOCATION + "/" + scriptName + ".sql";
    }

    /**
     * Execute the following three steps:
     * 1. Insert a new Scriptlog indicating we start the script.
     * 2. Execute the script with name scriptName, using #runFile(..).
     * 3. Update the Scriptlog indicating we finished the script.
     * @param scriptName The name of the script to run.
     * @return {@code true} if all steps succeed, else {@code false}.
     */
    private boolean runScript(String scriptName) {
        // First add line to scriptlog
        ScriptlogPojo scriptlog = new ScriptlogPojo();
        scriptlog.setScriptname(scriptName);
        scriptlog.setTsstarted(DateTimeUtil.getCurrentTimestamp());
        scriptlog.setIscompleted(false);

        scriptlogMyDao.insert(scriptlog);

        if (!runFile(scriptName)) {
            return false;
        }

        scriptlog.setIscompleted(true);
        scriptlog.setTsfinished(DateTimeUtil.getCurrentTimestamp());
        scriptlogMyDao.update(scriptlog);
        return true;
    }

    /**
     * Execute the SQL script with name scriptName.
     * @param scriptName Name of the script.
     * @return {@code true} if all steps succeed, else {@code false}.
     */
    private boolean runFile(String scriptName) {
        Log.logDebug("Going to run script: " + scriptName);

        try {
            Log.logDebug("Reading file.");
            readFile(getLocation(scriptName));
            Log.logDebug("End reading file.");



            context.getCreate().execute(readFile(getLocation(scriptName)));
        } catch (IOException e) {
            e.printStackTrace();
            Log.logError("Could not find script " + scriptName);
            return false;
        }

        Log.logDebug("Finished running script: " + scriptName);

        return true;
    }

    /**
     * Get the content of a file as a String.
     * @param path Path to the file.
     * @return Content of the file.
     * @throws IOException
     */
    private String readFile(String path) throws IOException {
        InputStream in = getClass().getResourceAsStream(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        // Add "\n" because otherwise jOOQ will not recognize it as several queries.
        return reader.lines().collect(Collectors.joining("\n"));
    }

}
