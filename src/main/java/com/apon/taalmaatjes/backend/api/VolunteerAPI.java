package com.apon.taalmaatjes.backend.api;

import com.apon.taalmaatjes.backend.api.returns.*;
import com.apon.taalmaatjes.backend.api.returns.mapper.VolunteerInstanceMapper;
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

import java.sql.Date;
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
    public Result getVolunteer(String externalIdentifier) {
        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }

        Log.logDebug("Start VolunteerApi.getVolunteer for externalIdentifier " + externalIdentifier);

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
        Log.logDebug("End VolunteerApi.getVolunteer");
        return ResultUtil.createOk(volunteerMapper.getVolunteerReturn());
    }

    /**
     * Get a volunteerinstance based on the external identifiers.
     * @param volunteerExtId The external identifier from the volunteer.
     * @param volunteerInstanceExtId The external identifier from the instance.
     * @return VolunteerInstanceReturn.
     */
    public Result getVolunteerInstance(String volunteerExtId, String volunteerInstanceExtId) {
        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start VolunteerAPI.getVolunteerInstance for volunteerExtId " + volunteerExtId + " and " + volunteerInstanceExtId);


        // Get volunteerId.
        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);
        Integer volunteerId = volunteerMyDao.getIdFromExtId(volunteerExtId);
        if (volunteerId == null) {
            return ResultUtil.createError("VolunteerAPI.error.noExtIdFound");
        }

        // Get volunteerInstanceId.
        VolunteerInstanceMyDao volunteerInstanceMyDao = new VolunteerInstanceMyDao(context);
        Integer volunteerInstanceId = volunteerInstanceMyDao.getIdFromExtId(volunteerId, volunteerInstanceExtId);
        if (volunteerInstanceId == null) {
            return ResultUtil.createError("VolunteerAPI.error.noVolunteerInstanceExtIdFound");
        }

        // Get the volunteerInstancePojo
        VolunteerinstancePojo volunteerinstancePojo = volunteerInstanceMyDao.fetchByIds(volunteerId, volunteerInstanceId);
        VolunteerInstanceMapper volunteerInstanceMapper = new VolunteerInstanceMapper();
        volunteerInstanceMapper.setVolunteerInstance(volunteerinstancePojo);

        context.close();
        context.close();
        Log.logDebug("End VolunteerAPI.update");
        return ResultUtil.createOk(volunteerInstanceMapper.getVolunteerInstanceReturn());
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
     * @param volunteerMatchReturn The volunteerMatch.
     * @return Nothing.
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

    /**
     * Check if the line can be added to the database. Merge the line if needed. Returns true if added (possibly merged)
     * and return false if some verification failed.
     * @param context
     * @param volunteerId
     * @param dateStart
     * @param dateEnd
     * @param volunteerInstanceId If this value is non-null, algorithm assumes it is an update instead of insert.
     * @return
     */
    private boolean isNewInstanceValidAndAdd(Context context, int volunteerId, Date dateStart, Date dateEnd, Integer volunteerInstanceId) {
        VolunteerInstanceMyDao volunteerInstanceMyDao = new VolunteerInstanceMyDao(context);
        // if [A,B] can be merged into [dateStart,dateEnd] with B=dateStart then mergeAfterVolunteerInstanceId will be B.
        Integer mergeAfterVolunteerInstanceId = null;

        // if [dateStart,dateEnd] can be merged into [A,B] with A=dateEnd then mergeAfterVolunteerInstanceId will be A.
        Integer mergeBeforeVolunteerInstanceId = null;
        for (VolunteerinstancePojo volunteerinstancePojo : volunteerInstanceMyDao.getInstanceForVolunteer(volunteerId)) {
            // If we update instead of insert, we want to 'exclude' the to-be-updated volunteerinstance from the search.
            if (volunteerInstanceId != null && volunteerinstancePojo.getVolunteerinstanceid().equals(volunteerInstanceId)) {
                continue;
            }
            // If one of the following hold, return false:
            // 1. dateStart is contained in (pojo.dateStart, pojo.dateEnd)
            // 2. dateEnd is contained in (pojo.dateStart, pojo.dateEnd)
            // 3. Range [pojo.dateStart, pojo.dateEnd] is contained within (dateStart, dateEnd).

            // However, the above does not hold at all whenever pojo.dateStart = pojo.dateEnd (one day instance).
            // So we only threat this case differently.
            if (volunteerinstancePojo.getDateend() != null &&
                    volunteerinstancePojo.getDatestart().compareTo(volunteerinstancePojo.getDateend()) == 0) {
                // We have 4 possibilities:
                // 1. pojo.date \in (dateStart, dateEnd) => overlap so false.
                // 2. pojo.date == dateStart || pojo.date + 1 DAY = dateStart => merge
                // 3. pojo.date == dateEnd || pojo.date + 1 DAY = dateEnd => merge
                // 4. none of the above => we do nothing, we can ignore this line.

                if (DateTimeUtil.isBetweenWithoutEndpoints(volunteerinstancePojo.getDatestart(), dateStart, dateEnd)) {
                    return false;
                }

                // Merge before
                if (volunteerinstancePojo.getDateend().compareTo(dateStart) == 0 ||
                        DateTimeUtil.nrOfDaysInBetween(volunteerinstancePojo.getDateend(), dateStart) == 1) {
                    mergeBeforeVolunteerInstanceId = volunteerinstancePojo.getVolunteerinstanceid();
                }

                // Merge after
                if (dateEnd != null && (volunteerinstancePojo.getDatestart().compareTo(dateEnd) == 0 ||
                        DateTimeUtil.nrOfDaysInBetween(dateEnd, volunteerinstancePojo.getDatestart()) == 1)) {
                    mergeAfterVolunteerInstanceId = volunteerinstancePojo.getVolunteerinstanceid();
                }

                // In any case we can just go on searching.
                continue;
            }

            if (DateTimeUtil.isBetweenWithoutEndpoints(dateStart,
                    volunteerinstancePojo.getDatestart(), volunteerinstancePojo.getDateend())) {
                return false;
            }

            if (DateTimeUtil.isBetweenWithoutEndpoints(dateEnd,
                    volunteerinstancePojo.getDatestart(), volunteerinstancePojo.getDateend())) {
                return false;
            }

            if (DateTimeUtil.isContained(volunteerinstancePojo.getDatestart(), volunteerinstancePojo.getDateend(),
                    dateStart, dateEnd)) {
                return false;
            }

            // Determine whether we can actually merge with this line.
            // Merge after: (note that if both dates are null, we can never merge.
            if (dateEnd != null && volunteerinstancePojo.getDatestart() != null &&
                    (volunteerinstancePojo.getDatestart().compareTo(dateEnd) == 0 ||
                    DateTimeUtil.nrOfDaysInBetween(dateEnd, volunteerinstancePojo.getDatestart()) == 1)) {
                mergeAfterVolunteerInstanceId = volunteerinstancePojo.getVolunteerinstanceid();
            }

            // Merge before: (note that dateEnd must be non-null to merge. Also dateStart is never null).
            if (volunteerinstancePojo.getDateend() != null &&
                    (volunteerinstancePojo.getDateend().compareTo(dateStart) == 0 ||
                    DateTimeUtil.nrOfDaysInBetween(volunteerinstancePojo.getDateend(), dateStart) == 1)) {
                mergeBeforeVolunteerInstanceId = volunteerinstancePojo.getVolunteerinstanceid();
            }
        }

        // If we actually reach this point, we know the line will be added to the database (merged or not).
        VolunteerinstancePojo volunteerinstancePojo = new VolunteerinstancePojo();
        volunteerinstancePojo.setVolunteerid(volunteerId);

        // Merge after if possible.
        if (mergeAfterVolunteerInstanceId != null) {
            VolunteerinstancePojo mergedVolunteerinstancePojo = volunteerInstanceMyDao.fetchByIds(volunteerId, mergeAfterVolunteerInstanceId);
            volunteerinstancePojo.setDateend(mergedVolunteerinstancePojo.getDateend());
            volunteerInstanceMyDao.delete(mergedVolunteerinstancePojo);
        } else {
            // Date start must still be set.
            volunteerinstancePojo.setDateend(dateEnd);
        }

        // Merge before if possible.
        if (mergeBeforeVolunteerInstanceId != null) {
            VolunteerinstancePojo mergedVolunteerinstancePojo = volunteerInstanceMyDao.fetchByIds(volunteerId, mergeBeforeVolunteerInstanceId);
            volunteerinstancePojo.setDatestart(mergedVolunteerinstancePojo.getDatestart());
            volunteerInstanceMyDao.delete(mergedVolunteerinstancePojo);
        } else {
            // Date start must still be set.
            volunteerinstancePojo.setDatestart(dateStart);
        }

        if (volunteerInstanceId == null) {
            volunteerInstanceMyDao.insertPojo(volunteerinstancePojo);
        } else {
            volunteerinstancePojo.setVolunteerinstanceid(volunteerInstanceId);
            volunteerInstanceMyDao.update(volunteerinstancePojo);
        }

        return true;
    }

    public Result addInstance(VolunteerInstanceReturn volunteerInstanceReturn) {
        if (volunteerInstanceReturn.getDateStart() == null) {
            return ResultUtil.createError("VolunteerAPI.addInstance.noDateStart");
        }

        if (volunteerInstanceReturn.getDateEnd() != null &&
                volunteerInstanceReturn.getDateStart().compareTo(volunteerInstanceReturn.getDateEnd()) > 0) {
            return ResultUtil.createError("VolunteerAPI.addInstance.dateStartAfterDateEnd");
        }

        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start VolunteerAPI.addInstance volunteerExtId " + volunteerInstanceReturn.getVolunteerExternalIdentifier());

        // Get volunteerId.
        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);
        Integer volunteerId = volunteerMyDao.getIdFromExtId(volunteerInstanceReturn.getVolunteerExternalIdentifier());
        if (volunteerId == null) {
            return ResultUtil.createError("VolunteerAPI.error.noExtIdFound");
        }

        // Handle the complete adding / merging in another function.
        if (!isNewInstanceValidAndAdd(context, volunteerId, volunteerInstanceReturn.getDateStart(), volunteerInstanceReturn.getDateEnd(), null)) {
            context.rollback();
            return ResultUtil.createError("VolunteerAPI.addInstance.invalidInstance");
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

    public Result editInstance(VolunteerInstanceReturn volunteerInstanceReturn) {
        if (volunteerInstanceReturn.getDateStart() == null) {
            return ResultUtil.createError("VolunteerAPI.addInstance.noDateStart");
        }

        if (volunteerInstanceReturn.getDateEnd() != null &&
                volunteerInstanceReturn.getDateStart().compareTo(volunteerInstanceReturn.getDateEnd()) > 0) {
            return ResultUtil.createError("VolunteerAPI.addInstance.dateStartAfterDateEnd");
        }

        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start VolunteerAPI.addInstance volunteerExtId " + volunteerInstanceReturn.getVolunteerExternalIdentifier());

        // Get volunteerId.
        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);
        Integer volunteerId = volunteerMyDao.getIdFromExtId(volunteerInstanceReturn.getVolunteerExternalIdentifier());
        if (volunteerId == null) {
            return ResultUtil.createError("VolunteerAPI.error.noExtIdFound");
        }

        // Get volunteerInstanceId
        VolunteerInstanceMyDao volunteerInstanceMyDao = new VolunteerInstanceMyDao(context);
        Integer volunteerInstanceId = volunteerInstanceMyDao.getIdFromExtId(volunteerId, volunteerInstanceReturn.getExternalIdentifier());
        if (volunteerInstanceId == null) {
            return ResultUtil.createError("VolunteerAPI.error.noVolunteerInstanceExtIdFound.");
        }

        // Handle the complete adding / merging in another function.
        if (!isNewInstanceValidAndAdd(context, volunteerId, volunteerInstanceReturn.getDateStart(), volunteerInstanceReturn.getDateEnd(), volunteerInstanceId)) {
            context.rollback();
            return ResultUtil.createError("VolunteerAPI.addInstance.invalidInstance");
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
