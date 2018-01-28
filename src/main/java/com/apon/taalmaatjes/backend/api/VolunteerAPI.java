package com.apon.taalmaatjes.backend.api;

import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.StudentReturn;
import com.apon.taalmaatjes.backend.api.returns.VolunteerReturn;
import com.apon.taalmaatjes.backend.api.returns.mapper.VolunteerMapper;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteerPojo;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteerinstancePojo;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteermatchPojo;
import com.apon.taalmaatjes.backend.database.jooq.Context;
import com.apon.taalmaatjes.backend.database.mydao.StudentMyDao;
import com.apon.taalmaatjes.backend.database.mydao.VolunteerInstanceMyDao;
import com.apon.taalmaatjes.backend.database.mydao.VolunteerMatchMyDao;
import com.apon.taalmaatjes.backend.database.mydao.VolunteerMyDao;
import com.apon.taalmaatjes.backend.log.Log;
import com.apon.taalmaatjes.backend.util.DateTimeUtil;
import com.apon.taalmaatjes.backend.util.ResultUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VolunteerAPI {
    private static VolunteerAPI ourInstance = new VolunteerAPI();

    public static VolunteerAPI getInstance() {
        return ourInstance;
    }

    private VolunteerAPI() { }

    // So I don't need to define it every time.
    Context context;

    /**
     * Get a volunteer based on the external identifier.
     * @param externalIdentifier
     * @return VolunteerReturn
     */
    public Result get(String externalIdentifier) {
        try {
            context = new Context();
        } catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }

        Log.logDebug("Start VolunteerApi.get for externalIdentifier " + externalIdentifier);

        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);

        // First check if the externalIdentifier is valid.
        Integer volunteerId = volunteerMyDao.getIdFromExtId(externalIdentifier);
        if (volunteerId == null) {
            return ResultUtil.createError("VolunteerAPI.error.noExtIdFound");
        }

        // Mapper to create the output.
        VolunteerMapper volunteerMapper = new VolunteerMapper();

        // Retrieve volunteer from the database.
        volunteerMapper.setVolunter(volunteerMyDao.fetchOneByVolunteerid(volunteerId));

        // Retrieve all instances
        VolunteerInstanceMyDao volunteerInstanceMyDao = new VolunteerInstanceMyDao(context);
        volunteerMapper.setInstanceList(volunteerInstanceMyDao.getInstanceForVolunteer(volunteerId, false));

        // Retrieve all matches
        VolunteerMatchMyDao volunteerMatchMyDao = new VolunteerMatchMyDao(context);
        volunteerMapper.setMatchList(volunteerMatchMyDao.getMatchForVolunteer(volunteerId, false), new StudentMyDao(context));

        // Close and return.
        context.close();
        Log.logDebug("End VolunteerApi.get");
        return ResultUtil.createOk(volunteerMapper.getVolunteerReturn());
    }

    /**
     * Update a volunteer based on frontend object.
     * @param volunteerReturn
     * @return nothing
     */
    public Result update(VolunteerReturn volunteerReturn) {
        try {
            context = new Context();
        } catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start VolunteerAPI.update for externalIdentifier " + volunteerReturn.getExternalIdentifier());

        // Check if it is a valid volunteer.
        if (volunteerReturn.getDateOfBirth() == null) {
            return ResultUtil.createError("VolunteerAPI.update.error.fillDateOfBirth");
        }
        if (volunteerReturn.getExternalIdentifier() == null) {
            return ResultUtil.createError("VolunteerAPI.update.error.fillExtId");
        }
        if (volunteerReturn.getLastName() == null) {
            return ResultUtil.createError("VolunteerAPI.update.error.fillLastName");
        }

        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);
        Integer volunteerId = volunteerMyDao.getIdFromExtId(volunteerReturn.getExternalIdentifier());
        if (volunteerId == null) {
            return ResultUtil.createError("VolunteerAPI.error.noExtIdFound");
        }

        // Volunteer is valid, so we map return to pojo.
        VolunteerMapper volunteerMapper = new VolunteerMapper(volunteerReturn);
        VolunteerPojo volunteerPojo = volunteerMapper.getPojo(volunteerId);

        volunteerMyDao.update(volunteerPojo);

        // Commit, close and return.
        try {
            context.getConnection().commit();
        } catch (SQLException e) {
            return ResultUtil.createError("Context.error.commit", e);
        }
        context.close();
        Log.logDebug("End VolunteerAPI.update");
        return ResultUtil.createOk();
    }

    /**
     * Add a volunteer based on frontend object.
     * @param volunteerReturn
     * @return external identifier from the added volunteer.
     */
    public Result add(VolunteerReturn volunteerReturn) {
        try {
            context = new Context();
        } catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start VolunteerAPI.add for externalIdentifier " + volunteerReturn.getExternalIdentifier());

        // Check if it is a valid volunteer.
        if (volunteerReturn.getDateOfBirth() == null) {
            return ResultUtil.createError("VolunteerAPI.update.error.fillDateOfBirth");
        }
        if (volunteerReturn.getLastName() == null) {
            return ResultUtil.createError("VolunteerAPI.update.error.fillLastName");
        }

        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);
        VolunteerMapper volunteerMapper = new VolunteerMapper(volunteerReturn);

        // Insert also fills the external identifier we return later.
        VolunteerPojo volunteerPojo = volunteerMapper.getPojo(null);
        if (!volunteerMyDao.insertPojo(volunteerPojo)) {
            context.rollback();
            return ResultUtil.createError("VolunteerAPI.add.error.insertVolunteer");
        }

        // Volunteer is active from today.
        VolunteerinstancePojo volunteerinstancePojo = new VolunteerinstancePojo();
        volunteerinstancePojo.setVolunteerid(volunteerPojo.getVolunteerid());
        volunteerinstancePojo.setDatestart(DateTimeUtil.getCurrentDate());
        VolunteerInstanceMyDao volunteerInstanceMyDao = new VolunteerInstanceMyDao(context);
        if (!volunteerInstanceMyDao.insertPojo(volunteerinstancePojo)) {
            context.rollback();
            return ResultUtil.createError("VolunteerAPI.add.error.insertVolunteerInstance");
        }

        // Commit, close and return.
        try {
            context.getConnection().commit();
        } catch (SQLException e) {
            return ResultUtil.createError("Context.error.commit", e);
        }
        context.close();
        Log.logDebug("End VolunteerAPI.add");
        return ResultUtil.createOk(volunteerPojo.getExternalidentifier());
    }

    /**
     * Search for volunteers that specify the given conditions, if they are filled.
     * @param input
     * @param isActive
     * @param hasTraining
     * @param hasMatch
     * @param city
     * @return
     */
    public Result advancedSearch(String input, Boolean isActive, Boolean hasTraining, Boolean hasMatch, String city) {
        try {
            context = new Context();
        } catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start VolunteerAPI.advancedSearch for input " + input + " isActive " + isActive + " hasTrainig " + hasTraining
            + " hasMatch " + hasMatch + " city " + city);
        // Retrieve the list from the database.
        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);
        List<VolunteerPojo> volunteerPojos = volunteerMyDao.advancedSearch(input, isActive, hasTraining, hasMatch, city);

        // No connection is needed.
        context.close();

        // Convert the list of pojos to returns.
        List<VolunteerReturn> volunteerReturns = new ArrayList();
        for (VolunteerPojo volunteerPojo : volunteerPojos) {
            VolunteerMapper volunteerMapper = new VolunteerMapper();
            volunteerMapper.setVolunter(volunteerPojo);

            volunteerReturns.add(volunteerMapper.getVolunteerReturn());
        }

        // Return the list.
        Log.logDebug("End VolunteerAPI.searchByInput");
        return ResultUtil.createOk(volunteerReturns);
    }

    /**
     * Toggle active state of a volunteer.
     * @param externalIdentifier
     * @return
     */
    public Result toggleActive(String externalIdentifier) {
        try {
            context = new Context();
        } catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start VolunteerAPI.toggleActive for externalIdentifier " + externalIdentifier);

        // Get volunteerId.
        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);
        Integer volunteerId = volunteerMyDao.getIdFromExtId(externalIdentifier);
        if (volunteerId == null) {
            return ResultUtil.createError("VolunteerAPI.error.noExtIdFound");
        }

        VolunteerInstanceMyDao volunteerInstanceMyDao = new VolunteerInstanceMyDao(context);
        boolean currentlyActive = volunteerMyDao.isActive(volunteerId);

        if (!currentlyActive) {
            // Just add a new line to volunteerinstance.
            VolunteerinstancePojo volunteerinstancePojo = new VolunteerinstancePojo();
            volunteerinstancePojo.setVolunteerid(volunteerId);
            volunteerinstancePojo.setDatestart(DateTimeUtil.getCurrentDate());
            volunteerInstanceMyDao.insertPojo(volunteerinstancePojo);
        } else {
            // Set dateEnd of the latest line to today. If the dateStart is also today, we just remove the line.
            VolunteerinstancePojo volunteerinstancePojo = volunteerInstanceMyDao.getInstanceToday(volunteerId);
            if (volunteerinstancePojo.getDatestart().compareTo(DateTimeUtil.getCurrentDate()) == 0) {
                volunteerInstanceMyDao.delete(volunteerinstancePojo);
            } else if (volunteerinstancePojo.getDateend() != null &&
                    volunteerinstancePojo.getDateend().compareTo(DateTimeUtil.getCurrentDate()) == 0) {
                volunteerinstancePojo.setDateend(null);
                volunteerInstanceMyDao.update(volunteerinstancePojo);
            } else {
                volunteerinstancePojo.setDateend(DateTimeUtil.getCurrentDate());
                volunteerInstanceMyDao.update(volunteerinstancePojo);
            }
        }

        // Commit, close and return.
        try {
            context.getConnection().commit();
        } catch (SQLException e) {
            return ResultUtil.createError("Context.error.commit", e);
        }
        context.close();
        Log.logDebug("End VolunteerAPI.toggleActive");
        return ResultUtil.createOk();
    }

    /**
     * Add a new match line, starting today, for given student.
     * @param studentReturn
     * @return VolunteerMatch.externalIdentifier
     */
    public Result addMatch(String volunteerExtId, StudentReturn studentReturn) {
        try {
            context = new Context();
        } catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start VolunteerAPI.addMatch volunteerExtId " + volunteerExtId + " studentExtId " + studentReturn.getExternalIdentifier());
        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);
        Integer volunteerId = volunteerMyDao.getIdFromExtId(volunteerExtId);
        if (volunteerId == null) {
            return ResultUtil.createError("VolunteerAPI.addMatch.noVolunteer");
        }

        StudentMyDao studentMyDao = new StudentMyDao(context);
        Integer studentId = studentMyDao.getIdFromExtId(studentReturn.getExternalIdentifier());
        if (studentId == null) {
            return ResultUtil.createError("VolunteerAPI.addMatch.noStudent");
        }

        VolunteerMatchMyDao volunteerMatchMyDao = new VolunteerMatchMyDao(context);
        VolunteermatchPojo volunteermatchPojo = new VolunteermatchPojo();
        volunteermatchPojo.setVolunteerid(volunteerId);
        volunteermatchPojo.setStudentid(studentId);
        volunteermatchPojo.setDatestart(DateTimeUtil.getCurrentDate());
        if (!volunteerMatchMyDao.insertPojo(volunteermatchPojo)) {
            return ResultUtil.createError("VolunteerAPI.addMatch.failedInsert");
        }

        // Commit, close and return.
        try {
            context.getConnection().commit();
        } catch (SQLException e) {
            return ResultUtil.createError("Context.error.commit", e);
        }
        context.close();
        Log.logDebug("End VolunteerAPI.addMatch");
        return ResultUtil.createOk(volunteermatchPojo.getExternalidentifier());
    }
}
