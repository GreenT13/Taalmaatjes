package com.apon.taalmaatjes.backend.api;

import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.StudentReturn;
import com.apon.taalmaatjes.backend.api.returns.VolunteerMatchReturn;
import com.apon.taalmaatjes.backend.api.returns.VolunteerReturn;
import com.apon.taalmaatjes.backend.api.returns.mapper.VolunteerMapper;
import com.apon.taalmaatjes.backend.api.returns.mapper.VolunteerMatchMapper;
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

    /**
     * Get a volunteer based on the external identifier.
     * @param externalIdentifier The external identifier.
     * @return VolunteerReturn
     */
    public Result get(String externalIdentifier) {
        Context context;
        try {context = new Context();} catch (SQLException e) {
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
     * @param volunteerReturn The volunteer.
     * @return nothing
     */
    public Result update(VolunteerReturn volunteerReturn) {
        Context context;
        try {context = new Context();} catch (SQLException e) {
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
     * @param volunteerReturn The volunteer.
     * @return external identifier from the added volunteer.
     */
    public Result add(VolunteerReturn volunteerReturn) {
        Context context;
        try {context = new Context();} catch (SQLException e) {
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
            volunteerMapper.setVolunter(volunteerPojo);

            volunteerReturns.add(volunteerMapper.getVolunteerReturn());
        }

        // Return the list.
        Log.logDebug("End VolunteerAPI.searchByInput");
        return ResultUtil.createOk(volunteerReturns);
    }

    /**
     * Toggle active state of a volunteer.
     * @param externalIdentifier The external identifier of the volunteer.
     * @return Nothing.
     */
    public Result toggleActive(String externalIdentifier) {
        Context context;
        try {context = new Context();} catch (SQLException e) {
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
     * @param studentReturn The student.
     * @return VolunteerMatch.externalIdentifier
     */
    public Result addMatch(String volunteerExtId, StudentReturn studentReturn) {
        Context context;
        try {context = new Context();} catch (SQLException e) {
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

        // Check that it is actually possible to add a new match today. This is the case if holds:
        // If there is a match with the student, it must not be active today.
        VolunteerMatchMyDao volunteerMatchMyDao = new VolunteerMatchMyDao(context);
        for (VolunteermatchPojo volunteermatchPojo : volunteerMatchMyDao.getMatchForVolunteer(volunteerId, true)) {
            if (!volunteermatchPojo.getStudentid().equals(studentId)) {
                continue;
            }

            // If the match is active today, return an logError.
            if (DateTimeUtil.isActiveToday(volunteermatchPojo.getDatestart(), volunteermatchPojo.getDateend())) {
                return ResultUtil.createError("VolunteerAPI.addMatch.alreadyExistsAndActive");
            }
        }

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

    /**
     * Toggle the active state of a match.
     * @param volunteerExtId The external identifier of the volunteer.
     * @param volunteerMatchExtId The external identifier of the volunteerMatch.
     * @return Nothing.
     */
    public Result toggleMatch(String volunteerExtId, String volunteerMatchExtId) {
        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start VolunteerAPI.toggleMatch volunteerExtId " + volunteerExtId + " volunteerMatchExtId " + volunteerMatchExtId);

        // Get volunteerId.
        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);
        Integer volunteerId = volunteerMyDao.getIdFromExtId(volunteerExtId);
        if (volunteerId == null) {
            return ResultUtil.createError("VolunteerAPI.error.noExtIdFound");
        }

        // Get volunteerMatchId
        VolunteerMatchMyDao volunteerMatchMyDao = new VolunteerMatchMyDao(context);
        Integer volunteerMatchId = volunteerMatchMyDao.getIdFromExtId(volunteerId, volunteerMatchExtId);
        if (volunteerMatchId == null) {
            return ResultUtil.createError("VolunteerAPI.error.noExtIdFoundMatch");
        }

        VolunteermatchPojo volunteermatchPojo = volunteerMatchMyDao.fetchById(volunteerId, volunteerMatchId);
        if (DateTimeUtil.isActiveTodayMinusOne(volunteermatchPojo.getDatestart(), volunteermatchPojo.getDateend())) {
            // It is active, so we stop it.
            volunteermatchPojo.setDateend(DateTimeUtil.getCurrentDate());
            // If it was only active for one day, we remove it.
            if (volunteermatchPojo.getDatestart().compareTo(volunteermatchPojo.getDateend()) == 0) {
                volunteerMatchMyDao.delete(volunteermatchPojo);
            } else {
                volunteerMatchMyDao.update(volunteermatchPojo);
            }
        } else {
            // Activate it.
            volunteermatchPojo.setDateend(null);
            volunteerMatchMyDao.update(volunteermatchPojo);
        }

        // Commit, close and return.
        try {
            context.getConnection().commit();
        } catch (SQLException e) {
            return ResultUtil.createError("Context.error.commit", e);
        }
        context.close();
        Log.logDebug("End VolunteerAPI.toggleMatch");
        return ResultUtil.createOk();
    }

    /**
     * Add a new match to the database. However, we need to check that it is a valid match to add.
     * This means there is no other match for the same student that overlaps in date.
     * @param volunteerMatchReturn
     * @return
     */
    public Result addMatch(String volunteerExtId, VolunteerMatchReturn volunteerMatchReturn) {
        if (volunteerMatchReturn.getDateStart() == null) {
            return ResultUtil.createError("VolunteerAPI.addMatch.noDateStart");
        }

        if (volunteerMatchReturn.getDateEnd() != null &&
                volunteerMatchReturn.getDateStart().compareTo(volunteerMatchReturn.getDateEnd()) > 0) {
            return ResultUtil.createError("VolunteerAPI.addMatch.dateStartAfterDateEnd");
        }

        if (volunteerMatchReturn.getStudent() == null ||
                volunteerMatchReturn.getStudent().getExternalIdentifier() == null) {
            return ResultUtil.createError("VolunteerAPI.addMatch.noStudentFilled");
        }

        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start VolunteerAPI.addMatch volunteerExtId " + volunteerExtId);

        // Get volunteerId.
        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);
        Integer volunteerId = volunteerMyDao.getIdFromExtId(volunteerExtId);
        if (volunteerId == null) {
            return ResultUtil.createError("VolunteerAPI.error.noExtIdFound");
        }

        // Get studentId.
        StudentMyDao studentMyDao = new StudentMyDao(context);
        Integer studentId = studentMyDao.getIdFromExtId(volunteerMatchReturn.getStudent().getExternalIdentifier());
        if (studentId == null) {
            return ResultUtil.createError("VolunteerAPI.error.noExtIdFoundStudent");
        }

        // Check there is no other VolunteerMatch that overlaps with this student.
        VolunteerMatchMyDao volunteerMatchMyDao = new VolunteerMatchMyDao(context);
        for (VolunteermatchPojo volunteerMatchPojo : volunteerMatchMyDao.getMatchForVolunteerAndStudent(volunteerId, studentId)) {
            if (DateTimeUtil.isOverlap(volunteerMatchReturn.getDateStart(), volunteerMatchReturn.getDateEnd(),
                    volunteerMatchPojo.getDatestart(), volunteerMatchPojo.getDateend())) {
                // We cannot have overlap.
                return ResultUtil.createError("VolunteerAPI.addMatch.overlap");
            }
        }

        // Add the match to the database.
        VolunteerMatchMapper volunteerMatchMapper = new VolunteerMatchMapper(volunteerMatchReturn);
        if (!volunteerMatchMyDao.insertPojo(volunteerMatchMapper.getPojo(volunteerId, studentId))) {
            return ResultUtil.createError("VolunteerAPI.addMatch.insert");
        }

        // Commit, close and return.
        try {
            context.getConnection().commit();
        } catch (SQLException e) {
            return ResultUtil.createError("Context.error.commit", e);
        }
        context.close();
        Log.logDebug("End VolunteerAPI.toggleMatch");
        return ResultUtil.createOk();
    }
}
