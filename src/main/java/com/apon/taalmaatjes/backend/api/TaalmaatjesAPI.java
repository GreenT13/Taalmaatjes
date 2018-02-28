package com.apon.taalmaatjes.backend.api;

import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.database.jooq.Context;
import com.apon.taalmaatjes.backend.database.mydao.VolunteerInstanceMyDao;
import com.apon.taalmaatjes.backend.database.mydao.VolunteerMatchMyDao;
import com.apon.taalmaatjes.backend.log.Log;
import com.apon.taalmaatjes.backend.util.ResultUtil;

import java.sql.SQLException;

public class TaalmaatjesAPI {
    private static TaalmaatjesAPI ourInstance = new TaalmaatjesAPI();
    private final static String VERSION_NUMBER = "v0.5";
    private final static String RELEASE_DATE = "2018-02-28";

    public static TaalmaatjesAPI getInstance() {
        return ourInstance;
    }

    private TaalmaatjesAPI() { }

    /**
     * Get the version number.
     * @return String
     */
    public Result getVersionNumber() {
        return ResultUtil.createOk(VERSION_NUMBER);
    }

    /**
     * Get the release date.
     * @return String
     */
    public Result getReleaseDate() {
        return ResultUtil.createOk(RELEASE_DATE);
    }

    /**
     * Return the number of volunteers that are active today.
     * @return Integer
     */
    public Result countActiveVolunteersToday() {
        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start TaalmaatjesAPI.countActiveVolunteers");

        // All the active volunteers.
        VolunteerInstanceMyDao volunteerInstanceMyDao = new VolunteerInstanceMyDao(context);
        Integer count = volunteerInstanceMyDao.getActiveToday();
        if (count == null) {
            count = 0;
        }

        // Close and return.
        context.close();
        Log.logDebug("End TaalmaatjesAPI.countActiveVolunteers");
        return ResultUtil.createOk(count);
    }

    /**
     * Return the number of matches that are active today.
     * @return Integer
     */
    public Result countActiveMatches() {
        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start TaalmaatjesAPI.countActiveMatches");

        // All the active volunteers.
        VolunteerMatchMyDao volunteerMatchMyDao = new VolunteerMatchMyDao(context);
        Integer count = volunteerMatchMyDao.getMatchToday();
        if (count == null) {
            count = 0;
        }

        // Close and return.
        context.close();
        Log.logDebug("End TaalmaatjesAPI.countActiveMatches");
        return ResultUtil.createOk(count);
    }
}
