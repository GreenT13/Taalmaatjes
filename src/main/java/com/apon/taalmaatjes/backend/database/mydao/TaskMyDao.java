package com.apon.taalmaatjes.backend.database.mydao;

import com.apon.taalmaatjes.backend.database.generated.tables.Task;
import com.apon.taalmaatjes.backend.database.generated.tables.Volunteer;
import com.apon.taalmaatjes.backend.database.generated.tables.daos.TaskDao;
import com.apon.taalmaatjes.backend.database.generated.tables.pojos.TaskPojo;
import com.apon.taalmaatjes.backend.database.generated.tables.records.TaskRecord;
import com.apon.taalmaatjes.backend.database.jooq.Context;
import com.apon.taalmaatjes.backend.log.Log;
import org.jooq.SelectWhereStep;
import org.jooq.util.mysql.MySQLDataType;

import java.util.List;

import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.using;

public class TaskMyDao extends TaskDao {
    private final static Integer STARTING_EXT_ID = 8001;

    public TaskMyDao(Context context) {
        super(context.getConfiguration());
    }

    @SuppressWarnings("Duplicates")
    public boolean generateIds(TaskPojo taskPojo) {
        if (taskPojo.getTaskid() == null) {
            Integer maxId = getMaxId();
            taskPojo.setTaskid(maxId != null ? maxId + 1 : 0);
        }

        if (taskPojo.getExternalidentifier() == null) {
            String maxExtId = getMaxExtId();
            if (maxExtId == null) {
                maxExtId = STARTING_EXT_ID.toString();
            } else {
                maxExtId = String.valueOf(Integer.valueOf(maxExtId) + 1);
            }
            taskPojo.setExternalidentifier(maxExtId);
        }

        return true;
    }

    private Integer getMaxId() {
        return using(configuration())
                .select(Task.TASK.TASKID.max())
                .from(Task.TASK)
                .fetchOne(0, Integer.class);
    }

    private String getMaxExtId() {
        return using(configuration())
                .select(Task.TASK.EXTERNALIDENTIFIER.cast(MySQLDataType.INT).max())
                .from(Task.TASK)
                .fetchOne(0, String.class);
    }

    public Integer getIdFromExtId(String externalIdentifier) {
        return using(configuration())
                .select(Task.TASK.TASKID)
                .from(Task.TASK)
                .where(Task.TASK.EXTERNALIDENTIFIER.eq(externalIdentifier))
                .fetchOne(0, Integer.class);
    }

    private boolean fillDefaultValues(TaskPojo taskPojo) {
        if (taskPojo.getIsfinished() == null) {
            taskPojo.setIsfinished(false);
        }

        return true;
    }

    public boolean insertPojo(TaskPojo taskPojo) {
        if (!generateIds(taskPojo)) {
            // Some kind of logError message?
            return false;
        }

        if (!fillDefaultValues(taskPojo)) {
            return false;
        }

        try {
            super.insert(taskPojo);
        } catch (Exception e) {
            Log.logError("Could not insert task", e);
            return false;
        }

        return true;
    }

    public boolean updatePojo(TaskPojo taskPojo) {
        if (!fillDefaultValues(taskPojo)) {
            return false;
        }

        try {
            super.update(taskPojo);
        } catch (Exception e) {
            Log.logError("Could not update task", e);
            return false;
        }

        return true;
    }

    public List<TaskPojo> advancedSearch(String input, Boolean isFinished, String volunteerExtId) {
        SelectWhereStep<TaskRecord> query = using(configuration()).selectFrom(Task.TASK);

        // Search for input in the description and the title.
        if (input != null && input.trim().length() > 0) {
            query.where(Task.TASK.DESCRIPTION.lower().like("%" + input.toLowerCase() + "%"))
            .or(Task.TASK.TITLE.lower().like("%" + input.toLowerCase() + "%"));
        }

        if (isFinished != null) {
            query.where(Task.TASK.ISFINISHED.eq(isFinished));
        }

        if(volunteerExtId != null) {
            query.where(Task.TASK.VOLUNTEERID.eq(
                    select(Volunteer.VOLUNTEER.VOLUNTEERID).from(Volunteer.VOLUNTEER)
                            .where(Volunteer.VOLUNTEER.EXTERNALIDENTIFIER.eq(volunteerExtId))));
        }

        return query.orderBy(Task.TASK.DATETOBEFINISHED.asc()).limit(50).fetch().map(mapper());
    }

    public List<TaskPojo> getTasksForVolunteer(int volunteerId) {
        return using(configuration())
                .selectFrom(Task.TASK)
                .where(Task.TASK.VOLUNTEERID.eq(volunteerId))
                .fetch()
                .map(mapper());
    }
}
