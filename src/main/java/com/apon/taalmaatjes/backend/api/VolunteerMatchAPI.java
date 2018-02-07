package com.apon.taalmaatjes.backend.api;

import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.VolunteerMatchReturn;
import com.apon.taalmaatjes.backend.api.returns.mapper.VolunteerMatchMapper;
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
import org.joda.time.DateTime;

import java.sql.Date;
import java.sql.SQLException;

public class VolunteerMatchAPI {
    private static VolunteerMatchAPI ourInstance = new VolunteerMatchAPI();

    public static VolunteerMatchAPI getInstance() {
        return ourInstance;
    }

    private VolunteerMatchAPI() { }

    /**
     * Get volunteer match.
     * @param volunteerExtId The external identifier from the volunteer.
     * @param volunteerMatchExtId The external identifier from the match.
     * @return VolunteerMatchReturn
     */
    public Result getVolunteerMatch(String volunteerExtId, String volunteerMatchExtId) {
        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start VolunteerMatchAPI.getVolunteerMatch for volunteerExtId " + volunteerExtId + " and " + volunteerMatchExtId);

        // Get volunteerId.
        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);
        Integer volunteerId = volunteerMyDao.getIdFromExtId(volunteerExtId);
        if (volunteerId == null) {
            return ResultUtil.createError("VolunteerAPI.error.noExtIdFound");
        }

        // Get volunteerMatchId.
        VolunteerMatchMyDao volunteerMatchMyDao = new VolunteerMatchMyDao(context);
        Integer volunteerMatchId = volunteerMatchMyDao.getIdFromExtId(volunteerId, volunteerMatchExtId);
        if (volunteerMatchId == null) {
            return ResultUtil.createError("VolunteerAPI.error.noVolunteerMatchExtIdFound");
        }

        // Get the volunteerInstancePojo
        VolunteermatchPojo volunteermatchPojo = volunteerMatchMyDao.fetchById(volunteerId, volunteerMatchId);
        VolunteerMatchMapper volunteerMatchMapper = new VolunteerMatchMapper();
        volunteerMatchMapper.setVolunteerMatch(volunteermatchPojo);

        // Get studentExtId
        StudentMyDao studentMyDao = new StudentMyDao(context);
        volunteerMatchMapper.setStudent(studentMyDao.getExtIdFromid(volunteermatchPojo.getStudentid()));

