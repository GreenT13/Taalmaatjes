package com.apon.taalmaatjes.backend.api;

import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.VolunteerReturn;
import com.apon.taalmaatjes.backend.api.returns.mapper.VolunteerMapper;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteerPojo;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteerinstancePojo;
import com.apon.taalmaatjes.backend.database.jooq.Context;
import com.apon.taalmaatjes.backend.database.mydao.*;
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

    /**
     * Get a volunteer based on the external identifier.
     * @param volunteerExtId The external identifier.
     * @return VolunteerReturn
     */
    public Result getVolunteer(String volunteerExtId) {
        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }

        Log.logDebug("Start VolunteerApi.getVolunteer for externalIdentifier " + volunteerExtId);

        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);

        // First check if the externalIdentifier is valid.
        Integer volunteerId = volunteerMyDao.getIdFromExtId(volunteerExtId);
        if (volunteerId == null) {
            return ResultUtil.createError("VolunteerAPI.error.noVolunteerExtIdFound");
        }

        // Mapper to create the output.
        VolunteerMapper volunteerMapper = new VolunteerMapper();

        // Retrieve volunteer from the database.
        volunteerMapper.setVolunteer(volunteerMyDao.fetchOneByVolunteerid(volunteerId));

        // Retrieve all instances
        VolunteerInstanceMyDao volunteerInstanceMyDao = new VolunteerInstanceMyDao(context);
        volunteerMapper.setInstanceList(volunteerInstanceMyDao.getInstanceForVolunteer(volunteerId, false));

        // Retrieve all matches
        VolunteerMatchMyDao volunteerMatchMyDao = new VolunteerMatchMyDao(context);
        volunteerMapper.setMatchList(volunteerMatchMyDao.getMatchForVolunteer(volunteerId, false), new StudentMyDao(context));

        // Retrieve all tasks
        TaskMyDao taskMyDao = new TaskMyDao(context);
        volunteerMapper.setTaskList(taskMyDao.getTasksForVolunteer(volunteerId));

        // Close and return.
        context.close();
        Log.logDebug("End VolunteerApi.getVolunteer");
        return ResultUtil.createOk(volunteerMapper.getVolunteerReturn());
    }

    /**
     * Add a volunteer based on frontend object.
     * @param volunteerReturn The volunteer.
     * @return external identifier from the added volunteer.
     */
    public Result addVolunteer(VolunteerReturn volunteerReturn) {
        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start VolunteerAPI.addVolunteer for externalIdentifier " + volunteerReturn.getExternalIdentifier());

        // Check if it is a valid volunteer.
        if (volunteerReturn.getDateOfBirth() == null) {
            return ResultUtil.createError("VolunteerAPI.error.fillDateOfBirth");
        }
        if (volunteerReturn.getLastName() == null) {
            return ResultUtil.createError("VolunteerAPI.error.fillLastName");
        }

        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);
        VolunteerMapper volunteerMapper = new VolunteerMapper(volunteerReturn);

        // Insert also fills the external identifier we return later.
        VolunteerPojo volunteerPojo = volunteerMapper.getPojo(null);
        if (!volunteerMyDao.insertPojo(volunteerPojo)) {
            context.rollback();
            return ResultUtil.createError("VolunteerAPI.addVolunteer.error.insertVolunteer");
        }

        // Volunteer is active from today.
        VolunteerinstancePojo volunteerinstancePojo = new VolunteerinstancePojo();
        volunteerinstancePojo.setVolunteerid(volunteerPojo.getVolunteerid());
        volunteerinstancePojo.setDatestart(DateTimeUtil.getCurrentDate());
        VolunteerInstanceMyDao volunteerInstanceMyDao = new VolunteerInstanceMyDao(context);
        if (!volunteerInstanceMyDao.insertPojo(volunteerinstancePojo)) {
            context.rollback();
            return ResultUtil.createError("VolunteerAPI.addVolunteer.error.insertVolunteerInstance");
        }

        // Commit, close and return.
        try {
            context.getConnection().commit();
        } catch (SQLException e) {
            return ResultUtil.createError("Context.error.commit", e);
        }
        context.close();
        Log.logDebug("End VolunteerAPI.addVolunteer");
        return ResultUtil.createOk(volunteerPojo.getExternalidentifier());
    }

    /**
     * Update a volunteer based on frontend object.
     * @param volunteerReturn The volunteer.
     * @return nothing
     */
    public Result updateVolunteer(VolunteerReturn volunteerReturn) {
        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start VolunteerAPI.updateVolunteer for volunteerExtId " + volunteerReturn.getExternalIdentifier());

        // Check if it is a valid volunteer.
        if (volunteerReturn.getDateOfBirth() == null) {
            return ResultUtil.createError("VolunteerAPI.error.fillDateOfBirth");
        }
        if (volunteerReturn.getExternalIdentifier() == null) {
            return ResultUtil.createError("VolunteerAPI.error.fillVolunteerExtId");
        }
        if (volunteerReturn.getLastName() == null) {
            return ResultUtil.createError("VolunteerAPI.error.fillLastName");
        }

        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);
        Integer volunteerId = volunteerMyDao.getIdFromExtId(volunteerReturn.getExternalIdentifier());
        if (volunteerId == null) {
            return ResultUtil.createError("VolunteerAPI.error.noVolunteerExtIdFound");
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
        Log.logDebug("End VolunteerAPI.updateVolunteer");
        return ResultUtil.createOk();
    }

    /**
     * Search for volunteers that specify the given conditions, if they are filled.
     * @param input The input searched for in firstName, insertion, lastName.
     * @param isActive Whether the volunteer must be active today.
     * @param hasTraining Whether the value of Volunteer.hasTraining is true or false.
     * @param hasMatch Whether there exists a VolunteerMatch for this volunteer.
     * @param city The city of the volunteer.
     * @return List&lt;VolunteerReturn&gt;
     */
    public Result advancedSearch(String input, Boolean isActive, Boolean hasTraining, Boolean hasMatch, String city) {
        Context context;
        try {context = new Context();} catch (SQLException e) {
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
            volunteerMapper.setVolunteer(volunteerPojo);

            volunteerReturns.add(volunteerMapper.getVolunteerReturn());
        }

        // Return the list.
        Log.logDebug("End VolunteerAPI.advancedSearch");
        return ResultUtil.createOk(volunteerReturns);
    }

}
