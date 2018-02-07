package com.apon.taalmaatjes.backend.api;

import com.apon.taalmaatjes.backend.api.returns.Result;
import com.apon.taalmaatjes.backend.api.returns.VolunteerInstanceReturn;
import com.apon.taalmaatjes.backend.api.returns.mapper.VolunteerInstanceMapper;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.VolunteerinstancePojo;
import com.apon.taalmaatjes.backend.database.jooq.Context;
import com.apon.taalmaatjes.backend.database.mydao.VolunteerInstanceMyDao;
import com.apon.taalmaatjes.backend.database.mydao.VolunteerMyDao;
import com.apon.taalmaatjes.backend.log.Log;
import com.apon.taalmaatjes.backend.util.DateTimeUtil;
import com.apon.taalmaatjes.backend.util.ResultUtil;

import java.sql.Date;
import java.sql.SQLException;

public class VolunteerInstanceAPI {
    private static VolunteerInstanceAPI ourInstance = new VolunteerInstanceAPI();

    public static VolunteerInstanceAPI getInstance() {
        return ourInstance;
    }

    private VolunteerInstanceAPI() { }

    /**
     * Get a volunteer instance based on the external identifiers.
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
        Log.logDebug("End VolunteerAPI.updateVolunteer");
        return ResultUtil.createOk(volunteerInstanceMapper.getVolunteerInstanceReturn());
    }

    /**
     * Add a new volunteer instance.
     * @param volunteerInstanceReturn The instance to add.
     * @return Nothing.
     */
    public Result addVolunteerInstance(VolunteerInstanceReturn volunteerInstanceReturn) {
        if (volunteerInstanceReturn.getDateStart() == null) {
            return ResultUtil.createError("VolunteerAPI.addVolunteerMatch.noDateStart");
        }

        if (volunteerInstanceReturn.getDateEnd() != null &&
                volunteerInstanceReturn.getDateStart().compareTo(volunteerInstanceReturn.getDateEnd()) > 0) {
            return ResultUtil.createError("VolunteerAPI.addVolunteerMatch.dateStartAfterDateEnd");
        }

        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start VolunteerAPI.addVolunteerMatch volunteerExtId " + volunteerInstanceReturn.getVolunteerExtId());

        // Get volunteerId.
        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);
        Integer volunteerId = volunteerMyDao.getIdFromExtId(volunteerInstanceReturn.getVolunteerExtId());
        if (volunteerId == null) {
            return ResultUtil.createError("VolunteerAPI.error.noExtIdFound");
        }

        // Handle the complete adding / merging in another function.
        if (!isVolunteerInstanceValidAndAdd(context, volunteerId, volunteerInstanceReturn.getDateStart(), volunteerInstanceReturn.getDateEnd(), null)) {
            context.rollback();
            return ResultUtil.createError("VolunteerAPI.addVolunteerMatch.invalidInstance");
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
     * @param volunteerInstanceReturn The updated instance.
     * @return Nothing.
     */
    public Result updateVolunteerInstance(VolunteerInstanceReturn volunteerInstanceReturn) {
        if (volunteerInstanceReturn.getDateStart() == null) {
            return ResultUtil.createError("VolunteerAPI.updateVolunteerInstance.noDateStart");
        }

        if (volunteerInstanceReturn.getDateEnd() != null &&
                volunteerInstanceReturn.getDateStart().compareTo(volunteerInstanceReturn.getDateEnd()) > 0) {
            return ResultUtil.createError("VolunteerAPI.updateVolunteerInstance.dateStartAfterDateEnd");
        }

        if (volunteerInstanceReturn.getVolunteerExtId() == null) {
            return ResultUtil.createError("VolunteerAPI.updateVolunteerInstance.noVolunteerExtId");
        }

        if (volunteerInstanceReturn.getExternalIdentifier() == null) {
            return ResultUtil.createError("VolunteerAPI.updateVolunteerInstance.noVolunteerInstanceExtId");
        }

        Context context;
        try {context = new Context();} catch (SQLException e) {
            return ResultUtil.createError("Context.error.create", e);
        }
        Log.logDebug("Start VolunteerAPI.updateVolunteerInstance volunteerExtId " + volunteerInstanceReturn.getVolunteerExtId());

        // Get volunteerId.
        VolunteerMyDao volunteerMyDao = new VolunteerMyDao(context);
        Integer volunteerId = volunteerMyDao.getIdFromExtId(volunteerInstanceReturn.getVolunteerExtId());
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
        if (!isVolunteerInstanceValidAndAdd(context, volunteerId, volunteerInstanceReturn.getDateStart(), volunteerInstanceReturn.getDateEnd(), volunteerInstanceId)) {
            context.rollback();
            return ResultUtil.createError("VolunteerAPI.updateVolunteerInstance.invalidInstance");
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
     * @param dateStart .
     * @param dateEnd .
     * @param volunteerInstanceId If this value is non-null, algorithm assumes it is an updateVolunteer instead of insert.
     * @return boolean
     */
    private boolean isVolunteerInstanceValidAndAdd(Context context, int volunteerId, Date dateStart, Date dateEnd, Integer volunteerInstanceId) {
        VolunteerInstanceMyDao volunteerInstanceMyDao = new VolunteerInstanceMyDao(context);
        // if [A,B] can be merged into [dateStart,dateEnd] with B=dateStart then mergeAfterVolunteerInstanceId will be B.
        Integer mergeAfterVolunteerInstanceId = null;

        // if [dateStart,dateEnd] can be merged into [A,B] with A=dateEnd then mergeAfterVolunteerInstanceId will be A.
        Integer mergeBeforeVolunteerInstanceId = null;
        for (VolunteerinstancePojo volunteerinstancePojo : volunteerInstanceMyDao.getInstanceForVolunteer(volunteerId)) {
            // If we updateVolunteer instead of insert, we want to 'exclude' the to-be-updated volunteerinstance from the search.
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
}