        context.close();
        Log.logDebug("End VolunteerAPI.getVolunteerMatch");
        return ResultUtil.createOk(volunteerMatchMapper.getVolunteerMatchReturn());
    }

    /**
     * Add a new volunteer instance.
     * @param volunteerMatchReturn The instance to add.
     * @return Nothing.
     */
    public Result addVolunteerMatch(VolunteerMatchReturn volunteerMatchReturn) {
        if (volunteerMatchReturn.getDateStart() == null) {
            return ResultUtil.createError("VolunteerAPI.addVolunteerMatch.noDateStart");
        }

        if (volunteerMatchReturn.getDateEnd() != null &&
                volunteerMatchReturn.getDateStart().compareTo(volunteerMatchReturn.getDateEnd()) > 0) {
            return ResultUtil.createError("VolunteerAPI.addVolunteerMatch.dateStartAfterDateEnd");
        }

        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start VolunteerAPI.addVolunteerMatch volunteerExtId " + volunteerMatchReturn.getVolunteerExtId());

        // Get volunteerId.
        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);
        Integer volunteerId = volunteerMyDao.getIdFromExtId(volunteerMatchReturn.getVolunteerExtId());
        if (volunteerId == null) {
            return ResultUtil.createError("VolunteerAPI.error.noExtIdFound");
        }

        // Get studentId.
        StudentMyDao studentMyDao = new StudentMyDao(context);
        Integer studentId = studentMyDao.getIdFromExtId(volunteerMatchReturn.getStudentExtId());
        if (studentId == null) {
            return ResultUtil.createError("VolunteerMatchAPI.error.noStudentExtIdFound.");
        }

        // Handle the complete adding / merging in another function.
        if (!isVolunteerMatchValidAndAdd(context, volunteerId, studentId, volunteerMatchReturn.getDateStart(), volunteerMatchReturn.getDateEnd(), null)) {
            context.rollback();
            return ResultUtil.createError("VolunteerAPI.addVolunteerMatch.invalidMatch");
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
     * Edit an existing volunteer instance.
     * @param volunteerMatchReturn The updated instance.
     * @return Nothing.
     */
    public Result updateVolunteerMatch(VolunteerMatchReturn volunteerMatchReturn) {
        if (volunteerMatchReturn.getDateStart() == null) {
            return ResultUtil.createError("VolunteerAPI.updateVolunteerMatch.noDateStart");
        }

        if (volunteerMatchReturn.getDateEnd() != null &&
                volunteerMatchReturn.getDateStart().compareTo(volunteerMatchReturn.getDateEnd()) > 0) {
            return ResultUtil.createError("VolunteerAPI.updateVolunteerMatch.dateStartAfterDateEnd");
        }

        if (volunteerMatchReturn.getVolunteerExtId() == null) {
            return ResultUtil.createError("VolunteerAPI.updateVolunteerMatch.noVolunteerExtId");
        }

        if (volunteerMatchReturn.getExternalIdentifier() == null) {
            return ResultUtil.createError("VolunteerAPI.updateVolunteerMatch.noVolunteerMatchExtId");
        }

        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start VolunteerAPI.updateVolunteerMatch volunteerExtId " + volunteerMatchReturn.getVolunteerExtId());

        // Get volunteerId.
        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);
        Integer volunteerId = volunteerMyDao.getIdFromExtId(volunteerMatchReturn.getVolunteerExtId());
        if (volunteerId == null) {
            return ResultUtil.createError("VolunteerAPI.error.noExtIdFound");
        }

        // Get volunteerMatchId.
        VolunteerMatchMyDao volunteerMatchMyDao = new VolunteerMatchMyDao(context);
        Integer volunteerMatchId = volunteerMatchMyDao.getIdFromExtId(volunteerId, volunteerMatchReturn.getExternalIdentifier());
        if (volunteerMatchId == null) {
            return ResultUtil.createError("VolunteerAPI.error.noVolunteerMatchExtIdFound.");
        }

        // Get studentId.
        StudentMyDao studentMyDao = new StudentMyDao(context);
        Integer studentId = studentMyDao.getIdFromExtId(volunteerMatchReturn.getStudentExtId());
        if (studentId == null) {
            return ResultUtil.createError("VolunteerMatchAPI.error.noStudentExtIdFound.");
        }

        // Handle the complete adding / merging in another function.
        if (!isVolunteerMatchValidAndAdd(context, volunteerId, studentId, volunteerMatchReturn.getDateStart(), volunteerMatchReturn.getDateEnd(), volunteerMatchId)) {
            context.rollback();
            return ResultUtil.createError("VolunteerAPI.updateVolunteerMatch.invalidInstance");
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
     * @param context .
     * @param volunteerId .
     * @param studentId .
     * @param dateStart .
     * @param dateEnd .
     * @param volunteerMatchId If this value is non-null, algorithm assumes it is an update instead of insert.
     * @return boolean
     */
    private boolean isVolunteerMatchValidAndAdd(Context context, int volunteerId, int studentId, Date dateStart, Date dateEnd, Integer volunteerMatchId) {
        VolunteerMatchMyDao volunteerMatchMyDao = new VolunteerMatchMyDao(context);
        // if [A,B] can be merged into [dateStart,dateEnd] with B=dateStart then mergeAfterVolunteerMatchId will be B.
        Integer mergeAfterVolunteerMatchId = null;

        // if [dateStart,dateEnd] can be merged into [A,B] with A=dateEnd then mergeAfterVolunteerMatchId will be A.
        Integer mergeBeforeVolunteerMatchId = null;
        for (VolunteermatchPojo volunteermatchPojo : volunteerMatchMyDao.getMatchForVolunteerAndStudent(volunteerId, studentId)) {
            // If we update instead of insert, we want to 'exclude' the to-be-updated volunteer match from the search.
            if (volunteerMatchId != null && volunteermatchPojo.getVolunteermatchid().equals(volunteerMatchId)) {
                continue;
            }
            // If one of the following hold, return false:
            // 1. dateStart is contained in (pojo.dateStart, pojo.dateEnd)
            // 2. dateEnd is contained in (pojo.dateStart, pojo.dateEnd)
            // 3. Range [pojo.dateStart, pojo.dateEnd] is contained within (dateStart, dateEnd).

            // However, the above does not hold at all whenever pojo.dateStart = pojo.dateEnd (one day instance).
            // So we only threat this case differently.
            if (volunteermatchPojo.getDateend() != null &&
                    volunteermatchPojo.getDatestart().compareTo(volunteermatchPojo.getDateend()) == 0) {
                // We have 4 possibilities:
                // 1. pojo.date \in (dateStart, dateEnd) => overlap so false.
                // 2. pojo.date == dateStart || pojo.date + 1 DAY = dateStart => merge
                // 3. pojo.date == dateEnd || pojo.date + 1 DAY = dateEnd => merge
                // 4. none of the above => we do nothing, we can ignore this line.

                if (DateTimeUtil.isBetweenWithoutEndpoints(volunteermatchPojo.getDatestart(), dateStart, dateEnd)) {
                    return false;
                }

                // Merge before
                if (volunteermatchPojo.getDateend().compareTo(dateStart) == 0 ||
                        DateTimeUtil.nrOfDaysInBetween(volunteermatchPojo.getDateend(), dateStart) == 1) {
                    mergeBeforeVolunteerMatchId = volunteermatchPojo.getVolunteermatchid();
                }

                // Merge after
                if (dateEnd != null && (volunteermatchPojo.getDatestart().compareTo(dateEnd) == 0 ||
                        DateTimeUtil.nrOfDaysInBetween(dateEnd, volunteermatchPojo.getDatestart()) == 1)) {
                    mergeAfterVolunteerMatchId = volunteermatchPojo.getVolunteermatchid();
                }

                // In any case we can just go on searching.
                continue;
            }

            if (DateTimeUtil.isBetweenWithoutEndpoints(dateStart,
                    volunteermatchPojo.getDatestart(), volunteermatchPojo.getDateend())) {
                return false;
            }

            if (DateTimeUtil.isBetweenWithoutEndpoints(dateEnd,
                    volunteermatchPojo.getDatestart(), volunteermatchPojo.getDateend())) {
                return false;
            }

            if (DateTimeUtil.isContained(volunteermatchPojo.getDatestart(), volunteermatchPojo.getDateend(),
                    dateStart, dateEnd)) {
                return false;
            }

            // Determine whether we can actually merge with this line.
            // Merge after: (note that if both dates are null, we can never merge.
            if (dateEnd != null && volunteermatchPojo.getDatestart() != null &&
                    (volunteermatchPojo.getDatestart().compareTo(dateEnd) == 0 ||
                            DateTimeUtil.nrOfDaysInBetween(dateEnd, volunteermatchPojo.getDatestart()) == 1)) {
                mergeAfterVolunteerMatchId = volunteermatchPojo.getVolunteermatchid();
            }

            // Merge before: (note that dateEnd must be non-null to merge. Also dateStart is never null).
            if (volunteermatchPojo.getDateend() != null &&
                    (volunteermatchPojo.getDateend().compareTo(dateStart) == 0 ||
                            DateTimeUtil.nrOfDaysInBetween(volunteermatchPojo.getDateend(), dateStart) == 1)) {
                mergeBeforeVolunteerMatchId = volunteermatchPojo.getVolunteermatchid();
            }
        }

        // If we actually reach this point, we know the line will be added to the database (merged or not).
        VolunteermatchPojo volunteermatchPojo = new VolunteermatchPojo();
        volunteermatchPojo.setVolunteerid(volunteerId);
        volunteermatchPojo.setStudentid(studentId);

        // Merge after if possible.
        if (mergeAfterVolunteerMatchId != null) {
            VolunteermatchPojo mergedVolunteermatchPojo = volunteerMatchMyDao.fetchById(volunteerId, mergeAfterVolunteerMatchId);
            volunteermatchPojo.setDateend(mergedVolunteermatchPojo.getDateend());
            volunteerMatchMyDao.delete(mergedVolunteermatchPojo);
        } else {
            // Date start must still be set.
            volunteermatchPojo.setDateend(dateEnd);
        }

        // Merge before if possible.
        if (mergeBeforeVolunteerMatchId != null) {
            VolunteermatchPojo mergedVolunteermatchPojo = volunteerMatchMyDao.fetchById(volunteerId, mergeBeforeVolunteerMatchId);
            volunteermatchPojo.setDatestart(mergedVolunteermatchPojo.getDatestart());
            volunteerMatchMyDao.delete(mergedVolunteermatchPojo);
        } else {
            // Date start must still be set.
            volunteermatchPojo.setDatestart(dateStart);
        }

        // Check that the volunteer is active during this period.
        if (!isVolunteerActiveDuringMatch(context, volunteermatchPojo)) {
            return false;
        }

        if (volunteerMatchId == null) {
            volunteerMatchMyDao.insertPojo(volunteermatchPojo);
        } else {
            volunteermatchPojo.setVolunteermatchid(volunteerMatchId);
            volunteerMatchMyDao.update(volunteermatchPojo);
        }

        return true;
    }

    /**
     * Returns whether the volunteer is active during the [match.dateStart, match.dateEnd].
     * @param context .
     * @param volunteermatchPojo .
     * @return boolean
     */
    private boolean isVolunteerActiveDuringMatch(Context context, VolunteermatchPojo volunteermatchPojo) {
        VolunteerInstanceMyDao volunteerInstanceMyDao = new VolunteerInstanceMyDao(context);
        for (VolunteerinstancePojo volunteerinstancePojo : volunteerInstanceMyDao.getInstanceForVolunteer(volunteermatchPojo.getVolunteerid())) {
            if (DateTimeUtil.isContained(volunteermatchPojo.getDatestart(), volunteermatchPojo.getDateend(),
                    volunteerinstancePojo.getDatestart(), volunteerinstancePojo.getDateend())) {
                return true;
            }
        }

        return false;
    }

}
